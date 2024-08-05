package io.github.hhy.linker.define;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.enums.TargetPointType;
import io.github.hhy.linker.exceptions.ParseException;
import io.github.hhy.linker.exceptions.VerifyException;
import io.github.hhy.linker.token.Token;
import io.github.hhy.linker.token.TokenParser;
import io.github.hhy.linker.token.Tokens;
import io.github.hhy.linker.util.ClassUtil;
import io.github.hhy.linker.util.ReflectUtil;
import io.github.hhy.linker.util.Util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassDefineParse {

    private static final TokenParser TOKEN_PARSER = new TokenParser();

    public static <T> InvokeClassDefine parseClass(Class<T> define, Class<?> targetClass) throws ParseException {
        return doParseClass(define, targetClass.getName());
    }

    public static <T> InvokeClassDefine doParseClass(Class<T> define, String targetClass) throws ParseException {
        io.github.hhy.linker.annotations.Target.Bind annotation = define.getDeclaredAnnotation(Target.Bind.class);
        if (annotation == null || annotation.value().equals("")) {
            throw new VerifyException("use @Target.Bind specified a class");
        } else if (ClassUtil.isAssignableFrom(targetClass, annotation.value())) {
            throw new VerifyException("@Target.Bind specified target "+annotation.value()+", but used another target class ["+targetClass+"]");
        }
        List<MethodDefine> methodDefines = new ArrayList<>();
        for (Method declaredMethod : define.getDeclaredMethods()) {
            methodDefines.add(parseMethod(targetClass, declaredMethod, TOKEN_PARSER));
        }
        InvokeClassDefine classDefine = new InvokeClassDefine();
        classDefine.define = define;
        classDefine.targetClass = targetClass;
        classDefine.methodDefines = methodDefines;
        return classDefine;
    }

    public static MethodDefine parseMethod(Class<?> targetClass, Method method, TokenParser tokenParser) {
        verify(method);

        String fieldExpr = null;
        Field.Getter getter = method.getDeclaredAnnotation(Field.Getter.class);
        Field.Setter setter = method.getDeclaredAnnotation(Field.Setter.class);
        if (getter != null) {
            fieldExpr = Util.getOrElseDefault(getter.value(), method.getName());
        } else if (setter != null) {
            fieldExpr = Util.getOrElseDefault(setter.value(), method.getName());
        }

        MethodDefine methodDefine = new MethodDefine(method);
        if (fieldExpr != null) {
            Tokens tokens = tokenParser.parse(fieldExpr);
            methodDefine.targetPoint = parseTargetField(targetClass, tokens);
            methodDefine.targetPointType = setter != null ? TargetPointType.SETTER : TargetPointType.GETTER;
        } else {
            io.github.hhy.linker.annotations.Method.Name methodNameAnn
                    = method.getAnnotation(io.github.hhy.linker.annotations.Method.Name.class);
            io.github.hhy.linker.annotations.Method.InvokeSuper superClass
                    = method.getAnnotation(io.github.hhy.linker.annotations.Method.InvokeSuper.class);
            io.github.hhy.linker.annotations.Method.DynamicSign dynamicSign = method
                    .getAnnotation(io.github.hhy.linker.annotations.Method.DynamicSign.class);
            // 校验方法是否存在
            String methodName = Util.getOrElseDefault(methodNameAnn == null ? null : methodNameAnn.value(), method.getName());
            java.lang.reflect.Method rMethod = ReflectUtil.getDeclaredMethod(targetClass, methodName);

            methodDefine.targetPoint = new TargetMethod(rMethod);
            methodDefine.targetPointType = setter != null ? TargetPointType.SETTER : TargetPointType.GETTER;
        }
        return methodDefine;
    }

    public static MethodDefine parseMethod(String targetClass, Method method, TokenParser tokenParser) {
        verify(method);

        String fieldExpr = null;
        Field.Getter getter = method.getDeclaredAnnotation(Field.Getter.class);
        Field.Setter setter = method.getDeclaredAnnotation(Field.Setter.class);
        if (getter != null) {
            fieldExpr = Util.getOrElseDefault(getter.value(), method.getName());
        } else if (setter != null) {
            fieldExpr = Util.getOrElseDefault(setter.value(), method.getName());
        }

        MethodDefine methodDefine = new MethodDefine(method);
        if (fieldExpr != null) {
            Tokens tokens = tokenParser.parse(fieldExpr);
            methodDefine.targetPoint = parseTargetField(targetClass, tokens);
            methodDefine.targetPointType = setter != null ? TargetPointType.SETTER : TargetPointType.GETTER;
        } else {
            io.github.hhy.linker.annotations.Method.Name methodNameAnn
                    = method.getAnnotation(io.github.hhy.linker.annotations.Method.Name.class);
            io.github.hhy.linker.annotations.Method.InvokeSuper superClass
                    = method.getAnnotation(io.github.hhy.linker.annotations.Method.InvokeSuper.class);
            io.github.hhy.linker.annotations.Method.DynamicSign dynamicSign = method
                    .getAnnotation(io.github.hhy.linker.annotations.Method.DynamicSign.class);
            // 校验方法是否存在
            String methodName = Util.getOrElseDefault(methodNameAnn == null ? null : methodNameAnn.value(), method.getName());
//            java.lang.reflect.Method rMethod = ReflectUtil.getDeclaredMethod(targetClass, methodName);
            methodDefine.targetPoint = new TargetMethod(methodName);
            methodDefine.targetPointType = setter != null ? TargetPointType.SETTER : TargetPointType.GETTER;
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


    /**
     * 解析目标字段
     *
     * @param tokens
     */
    private static TargetField parseTargetField(final Class<?> first, final Tokens tokens) {
        Class<?> pos = first;
        TargetField targetField = null;
        for (Token token : tokens) {
            if (!(targetField instanceof RuntimeField)) {
                java.lang.reflect.Field field = token.getField(pos);
                if (field != null) {
                    if (token.arrayExpr() && !field.getType().isArray()) {
                        throw new VerifyException(" ,field "+pos.getDeclaringClass()+"."+pos.getName()+", not an array type");
                    }
                    if (token.mapExpr() && field.getType().isAssignableFrom(Map.class)) {
                        throw new VerifyException(" ,field "+pos.getDeclaringClass()+"."+pos.getName()+" not an array type");
                    }
                    targetField = new TargetField(targetField, field);
                    pos = field.getType();
                    continue;
                }
            }
            targetField = new RuntimeField(targetField, token.value());
            pos = Object.class;
        }
        return targetField;
    }

    /**
     * 解析字段 （运行时）
     *
     * @param first
     * @param tokens
     * @return
     */
    private static TargetField parseTargetField(final String first, final Tokens tokens) {
        TargetField targetField = null;
        for (Token token : tokens) {
            targetField = new RuntimeField(targetField, token.value());
        }
        return targetField;
    }
}
