package io.github.hhy50.linker.define;

import io.github.hhy50.linker.annotations.Runtime;
import io.github.hhy50.linker.annotations.Target;
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
import io.github.hhy50.linker.generate.ClassImplGenerator;
import io.github.hhy50.linker.token.Token;
import io.github.hhy50.linker.token.TokenParser;
import io.github.hhy50.linker.token.Tokens;
import io.github.hhy50.linker.util.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static io.github.hhy50.linker.util.AnnotationUtils.isRuntime;
import static io.github.hhy50.linker.util.ClassUtil.getTypeDefines;

/**
 * The type Class define parse.
 */
public class ClassDefineParse {

    private static final Map<String, Map<String, InterfaceImplClassDefine>> PARSED = new HashMap<>();
    private static final String FIRST_OBJ_NAME = "target";
    private static final TokenParser TOKEN_PARSER = new TokenParser();

    /**
     * Parse class interface impl class define.
     *
     * @param define      the define
     * @param targetClass the target class
     * @param cl          the cl
     * @return interface impl class define
     * @throws ParseException         the parse exception
     * @throws ClassNotFoundException the class not found exception
     * @throws IOException            the io exception
     */
    public static InterfaceImplClassDefine parseClass(Class<?> define, Class<?> targetClass, ClassLoader cl) throws ParseException,
            ClassNotFoundException, IOException {
        if (isRuntime(define)) {
            targetClass = Object.class;
        } else if (targetClass == null) {
            Target.Bind bindAnno = define.getDeclaredAnnotation(Target.Bind.class);
            if (bindAnno == null) {
                throw new VerifyException("use @Target.Bind specified a class  \n"+
                        "                  or use @Runtime designated as runtime");
            }
            targetClass = cl.loadClass(bindAnno.value());
        }

        String dynKey = targetClass == Object.class ? "runtime" : targetClass.getName().replace('.', '_');
        InterfaceImplClassDefine defineClass = getCache(define, dynKey);
        if (defineClass != null) {
            return defineClass;
        }

        defineClass = doParseClass(define, targetClass, cl);
        defineClass.setClassName(define.getName()+"$"+dynKey);

        ClassImplGenerator.generateBytecode(defineClass, cl);
        putCache(define, dynKey, defineClass);
        return defineClass;
    }

    private static InterfaceImplClassDefine getCache(Class<?> define, String dynKey) {
        Map<String, InterfaceImplClassDefine> parsed = PARSED.computeIfAbsent(define.getName(), k -> new HashMap<>());
        return parsed.get(dynKey);
    }

    private static void putCache(Class<?> define, String dynKey, InterfaceImplClassDefine classDefine) {
        Map<String, InterfaceImplClassDefine> parsed = PARSED.computeIfAbsent(define.getName(), k -> new HashMap<>());
        parsed.put(dynKey, classDefine);
    }

    /**
     * Do parse class interface class define.
     *
     * @param define      the define
     * @param targetClass the target class
     * @param cl          the class loader
     * @return the interface class define
     * @throws ParseException         the parse exception
     * @throws ClassNotFoundException the class not found exception
     */
    private static InterfaceImplClassDefine doParseClass(Class<?> define, Class<?> targetClass, ClassLoader cl) throws ParseException, ClassNotFoundException {
        Map<String, String> typeDefines = getTypeDefines(define);
        List<MethodDefine> methodDefines = new ArrayList<>();

        FieldRef targetField = new EarlyFieldRef(null, null, FIRST_OBJ_NAME, targetClass);
        if (isRuntime(define)) {
            targetField = targetField.toRuntime();
        }
        for (Method method : define.getMethods()) {
            if (!Modifier.isAbstract(method.getModifiers())) continue;
            methodDefines.add(parseMethod(targetField, cl, method, typeDefines));
        }
        return new InterfaceImplClassDefine(define, targetClass, methodDefines);
    }

