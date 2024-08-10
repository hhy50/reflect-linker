package io.github.hhy.linker.define;

import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.exceptions.ParseException;
import io.github.hhy.linker.exceptions.VerifyException;
import io.github.hhy.linker.token.Token;
import io.github.hhy.linker.token.TokenParser;
import io.github.hhy.linker.token.Tokens;
import io.github.hhy.linker.util.ClassUtil;
import io.github.hhy.linker.util.Util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClassDefineParse {

    private static final TokenParser TOKEN_PARSER = new TokenParser();

    public static <T> InvokeClassDefine parseClass(Class<T> define, Class<?> bindClass) throws ParseException {
        return doParseClass(define, bindClass.getName());
    }

    public static <T> InvokeClassDefine doParseClass(Class<T> define, String bindClass) throws ParseException {
        io.github.hhy.linker.annotations.Target.Bind annotation = define.getDeclaredAnnotation(Target.Bind.class);
        if (annotation == null || annotation.value().equals("")) {
            throw new VerifyException("use @Target.Bind specified a class");
        } else if (ClassUtil.isAssignableFrom(bindClass, annotation.value())) {
            throw new VerifyException("@Target.Bind specified target "+annotation.value()+", but used another target class ["+bindClass+"]");
        }
        List<MethodDefine> methodDefines = new ArrayList<>();
        for (Method declaredMethod : define.getDeclaredMethods()) {
            methodDefines.add(parseMethod(bindClass, declaredMethod, TOKEN_PARSER));
        }
        InvokeClassDefine classDefine = new InvokeClassDefine();
        classDefine.define = define;
        classDefine.bindClass = bindClass;
        classDefine.methodDefines = methodDefines;
        return classDefine;
    }

    public static MethodDefine parseMethod(String bindClass, Method method, TokenParser tokenParser) {
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
            Tokens tokens = tokenParser.parse(fieldExpr);
            methodDefine.targetPoint = parseFieldExpr(bindClass, tokens);
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
     * 解析字段 （运行时）
     *
     * @param first
     * @param tokens
     * @return
     */
    private static RuntimeField parseFieldExpr(final String first, final Tokens tokens) {
        RuntimeField field = RuntimeField.TARGET;
        for (Token token : tokens) {
            field = new RuntimeField(field, token.value());
        }
        return field;
    }
}
