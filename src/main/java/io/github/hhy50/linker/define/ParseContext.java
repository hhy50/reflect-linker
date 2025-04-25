package io.github.hhy50.linker.define;

import io.github.hhy50.linker.annotations.Runtime;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.define.method.ConstructorRef;
import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.define.method.RuntimeMethodRef;
import io.github.hhy50.linker.exceptions.ClassTypeNotMatchException;
import io.github.hhy50.linker.exceptions.ParseException;
import io.github.hhy50.linker.exceptions.VerifyException;
import io.github.hhy50.linker.token.Token;
import io.github.hhy50.linker.token.TokenParser;
import io.github.hhy50.linker.token.Tokens;
import io.github.hhy50.linker.util.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * The type Parse context.
 */
public class ParseContext {
    private static final String FIRST_OBJ_NAME = "target";

    /**
     * The Define class.
     */
    Class<?> defineClass;
    /**
     * The Target class.
     */
    Class<?> targetClass;
    /**
     * 解析过程需要用到的ClassLoader
     */
    ClassLoader classLoader;

    /**
     * 第一个字段
     */
    FieldRef targetRoot;

    /**
     * 解析过程中的字段
     */
    final Map<String, FieldRef> parsedFields = new HashMap<>();
    /**
     * 解析过程中指定的字段类型
     */
    final Map<String, String> typedFields = new HashMap<>();
    /**
     * 解析过程中指定的静态字段
     */
    final Map<String, Boolean> staticFields = new HashMap<>();

    /**
     * The Token parser.
     */
    TokenParser tokenParser = new TokenParser();

    /**
     * Instantiates a new Parse context.
     *
     * @param defineClass the define class
     * @param targetClass the target class
     */
    ParseContext(Class<?> defineClass, Class<?> targetClass) {
        this.defineClass = defineClass;
        this.targetClass = targetClass;
    }

    /**
     *
     */
    void preParse(Method method) {
        Map<String, String> typeDefines = ClassUtil.getTypeDefines(method);
        Map<String, Boolean> designateStaticFields = AnnotationUtils.getDesignateStaticFields(method);
    }

