package io.github.hhy50.linker.define;

import io.github.hhy50.linker.annotations.Target;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static io.github.hhy50.linker.util.ClassUtil.getTypeDefines;

/**
 * <p>ClassDefineParse class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class ClassDefineParse {

    private static final Map<String, InterfaceClassDefine> PARSED = new HashMap<>();
    private static final String FIRST_OBJ_NAME = "target";
    private static final TokenParser TOKEN_PARSER = new TokenParser();

    /**
     * <p>parseClass.</p>
     *
     * @param define a {@link java.lang.Class} object.
     * @param classLoader a {@link java.lang.ClassLoader} object.
     * @return a {@link InterfaceClassDefine} object.
     * @throws ParseException if any.
     * @throws java.lang.ClassNotFoundException if any.
     */
    public static InterfaceClassDefine parseClass(Class<?> define, ClassLoader classLoader) throws ParseException, ClassNotFoundException {
        Target.Bind bindAnno = define.getDeclaredAnnotation(Target.Bind.class);
        if (bindAnno == null || bindAnno.value().equals("")) {
            throw new VerifyException("use @Target.Bind specified a class");
        }
        Class<?> targetClass = classLoader.loadClass(bindAnno.value());
        return doParseClass(define, targetClass, classLoader);
    }

    /**
     * <p>doParseClass.</p>
     *
     * @param define a {@link java.lang.Class} object.
     * @param targetClass a {@link java.lang.Class} object.
     * @param classLoader a {@link java.lang.ClassLoader} object.
     * @return a {@link InterfaceClassDefine} object.
     * @throws ParseException if any.
     * @throws java.lang.ClassNotFoundException if any.
     */
    public static InterfaceClassDefine doParseClass(Class<?> define, Class<?> targetClass, ClassLoader classLoader) throws ParseException, ClassNotFoundException {
        InterfaceClassDefine defineClass = PARSED.get(define.getName());
        if (defineClass != null) {
            return defineClass;
        }

        Map<String, String> typeDefines = getTypeDefines(define);
        List<MethodDefine> methodDefines = new ArrayList<>();

        FieldRef targetField = new EarlyFieldRef(null, null, FIRST_OBJ_NAME, targetClass);
        if (AnnotationUtils.isRuntime(define)) {
            targetField = targetField.toRuntime();
        }
        for (Method methodDefine : define.getDeclaredMethods()) {
            if (methodDefine.isDefault()) continue;
            methodDefines.add(parseMethod(targetField, classLoader, methodDefine, typeDefines));
        }

        InterfaceClassDefine classDefine = new InterfaceClassDefine(define, targetClass, methodDefines);
        PARSED.put(define.getName(), classDefine);
        return classDefine;
    }

    /**
     * <p>parseMethod.</p>
     *
     * @param firstField a {@link FieldRef} object.
     * @param classLoader a {@link java.lang.ClassLoader} object.
     * @param method a {@link java.lang.reflect.Method} object.
     * @param typedDefines a {@link java.util.Map} object.
     * @return a {@link MethodDefine} object.
     * @throws java.lang.ClassNotFoundException if any.
     */
    public static MethodDefine parseMethod(FieldRef firstField, ClassLoader classLoader, Method method, Map<String, String> typedDefines) throws ClassNotFoundException {
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
            firstField = new EarlyFieldRef(null, null, FIRST_OBJ_NAME, classLoader.loadClass(targetClassName));
        }
        firstField.setFullName(FIRST_OBJ_NAME);

        MethodDefine methodDefine = new MethodDefine(method);
        if (fieldExpr != null) {
            Tokens tokens = TOKEN_PARSER.parse(fieldExpr);
            methodDefine.fieldRef = parseFieldExpr(firstField, classLoader, tokens, AnnotationUtils.getDesignateStaticFields(method), typedDefines);
            if (AnnotationUtils.isRuntime(method)) {
                methodDefine.fieldRef = methodDefine.fieldRef.toRuntime();
            }
        } else {
            io.github.hhy50.linker.annotations.Method.Name methodNameAnn = method.getAnnotation(io.github.hhy50.linker.annotations.Method.Name.class);
            String methodName = Util.getOrElseDefault(methodNameAnn == null ? null : methodNameAnn.value(), methodDefine.getName());
            int i;
            if ((i = methodName.lastIndexOf('.')) != -1) {
                fieldExpr = methodName.substring(0, i);
                methodName = methodName.substring(i+1);
            }
            methodDefine.methodRef = parseMethodExpr(firstField, classLoader, methodName, method, TOKEN_PARSER.parse(fieldExpr), typedDefines);
        }
        return methodDefine;
    }

    private static MethodRef parseMethodExpr(FieldRef firstField, ClassLoader classLoader, String name, Method defineMethod,
                                             final Tokens fieldTokens, Map<String, String> typedDefines) throws ClassNotFoundException {
        Map<String, Boolean> staticTokens = AnnotationUtils.getDesignateStaticFields(defineMethod);
        FieldRef owner = parseFieldExpr(firstField, classLoader, fieldTokens, staticTokens, typedDefines);
        String[] argsType = Arrays.stream(defineMethod.getParameters())
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
        Class<?> returnClass = defineMethod.getReturnType();
        if (AnnotationUtils.isRuntime(defineMethod)) {
            owner = owner.toRuntime();
        }
        io.github.hhy50.linker.annotations.Method.InvokeSuper invokeSuperAnno = defineMethod.getAnnotation(io.github.hhy50.linker.annotations.Method.InvokeSuper.class);
        String superClass = invokeSuperAnno != null ? invokeSuperAnno.value() : null;
        Method method = null;
        if (owner instanceof EarlyFieldRef) {
            Class<?> ownerClass = ((EarlyFieldRef) owner).getClassType();
            method = ReflectUtil.matchMethod(ownerClass, name, superClass, argsType);
            if (method == null && typedDefines.containsKey(owner.getFullName())) {
                throw new ParseException("can not find method "+name+" in class "+ownerClass.getName());
            }
        }
        MethodRef methodRef = method == null ? new RuntimeMethodRef(owner, name, argsType, returnClass) : new EarlyMethodRef(owner, method);
        if (superClass != null) {
            if (method != null) methodRef.setSuperClass(method.getDeclaringClass().getName());
            else methodRef.setSuperClass(superClass);
        }
        return methodRef;
    }


    /**
     * 解析目标字段
     *
     * @param firstField
     * @param classLoader
     * @param tokens
     * @param staticFields
     * @param typedDefines
     * @return
     * @throws ClassNotFoundException
     */
    private static FieldRef parseFieldExpr(FieldRef firstField, ClassLoader classLoader, final Tokens tokens,
                                           Map<String, Boolean> staticFields, Map<String, String> typedDefines) throws ClassNotFoundException {
        boolean isRuntime = firstField instanceof RuntimeFieldRef;
        Class<?> currentType = isRuntime ? null : ((EarlyFieldRef) firstField).getClassType();
        FieldRef lastField = firstField;
        String fullField = null;
        for (Token token : tokens) {
            Field currentField = lastField instanceof RuntimeFieldRef ? null : token.getField(currentType);
            currentType = currentField == null ? null : currentField.getType();
            fullField = fullField == null ? token.value() : (fullField+"."+token.value());

            Class<?> assignedType = getFieldTyped(typedDefines, classLoader, fullField, token.value());
            if (assignedType != null) {
                if (currentField != null && !ClassUtil.isAssignableFrom(assignedType, currentField.getType())) {
                    throw new ClassTypeNotMatchException(assignedType.getName(), currentField.getType().getName());
                }
                currentType = assignedType;
            }

            lastField = currentField != null ? new EarlyFieldRef(lastField, currentField, assignedType)
                    : new RuntimeFieldRef(lastField, lastField.fieldName, token.value());
            lastField.setFullName(fullField);
            designateStatic(staticFields, lastField);
        }
        return lastField;
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
        io.github.hhy50.linker.annotations.Field.Getter getter = method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Field.Getter.class);
        io.github.hhy50.linker.annotations.Field.Setter setter = method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Field.Setter.class);
        // Field.Setter和@Field.Getter只能有一个
        if (getter != null && setter != null) {
            throw new VerifyException("class ["+method.getDeclaringClass()+"@"+method.getName()+"] cannot have two annotations @Field.getter and @Field.setter");
        }
        if (getter != null) {
            if (StringUtil.isEmpty(getter.value())) {
                throw new VerifyException("class ["+method.getDeclaringClass()+"@"+method.getName()+"] is getter method, must be specified field-expression");
            }
            if (method.getReturnType() == void.class || method.getParameters().length > 0) {
                throw new VerifyException("class ["+method.getDeclaringClass()+"@"+method.getName()+"] is getter method,its return value cannot be of type void, and the parameter length must be 0");
            }
        } else if (setter != null) {
            if (StringUtil.isEmpty(setter.value())) {
                throw new VerifyException("class ["+method.getDeclaringClass()+"@"+method.getName()+"] is getter method, must be specified field-expression");
            }
            if (method.getReturnType() != void.class || method.getParameters().length != 1) {
                throw new VerifyException("class ["+method.getDeclaringClass()+"@"+method.getName()+"] is setter method,its return value must be of type void, and the parameter length must be 1");
            }
        }

        io.github.hhy50.linker.annotations.Method.Name methodNameAnn = method.getAnnotation(io.github.hhy50.linker.annotations.Method.Name.class);
        // @Field 和 @Method相关的注解不能同时存在
        if ((getter != null || setter != null) & (methodNameAnn != null)) {
            throw new VerifyException("class ["+method.getDeclaringClass()+"@"+method.getName()+"], @Method.Name and @Field.Setter|@Field.Getter only one can exist");
        }
    }

    private static Class<?> getFieldTyped(Map<String, String> typedDefines, ClassLoader classLoader, String fullField, String tokenValue) throws ClassNotFoundException {
        if (typedDefines.containsKey(fullField)) {
            return classLoader.loadClass(typedDefines.get(fullField));
        }
        if (typedDefines.containsKey(tokenValue)) {
            return classLoader.loadClass(typedDefines.get(tokenValue));
        }
        return null;
    }

    private static void designateStatic(Map<String, Boolean> staticFields, FieldRef fieldRef) {
        if (fieldRef instanceof RuntimeFieldRef) {
//            Map<String, Boolean> staticFields = AnnotationUtils.getDesignateStaticFields(methodDefine);
            if (staticFields.containsKey(fieldRef.getFullName())) {
                ((RuntimeFieldRef) fieldRef).designateStatic(staticFields.get(fieldRef.getFullName()));
            } else if (staticFields.containsKey(fieldRef.fieldName)) {
                ((RuntimeFieldRef) fieldRef).designateStatic(staticFields.get(fieldRef.fieldName));
            }
        }
    }
}