    /**
     * Parse method method define.
     *
     * @param firstField   the first field
     * @param cl           the class loader
     * @param method       the method
     * @param typedDefines the typed defines
     * @return the method define
     * @throws ClassNotFoundException the class not found exception
     */
    public static MethodDefine parseMethod(FieldRef firstField, ClassLoader cl, Method method, Map<String, String> typedDefines) throws ClassNotFoundException {
        verify(method);

        String fieldExpr = null;
        io.github.hhy50.linker.annotations.Field.Getter getter = method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Field.Getter.class);
        io.github.hhy50.linker.annotations.Field.Setter setter = method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Field.Setter.class);
        if (getter != null) {
            fieldExpr = Util.getOrElseDefault(getter.value(), method.getName());
        } else if (setter != null) {
            fieldExpr = Util.getOrElseDefault(setter.value(), method.getName());
        }

        // copy
        typedDefines = new HashMap<>(typedDefines);
        typedDefines.putAll(getTypeDefines(method));

        String targetClassName = typedDefines.get(FIRST_OBJ_NAME);
        if (firstField instanceof EarlyFieldRef && StringUtil.isNotEmpty(targetClassName)) {
            firstField = new EarlyFieldRef(null, null, FIRST_OBJ_NAME, cl.loadClass(targetClassName));
        }
        firstField.setFullName(FIRST_OBJ_NAME);

        MethodDefine methodDefine = new MethodDefine(method);
        if (fieldExpr != null) {
            Tokens tokens = TOKEN_PARSER.parse(fieldExpr);
            methodDefine.fieldRef = parseFieldExpr(firstField, cl, tokens,
                    AnnotationUtils.getDesignateStaticFields(method, tokens.tail().value()),
                    typedDefines);
        } else if (methodDefine.hasConstructor()) {
            Class<?> classType = ((EarlyFieldRef) firstField).getClassType();
            String[] argsType = parseArgsType(method);
            Constructor<?> constructor = ReflectUtil.matchConstructor(classType, argsType);
            if (constructor == null) {
                throw new ParseException("Constructor not found in class '"+classType+"' with args "+Arrays.toString(argsType));
            }
            methodDefine.methodRef = new ConstructorRef((EarlyFieldRef) firstField, method.getName(), constructor);
        } else {
            io.github.hhy50.linker.annotations.Method.Name methodNameAnn = method.getAnnotation(io.github.hhy50.linker.annotations.Method.Name.class);
            String methodName = Util.getOrElseDefault(methodNameAnn == null ? null : methodNameAnn.value(), methodDefine.getName());
            int i;
            if ((i = methodName.lastIndexOf('.')) != -1) {
                fieldExpr = methodName.substring(0, i);
                methodName = methodName.substring(i+1);
            }
            methodDefine.methodRef = parseMethodExpr(firstField, cl, methodName, method, TOKEN_PARSER.parse(fieldExpr), typedDefines);
        }
        return methodDefine;
    }

    private static MethodRef parseMethodExpr(FieldRef firstField, ClassLoader cl, String targetMethod, Method defineMethod,
                                             final Tokens fieldTokens, Map<String, String> typedDefines) throws ClassNotFoundException {
        Map<String, Boolean> staticTokens = AnnotationUtils.getDesignateStaticFields(defineMethod, targetMethod);
        FieldRef owner = parseFieldExpr(firstField, cl, fieldTokens, staticTokens, typedDefines);
        String[] argsType = parseArgsType(defineMethod);
        Class<?> returnClass = defineMethod.getReturnType();
        io.github.hhy50.linker.annotations.Method.InvokeSuper invokeSuperAnno = defineMethod.getAnnotation(io.github.hhy50.linker.annotations.Method.InvokeSuper.class);
        String superClass = invokeSuperAnno != null ? invokeSuperAnno.value() : null;
        MethodRef methodRef = null;
        if (owner instanceof EarlyFieldRef) {
            Class<?> ownerClass = ((EarlyFieldRef) owner).getClassType();
            Method method = ReflectUtil.matchMethod(ownerClass, targetMethod, superClass, argsType);
            if (method == null && typedDefines.containsKey(owner.getFullName())) {
                throw new ParseException("can not find method "+targetMethod+" in class "+ownerClass.getName());
            }
            methodRef = method == null ? null : new EarlyMethodRef(owner, method);
        }
        if (methodRef == null) {
            methodRef = new RuntimeMethodRef(owner, targetMethod, argsType, returnClass);
            if (staticTokens.containsKey(targetMethod)) {
                ((RuntimeMethodRef) methodRef).designateStatic(staticTokens.get(targetMethod));
            }
        }
        if (superClass != null) {
            methodRef.setSuperClass(superClass);
        }
        return methodRef;
    }

    private static String[] parseArgsType(Method method) {
        return Arrays.stream(method.getParameters())
                .map(item -> {
                    String typed = AnnotationUtils.getTyped(item);
                    if (StringUtil.isNotEmpty(typed)) {
                        return typed;
                    }
                    String bind = AnnotationUtils.getBind(item.getType());
                    if (StringUtil.isNotEmpty(bind)) {
                        return bind;
                    }
                    return item.getType().getName();
                }).toArray(String[]::new);
    }


    private static FieldRef parseFieldExpr(FieldRef firstField, ClassLoader cl, final Tokens tokens,
                                           Map<String, Boolean> staticFields, Map<String, String> typedDefines) throws ClassNotFoundException {
        boolean isRuntime = firstField instanceof RuntimeFieldRef;
        Class<?> currentType = isRuntime ? null : ((EarlyFieldRef) firstField).getClassType();
        FieldRef lastField = firstField;
        String fullField = null;
        for (Token token : tokens) {
            Field earlyField = lastField instanceof EarlyFieldRef ? token.getField(currentType) : null;
            currentType = earlyField == null ? null : earlyField.getType();
            fullField = Optional.ofNullable(fullField).map(i -> i+"."+token.value()).orElseGet(token::value);

            Class<?> assignedType = getFieldTyped(typedDefines, cl, fullField, token.value());
            if (assignedType != null) {
                if (earlyField != null && !ClassUtil.isAssignableFrom(assignedType, earlyField.getType())) {
                    throw new ClassTypeNotMatchException(assignedType.getName(), earlyField.getType().getName());
                }
                currentType = assignedType;
            }

            lastField = earlyField != null ? new EarlyFieldRef(lastField, earlyField, assignedType)
                    : new RuntimeFieldRef(lastField, lastField.fieldName, token.value());
            lastField.setFullName(fullField);
            designateStatic(staticFields, lastField);
        }
        return lastField;
    }

    private static void verify(Method method) {
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

    private static Class<?> getFieldTyped(Map<String, String> typedDefines, ClassLoader cl, String fullField, String tokenValue) throws ClassNotFoundException {
        if (typedDefines.containsKey(fullField)) {
            return cl.loadClass(typedDefines.get(fullField));
        }
        if (typedDefines.containsKey(tokenValue)) {
            return cl.loadClass(typedDefines.get(tokenValue));
        }
        return null;
    }

    private static void designateStatic(Map<String, Boolean> staticFields, FieldRef fieldRef) {
        if (fieldRef instanceof RuntimeFieldRef) {
            if (staticFields.containsKey(fieldRef.getFullName())) {
                ((RuntimeFieldRef) fieldRef).designateStatic(staticFields.get(fieldRef.getFullName()));
            } else if (staticFields.containsKey(fieldRef.fieldName)) {
                ((RuntimeFieldRef) fieldRef).designateStatic(staticFields.get(fieldRef.fieldName));
            }
        }
    }
}
