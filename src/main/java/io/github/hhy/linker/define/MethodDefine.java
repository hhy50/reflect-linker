package io.github.hhy.linker.define;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.bytecode.BytecodeGenerator;
import io.github.hhy.linker.bytecode.GetterBytecodeGenerator;
import io.github.hhy.linker.bytecode.InvokeBytecodeGenerator;
import io.github.hhy.linker.bytecode.SetterBytecodeGenerator;
import io.github.hhy.linker.exceptions.VerifyException;
import io.github.hhy.linker.util.Util;
import lombok.Data;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

@Data
public class MethodDefine {
    private Method method;
    private String methodName;
    private String methodDesc;
    private BytecodeGenerator bytecodeGenerator;

    public MethodDefine(Method method) {
        this.method = method;
        this.methodName = method.getName();
        this.methodDesc = Type.getMethodDescriptor(method);
    }

    public static MethodDefine parseMethod(Class<?> targetClass, Method method) {
        verify(method);

        MethodDefine methodDefine = new MethodDefine(method);
        methodDefine.bytecodeGenerator = findGenerator(targetClass, methodDefine, method);
        return methodDefine;
    }

    private static void verify(Method method) throws VerifyException {
        String fieldName = null;
        Field.Getter getter = method.getDeclaredAnnotation(Field.Getter.class);
        Field.Setter setter = method.getDeclaredAnnotation(Field.Setter.class);
        if (getter != null && setter != null) {
            throw new VerifyException("class ["+method.getDeclaringClass()+method.getName()+"] cannot have two annotations @Field.getter and @Field.setter");
        } else if (getter != null) {
            fieldName = Util.getOrElseDefault(getter.value(), method.getName());
        } else if (setter != null) {
            fieldName = Util.getOrElseDefault(setter.value(), method.getName());
        }
        if (getter != null && (method.getReturnType() == void.class || method.getParameters().length > 0)) {
            throw new VerifyException("class ["+method.getDeclaringClass()+method.getName()+"] is getter method,its return value cannot be of type void, and the parameter length must be 0");
        } else if (setter != null && (method.getReturnType() != void.class || method.getParameters().length != 1)) {
            throw new VerifyException("class ["+method.getDeclaringClass()+method.getName()+"] is setter method,its return value must be of type void, and the parameter length must be 1");
        }

        io.github.hhy.linker.annotations.Method.Name methodNameAnn
                = method.getAnnotation(io.github.hhy.linker.annotations.Method.Name.class);
        if (fieldName != null) {
            // @Field 和 @Method相关的注解不能同时存在
            if (methodNameAnn != null)
                throw new VerifyException("class ["+method.getDeclaringClass()+method.getName()+"], @Method.Name and @Field.Setter|@Field.Getter only one can exist");
            // 校验字段是否存在

        } else {
            io.github.hhy.linker.annotations.Method.InvokeSuper superClass
                    = method.getAnnotation(io.github.hhy.linker.annotations.Method.InvokeSuper.class);
            io.github.hhy.linker.annotations.Method.DynamicSign dynamicSign = method
                    .getAnnotation(io.github.hhy.linker.annotations.Method.DynamicSign.class);

            // 校验方法是否存在
            String methodName = Util.getOrElseDefault(methodNameAnn == null ? null : methodNameAnn.value(), method.getName());
        }
    }

    private static BytecodeGenerator findGenerator(Class<?> targetClass, MethodDefine methodDefine, Method method) {
        Field.Getter getter = method.getAnnotation(Field.Getter.class);
        if (getter != null) {
            String fieldName = Util.getOrElseDefault(getter.value(), method.getName());
            return new GetterBytecodeGenerator(targetClass, fieldName, method.getReturnType());
        }

        Field.Setter setter = method.getAnnotation(Field.Setter.class);
        if (setter != null) {
            String fieldName = Util.getOrElseDefault(setter.value(), method.getName());
            return new SetterBytecodeGenerator(targetClass, fieldName, method.getParameterTypes()[0]);
        }
        return new InvokeBytecodeGenerator(targetClass, method);
    }
}
