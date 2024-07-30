package io.github.hhy.linker.define;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.bytecode.BytecodeGenerator;
import io.github.hhy.linker.bytecode.GetterBytecodeGenerator;
import io.github.hhy.linker.bytecode.InvokeBytecodeGenerator;
import io.github.hhy.linker.bytecode.SetterBytecodeGenerator;
import io.github.hhy.linker.exceptions.VerifyException;
import io.github.hhy.linker.util.ReflectUtil;
import io.github.hhy.linker.util.Util;
import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Data
public class MethodDefine {
    private Method method;
    private Target target;
    private BytecodeGenerator bytecodeGenerator;

    public MethodDefine(Method method) {
        this.method = method;
    }

    public static MethodDefine parseMethod(Class<?> targetClass, Method method) {
        verify(targetClass, method);

        String fieldName = null;
        Class<?> fieldType = null;
        Field.Getter getter = method.getDeclaredAnnotation(Field.Getter.class);
        Field.Setter setter = method.getDeclaredAnnotation(Field.Setter.class);
        if (getter != null) {
            fieldName = Util.getOrElseDefault(getter.value(), method.getName());
            fieldType = method.getReturnType();
        } else if (setter != null) {
            fieldName = Util.getOrElseDefault(setter.value(), method.getName());
            fieldType = method.getParameterTypes()[0];
        }

        Target target = null;
        if (fieldName != null) {
            java.lang.reflect.Field field = ReflectUtil.getDeclaredField(targetClass, fieldName);
            if (field == null || !fieldType.isAssignableFrom(field.getType())) {
                throw new VerifyException("class ["+method.getDeclaringClass()+"@"+method.getName()+"], not found field "+fieldName+" in "+targetClass.getName());
            }
            // 无法为final字段生成setter
            if (setter != null && (field.getModifiers() & Modifier.FINAL) > 0) {
                throw new VerifyException("class ["+method.getDeclaringClass()+"@"+method.getName()+"], unable to generate setter for final field '"+fieldName+"'");
            }
            target = setter != null ? Target$Field.createSetter(field) : Target$Field.createGetter(field);
        } else {
            io.github.hhy.linker.annotations.Method.Name methodNameAnn
                    = method.getAnnotation(io.github.hhy.linker.annotations.Method.Name.class);
            io.github.hhy.linker.annotations.Method.InvokeSuper superClass
                    = method.getAnnotation(io.github.hhy.linker.annotations.Method.InvokeSuper.class);
            io.github.hhy.linker.annotations.Method.DynamicSign dynamicSign = method
                    .getAnnotation(io.github.hhy.linker.annotations.Method.DynamicSign.class);
            // 校验方法是否存在
            String methodName = Util.getOrElseDefault(methodNameAnn == null ? null : methodNameAnn.value(), method.getName());
        }

        MethodDefine methodDefine = new MethodDefine(method);
        methodDefine.target = target;
        methodDefine.bytecodeGenerator = findGenerator(targetClass, methodDefine);
        return methodDefine;
    }


    /**
     * 校验规则
     * 1. 无法为final字段生成setter
     * 2. @Field.Setter和@Field.Getter只能有一个
     * 3. @Field.Setter和@Field.Getter和@Method.Name只能有一个
     * 4.
     *
     * @param targetClass
     * @param method
     */
    private static void verify(Class<?> targetClass, Method method) {
        Field.Getter getter = method.getDeclaredAnnotation(Field.Getter.class);
        Field.Setter setter = method.getDeclaredAnnotation(Field.Setter.class);
        // Field.Setter和@Field.Getter只能有一个
        if (getter != null && setter != null) {
            throw new VerifyException("class ["+method.getDeclaringClass()+"@"+method.getName()+"] cannot have two annotations @Field.getter and @Field.setter");
        }
        if (getter != null && (method.getReturnType() == void.class || method.getParameters().length > 0)) {
            throw new VerifyException("class ["+method.getDeclaringClass()+"@"+method.getName()+"] is getter method,its return value cannot be of type void, and the parameter length must be 0");
        } else if (setter != null && (method.getReturnType() != void.class || method.getParameters().length != 1)) {
            throw new VerifyException("class ["+method.getDeclaringClass()+"@"+method.getName()+"] is setter method,its return value must be of type void, and the parameter length must be 1");
        }

        io.github.hhy.linker.annotations.Method.Name methodNameAnn
                = method.getAnnotation(io.github.hhy.linker.annotations.Method.Name.class);
        // @Field 和 @Method相关的注解不能同时存在
        if ((getter != null || setter != null) & (methodNameAnn != null)) {
            throw new VerifyException("class ["+method.getDeclaringClass()+"@"+method.getName()+"], @Method.Name and @Field.Setter|@Field.Getter only one can exist");
        }
    }

    private static BytecodeGenerator findGenerator(Class<?> targetClass, MethodDefine methodDefine) {
        Target target = methodDefine.target;
        if (target instanceof Target$Field.Getter) {
            return new GetterBytecodeGenerator((Target$Field.Getter) target);
        } else if (target instanceof Target$Field.Setter) {
            return new SetterBytecodeGenerator((Target$Field.Setter) target);
        } else {
            return new InvokeBytecodeGenerator(targetClass, null);
        }
    }
}
