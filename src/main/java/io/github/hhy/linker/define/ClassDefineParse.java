package io.github.hhy.linker.define;

import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.annotations.Typed;
import io.github.hhy.linker.define.field.EarlyFieldRef;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.define.field.RuntimeFieldRef;
import io.github.hhy.linker.define.field.VulnerableFieldRef;
import io.github.hhy.linker.exceptions.ParseException;
import io.github.hhy.linker.exceptions.VerifyException;
import io.github.hhy.linker.token.Token;
import io.github.hhy.linker.token.TokenParser;
import io.github.hhy.linker.token.Tokens;
import io.github.hhy.linker.util.ClassUtil;
import io.github.hhy.linker.util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassDefineParse {

    private static final TokenParser TOKEN_PARSER = new TokenParser();

    public static InvokeClassDefine parseClass(Class<?> define, Class<?> bindClass) throws ParseException {
        return doParseClass(define, bindClass);
    }

    public static InvokeClassDefine doParseClass(Class<?> define, Class<?> bindClass) throws ParseException {
        io.github.hhy.linker.annotations.Target.Bind annotation = define.getDeclaredAnnotation(Target.Bind.class);
        if (annotation == null || annotation.value().equals("")) {
            throw new VerifyException("use @Target.Bind specified a class");
        } else if (ClassUtil.isAssignableFrom(bindClass, annotation.value())) {
            throw new VerifyException("@Target.Bind specified target "+annotation.value()+", but used another target class ["+bindClass+"]");
        }

        Map<String, String> typeDefines = getTypeDefines(define);
        EarlyFieldRef targetFieldRef = new EarlyFieldRef(null, "target", bindClass);
        List<MethodDefine> methodDefines = new ArrayList<>();
        for (Method declaredMethod : define.getDeclaredMethods()) {
            methodDefines.add(parseMethod(declaredMethod, targetFieldRef, typeDefines));
        }

        InvokeClassDefine classDefine = new InvokeClassDefine();
        classDefine.define = define;
        classDefine.bindClass = bindClass.getName();
        classDefine.methodDefines = methodDefines;
        return classDefine;
    }

    /**
     * 从接口类上面获取提前定义好的类型
     *
     * @param define
     * @param <T>
     * @return
     */
    private static <T> Map<String, String> getTypeDefines(Class<T> define) {
        Typed[] declaredAnnotations = define.getDeclaredAnnotationsByType(Typed.class);
        return Arrays.stream(declaredAnnotations).collect(Collectors.toMap(Typed::name, Typed::type));
    }

    /**
     * @param method
     * @param targetFieldRef
     * @param typedDefines
     * @return
     */
    public static MethodDefine parseMethod(Method method, EarlyFieldRef targetFieldRef, Map<String, String> typedDefines) {
        verify(method);

        String fieldExpr = null;
        io.github.hhy.linker.annotations.Field.Getter getter = method.getDeclaredAnnotation(io.github.hhy.linker.annotations.Field.Getter.class);
        io.github.hhy.linker.annotations.Field.Setter setter = method.getDeclaredAnnotation(io.github.hhy.linker.annotations.Field.Setter.class);
        if (getter != null) {
            fieldExpr = Util.getOrElseDefault(getter.value(), method.getName());
        } else if (setter != null) {
            fieldExpr = Util.getOrElseDefault(setter.value(), method.getName());
        }

        MethodDefine methodDefine = new MethodDefine(method);
        if (fieldExpr != null) {
            Tokens tokens = TOKEN_PARSER.parse(fieldExpr);
            methodDefine.fieldRef = parseFieldExpr(targetFieldRef, tokens, typedDefines);
        } else {
            io.github.hhy.linker.annotations.Method.Name methodNameAnn
                    = method.getAnnotation(io.github.hhy.linker.annotations.Method.Name.class);
            io.github.hhy.linker.annotations.Method.InvokeSuper superClass
                    = method.getAnnotation(io.github.hhy.linker.annotations.Method.InvokeSuper.class);
            io.github.hhy.linker.annotations.Method.DynamicSign dynamicSign = method
                    .getAnnotation(io.github.hhy.linker.annotations.Method.DynamicSign.class);
            // 校验方法是否存在
            String methodName = Util.getOrElseDefault(methodNameAnn == null ? null : methodNameAnn.value(), method.getName());
//            java.lang.reflect.Method rMethod = ReflectUtil.getDeclaredMethod(bindClass, methodName);
            methodDefine.targetPoint = new RuntimeMethod(methodName);
        }
        return methodDefine;
    }

    /**
     * 校验规则
     * <p>1. 无法为final字段生成setter</p>
     * <p>2. @Field.Setter和@Field.Getter只能有一个</p>
     * <p>3. @Field.Setter和@Field.Getter和@Method.Name只能有一个</p>
     * <p>4.</p>
     *
     * @param method
     * @param method
     */
    private static void verify(Method method) {
        io.github.hhy.linker.annotations.Field.Getter getter = method.getDeclaredAnnotation(io.github.hhy.linker.annotations.Field.Getter.class);
        io.github.hhy.linker.annotations.Field.Setter setter = method.getDeclaredAnnotation(io.github.hhy.linker.annotations.Field.Setter.class);
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

    /**
     * 解析目标字段
     *
     * @param targetFieldRef
     * @param tokens
     * @param typedDefines
     * @return
     */
    private static FieldRef parseFieldExpr(final EarlyFieldRef targetFieldRef, final Tokens tokens, Map<String, String> typedDefines) {
        Class<?> currentType = targetFieldRef.getType();
        FieldRef lastField = targetFieldRef;

        String curField = null;
        for (Token token : tokens) {
            curField = curField == null ? token.value() : (curField+"."+token.value());
            if (lastField instanceof RuntimeFieldRef) {
                lastField = new RuntimeFieldRef(lastField, token.value());
                continue;
            }
            String type = typedDefines.get(curField);
            if (type == null) {
                type = typedDefines.get(token.value());
            }
            if (type != null) {
                lastField = new EarlyFieldRef(lastField, token.value(), type);
            } else {
                Field field = token.getField(currentType);
                if (field == null) {
                    lastField = new RuntimeFieldRef(lastField, token.value());
                    continue;
                }
                boolean isFinal = Modifier.isFinal(field.getType().getModifiers());
                lastField = isFinal ? new EarlyFieldRef(lastField, field) : new VulnerableFieldRef(lastField, field);
            }
        }
        return lastField;
    }
}