    /**
     * Sets class loader.
     *
     * @param classLoader the class loader
     */
    void setClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        this.classLoader = classLoader;
    }

    /**
     * Parse list.
     *
     * @return the list
     * @throws ClassNotFoundException the class not found exception
     * @throws ParseException         the parse exception
     */
    public List<MethodDefine> parse() throws ClassNotFoundException, ParseException {
        return doParseClass();
    }

    private List<MethodDefine> doParseClass() throws ClassNotFoundException, ParseException {
        List<MethodDefine> methodDefines = new ArrayList<>();
        for (Method method : this.defineClass.getMethods()) {
            if (!Modifier.isAbstract(method.getModifiers())) continue;
            preParse(method);
            methodDefines.add(parseMethod(method));
        }
        return methodDefines;
    }

    /**
     * Parse method method define.
     *
     * @param method the method
     * @return the method define
     * @throws ClassNotFoundException the class not found exception
     */
    public MethodDefine parseMethod(Method method) throws VerifyException, ClassNotFoundException {
        verify(method);

        String fieldExpr = null;
        io.github.hhy50.linker.annotations.Field.Getter getter = method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Field.Getter.class);
        io.github.hhy50.linker.annotations.Field.Setter setter = method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Field.Setter.class);
        if (getter != null) {
            fieldExpr = Util.getOrElseDefault(getter.value(), method.getName());
        } else if (setter != null) {
            fieldExpr = Util.getOrElseDefault(setter.value(), method.getName());
        }

        MethodDefine methodDefine = new MethodDefine(method);
        if (fieldExpr != null) {
            Tokens tokens = tokenParser.parse(fieldExpr);
            methodDefine.fieldRef = parseFieldExpr(tokens);
        } else if (methodDefine.hasConstructor()) {
            Class<?> instanceType = ((EarlyFieldRef) targetRoot).getClassType();
            String[] argsType = parseArgsType(method);
            Constructor<?> constructor = ReflectUtil.matchConstructor(instanceType, argsType);
            if (constructor == null) {
                throw new ParseException("Constructor not found in class '"+instanceType+"' with args "+Arrays.toString(argsType));
            }
            methodDefine.methodRef = new ConstructorRef(targetRoot, method.getName(), constructor);
        } else {
            io.github.hhy50.linker.annotations.Method.Name methodNameAnn = method.getAnnotation(io.github.hhy50.linker.annotations.Method.Name.class);
            String methodName = Util.getOrElseDefault(methodNameAnn == null ? null : methodNameAnn.value(), methodDefine.getName());
            int i;
            if ((i = methodName.lastIndexOf('.')) != -1) {
                fieldExpr = methodName.substring(0, i);
                methodName = methodName.substring(i+1);
            }
            methodDefine.methodRef = parseMethodExpr(methodName, method, tokenParser.parse(fieldExpr));
        }
        return methodDefine;
    }

    private MethodRef parseMethodExpr(String methodName, Method methodDefine, final Tokens fieldTokens) throws ClassNotFoundException {
        Map<String, Boolean> staticTokens = AnnotationUtils.getDesignateStaticFields(methodDefine, methodName);
        FieldRef owner = parseFieldExpr(fieldTokens);

        String[] argsType = parseArgsType(methodDefine);
        Class<?> returnClass = methodDefine.getReturnType();
        io.github.hhy50.linker.annotations.Method.InvokeSuper invokeSuperAnno = methodDefine.getAnnotation(io.github.hhy50.linker.annotations.Method.InvokeSuper.class);
        String superClass = invokeSuperAnno != null ? invokeSuperAnno.value() : null;
        MethodRef methodRef = null;
        if (owner instanceof EarlyFieldRef) {
            Class<?> ownerClass = ((EarlyFieldRef) owner).getClassType();
            Method method = ReflectUtil.matchMethod(ownerClass, methodName, superClass, argsType);
            if (method == null && this.typedFields.containsKey(owner.getFullName())) {
                throw new ParseException("can not find method "+methodName+" in class "+ownerClass.getName());
            }
            methodRef = method == null ? null : new EarlyMethodRef(owner, method);
        }
        if (methodRef == null) {
            methodRef = new RuntimeMethodRef(owner, methodName, argsType, returnClass);
            if (staticTokens.containsKey(methodName)) {
                ((RuntimeMethodRef) methodRef).designateStatic(staticTokens.get(methodName));
            }
        }
        if (superClass != null) {
            methodRef.setSuperClass(superClass);
        }
        return methodRef;
    }

    private FieldRef parseFieldExpr(final Tokens tokens) throws ClassNotFoundException {
        Class<?> currentType = targetRoot instanceof EarlyFieldRef ? ((EarlyFieldRef) targetRoot).getClassType() : null;
        FieldRef lastField = targetRoot;
        String fullField = null;
        for (Token token : tokens) {
            Field earlyField = currentType == null ? null : token.getField(currentType);
            currentType = earlyField == null ? null : earlyField.getType();
            fullField = Optional.ofNullable(fullField).map(i -> i+"."+token.value()).orElseGet(token::value);
            if (!this.parsedFields.containsKey(fullField)) {
                // 使用@Typed指定的类型
                Class<?> assignedType = getFieldTyped(fullField, token.value());
                if (assignedType != null) {
                    if (earlyField != null && !ClassUtil.isAssignableFrom(assignedType, earlyField.getType())) {
                        throw new ClassTypeNotMatchException(assignedType.getName(), earlyField.getType().getName());
                    }
                    currentType = assignedType;
                }
                lastField = earlyField != null ? new EarlyFieldRef(lastField, earlyField, assignedType)
                        : new RuntimeFieldRef(lastField, lastField.fieldName, token.value());
                lastField.setFullName(fullField);
                designateStatic(lastField);
                this.parsedFields.put(fullField, lastField);
            } else {
                lastField = this.parsedFields.get(fullField);
            }
        }
        return lastField;
    }

    private static String[] parseArgsType(Method method) {
        return Arrays.stream(method.getParameters())
                .map(item -> {
                    String typed = AnnotationUtils.getTyped(item);
                    if (StringUtil.isNotEmpty(typed)) {
                        return typed;
                    }
                    Class<?> type = item.getType();
                    String bind = AnnotationUtils.getBind(type);
                    if (StringUtil.isNotEmpty(bind)) {
                        return bind;
                    }
                    return type.isArray() ? type.getCanonicalName() : type.getName();
                }).toArray(String[]::new);
    }

    private Class<?> getFieldTyped(String fullField, String tokenValue) throws ClassNotFoundException {
        if (this.typedFields.containsKey(fullField)) {
            return this.classLoader.loadClass(this.typedFields.get(fullField));
        }
        if (this.typedFields.containsKey(tokenValue)) {
            return this.classLoader.loadClass(this.typedFields.get(tokenValue));
        }
        return null;
    }

    private void designateStatic(FieldRef fieldRef) {
        if (fieldRef instanceof RuntimeFieldRef) {
            if (staticFields.containsKey(fieldRef.getFullName())) {
                ((RuntimeFieldRef) fieldRef).designateStatic(staticFields.get(fieldRef.getFullName()));
            } else if (staticFields.containsKey(fieldRef.fieldName)) {
                ((RuntimeFieldRef) fieldRef).designateStatic(staticFields.get(fieldRef.fieldName));
            }
        }
    }

    private static void verify(Method method) throws VerifyException {
        io.github.hhy50.linker.annotations.Field.Getter getter = method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Field.Getter.class);
        io.github.hhy50.linker.annotations.Field.Setter setter = method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Field.Setter.class);
        io.github.hhy50.linker.annotations.Method.Constructor constructor = method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Method.Constructor.class);
        // Field.Setter和@Field.Getter只能有一个
        if (getter != null && setter != null) {
            throw new VerifyException("method ["+method.getDeclaringClass()+"@"+method.getName()+"] cannot have two annotations @Field.getter and @Field.setter");
        }
        if (getter != null) {
            if (StringUtil.isEmpty(getter.value())) {
                throw new VerifyException("method ["+method.getDeclaringClass()+"@"+method.getName()+"] is getter method, must be specified field-expression");
            }
            if (method.getReturnType() == void.class || method.getParameters().length > 0) {
                throw new VerifyException("method ["+method.getDeclaringClass()+"@"+method.getName()+"] is getter method,its return value cannot be of type void, and the parameter length must be 0");
            }
        } else if (setter != null) {
            if (StringUtil.isEmpty(setter.value())) {
                throw new VerifyException("method ["+method.getDeclaringClass()+"@"+method.getName()+"] is getter method, must be specified field-expression");
            }
            if (method.getReturnType() != void.class || method.getParameters().length != 1) {
                throw new VerifyException("method ["+method.getDeclaringClass()+"@"+method.getName()+"] is setter method,its return value must be of type void, and the parameter length must be 1");
            }
        } else if (constructor != null) {
            boolean runtime = method.getDeclaringClass().getDeclaredAnnotation(Runtime.class) != null;
            if (runtime)
                throw new VerifyException("method ["+method.getDeclaringClass()+"@"+method.getName()+"], @Constructor cannot be used with @Runtime");
            if (method.getReturnType() != method.getDeclaringClass()) {
                throw new VerifyException("method ["+method.getDeclaringClass()+"@"+method.getName()+"], return type must be this class");
            }
        }

        io.github.hhy50.linker.annotations.Method.Name methodNameAnn = method.getAnnotation(io.github.hhy50.linker.annotations.Method.Name.class);
        // @Field 和 @Method相关的注解不能同时存在
        if ((getter != null || setter != null) & (methodNameAnn != null)) {
            throw new VerifyException("method ["+method.getDeclaringClass()+"@"+method.getName()+"], @Method.Name and @Field.Setter|@Field.Getter only one can exist");
        }
    }
}