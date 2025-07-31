package io.github.hhy50.linker.define;

import io.github.hhy50.linker.annotations.Builtin;
import io.github.hhy50.linker.annotations.Verify;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldIndexRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.define.method.ConstructorRef;
import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.define.method.RuntimeMethodRef;
import io.github.hhy50.linker.exceptions.ClassTypeNotMatchException;
import io.github.hhy50.linker.exceptions.ParseException;
import io.github.hhy50.linker.exceptions.VerifyException;
import io.github.hhy50.linker.token.FieldToken;
import io.github.hhy50.linker.token.Token;
import io.github.hhy50.linker.token.TokenParser;
import io.github.hhy50.linker.token.Tokens;
import io.github.hhy50.linker.util.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Parse context.
 */
public class ParseContext {
    private static final String CURRENT_TOKEN = "$";
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
     * The Class loader.
     */
    ClassLoader classLoader;

    /**
     * The Target root.
     */
    EarlyFieldRef targetRoot;

    /**
     * The Parsed fields.
     */
    final Map<String, FieldRef> parsedFields = new HashMap<>();
    /**
     * The Typed fields.
     */
    final Map<String, String> typedFields = new HashMap<>();
    /**
     * The constant staticTokens.
     */
    final Map<String, Boolean> staticTokens = new HashMap<>();

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
        this.targetRoot = new EarlyFieldRef(null, FIRST_OBJ_NAME, targetClass);
    }

    /**
     * Pre parse.
     *
     * @param method the method
     */
    void preParse(Method method) {
        verify(method);

        Map<String, String> typeDefines = ClassUtil.getTypeDefines(method);
        for (Map.Entry<String, String> fieldEntry : typeDefines.entrySet()) {
            String type = this.typedFields.put(fieldEntry.getKey(), fieldEntry.getValue());
            if (type != null && !type.equals(fieldEntry.getValue())) {
                throw new VerifyException("@Typed of field '" + fieldEntry.getKey() + "' defined twice is inconsistent");
            }
        }
        for (Map.Entry<String, Boolean> fieldEntry : AnnotationUtils.getDesignateStaticTokens(method, CURRENT_TOKEN).entrySet()) {
            String name = fieldEntry.getKey();
            Boolean isStatic = this.staticTokens.put(name, fieldEntry.getValue());
            if (isStatic != null && !isStatic.equals(fieldEntry.getValue())) {
                throw new VerifyException("@Static of field '" + name + "' defined twice is inconsistent");
            }
        }
    }

    /**
     * Post parse.
     *
     * @param absMethod the abs method
     */
    void postParse(AbsMethodDefine absMethod) {
        this.staticTokens.remove(CURRENT_TOKEN);
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
    public List<AbsMethodDefine> parse() throws ClassNotFoundException, ParseException {
        return doParseClass();
    }

    private List<AbsMethodDefine> doParseClass() throws ClassNotFoundException, ParseException {
        List<AbsMethodDefine> absMethodDefines = new ArrayList<>();
        for (Method method : this.defineClass.getMethods()) {
            if (!Modifier.isAbstract(method.getModifiers())) continue;
            Builtin builtin = method.getDeclaringClass().getDeclaredAnnotation(Builtin.class);
            if (builtin != null) {
                continue;
            }
            preParse(method);
            AbsMethodDefine absMethod = parseMethod(method);
            absMethodDefines.add(absMethod);
            postParse(absMethod);
        }
        return absMethodDefines;
    }

    /**
     * Parse method abs method define.
     *
     * @param method the method
     * @return the abs method define
     * @throws VerifyException        the verify exception
     * @throws ClassNotFoundException the class not found exception
     */
    public AbsMethodDefine parseMethod(Method method) throws VerifyException, ClassNotFoundException, ParseException {
        io.github.hhy50.linker.annotations.Field.Getter getter = method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Field.Getter.class);
        io.github.hhy50.linker.annotations.Field.Setter setter = method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Field.Setter.class);
        io.github.hhy50.linker.annotations.Method.Name name = method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Method.Name.class);

        AbsMethodDefine absMethodDefine = new AbsMethodDefine(method);
        if (getter != null || setter != null) {
            String exprStr = Util.getOrElseDefault(Optional.ofNullable(getter).map(io.github.hhy50.linker.annotations.Field.Getter::value)
                    .orElseGet(() -> setter.value()), method.getName());
            Tokens tokens = tokenParser.parse(exprStr);
            absMethodDefine.fieldRef = parseFieldExpr(targetClass, tokens);
        } else if (absMethodDefine.hasConstructor()) {
            String[] argsType = parseArgsType(method);
            Constructor<?> constructor = ReflectUtil.matchConstructor(targetClass, argsType);
            if (constructor == null) {
                throw new ParseException("Constructor not found in class '" + targetClass + "' with args " + Arrays.toString(argsType));
            }
            absMethodDefine.methodRef = new ConstructorRef(targetRoot, method.getName(), constructor);
        } else {
            io.github.hhy50.linker.annotations.Method.Name methodNameAnn = method.getAnnotation(io.github.hhy50.linker.annotations.Method.Name.class);
            String methodName = Util.getOrElseDefault(methodNameAnn == null ? null : methodNameAnn.value(), absMethodDefine.getName());
            String fieldExpr = null;
            int i;
            if ((i = methodName.lastIndexOf('.')) != -1) {
                fieldExpr = methodName.substring(0, i);
                methodName = methodName.substring(i + 1);
            }
            absMethodDefine.methodRef = parseMethodExpr(methodName, method, tokenParser.parse(fieldExpr));
        }
        return absMethodDefine;
    }

    private MethodRef parseMethodExpr(String methodName, Method methodDefine, final Tokens fieldTokens) throws ClassNotFoundException {
        FieldRef owner = parseFieldExpr(targetClass, fieldTokens);

        String[] argsType = parseArgsType(methodDefine);
        io.github.hhy50.linker.annotations.Method.InvokeSuper invokeSuperAnno = methodDefine.getAnnotation(io.github.hhy50.linker.annotations.Method.InvokeSuper.class);
        String superClass = invokeSuperAnno != null ? invokeSuperAnno.value() : null;
        MethodRef methodRef = null;
        Class<?> ownerClass = owner.getActualType();
        if (ownerClass != null) {
            Method method = ReflectUtil.matchMethod(ownerClass, methodName, superClass, argsType);
            if (method == null && this.typedFields.containsKey(owner.getFullName())) {
                throw new ParseException("can not find method " + methodName + " in class " + ownerClass.getName());
            }
            methodRef = method == null ? null : new EarlyMethodRef(owner, method);
        }
        if (methodRef == null) {
            methodRef = new RuntimeMethodRef(owner, methodName, argsType)
                    .setAutolink(AnnotationUtils.isAutolink(methodDefine));
            designateStatic(methodRef);
        }
        if (superClass != null) {
            methodRef.setSuperClass(superClass);
        }
        return methodRef;
    }

    private FieldRef parseFieldExpr(Class<?> rootType, final Tokens tokens) throws ClassNotFoundException {
        Class<?> currentType = rootType;
        FieldRef lastField = targetRoot;
        String fullField = null;
        for (Token item : tokens) {
            if (!(item instanceof FieldToken)) {
                throw new ParseException("Field token expected, but " + item.getClass().getSimpleName() + " found");
            }
            FieldToken token = (FieldToken) item;
            String fieldName = token.fieldName;
            List<Object> index = token.getIndexVal();
            Field earlyField = currentType == null ? null : token.getField(currentType);
            currentType = earlyField == null ? null : earlyField.getType();
            fullField = Optional.ofNullable(fullField).map(i -> i + "." + fieldName).orElse(fieldName);
            // 使用@Typed指定的类型
            Class<?> assignedType = getFieldTyped(fullField, fieldName);
            if (assignedType != null) {
                if (earlyField != null && !ClassUtil.isAssignableFrom(assignedType, earlyField.getType())) {
                    throw new ClassTypeNotMatchException(assignedType.getName(), earlyField.getType().getName());
                }
                currentType = assignedType;
            }
            lastField = earlyField != null ? new EarlyFieldRef(lastField, earlyField, assignedType) : new RuntimeFieldRef(lastField, fieldName);
            lastField.setFullName(fullField);
            designateStatic(lastField);
            if (index != null && index.size() > 0) {
                lastField = new FieldIndexRef(lastField, index);
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
                    return type.getCanonicalName();
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

    private void designateStatic(Object refObj) {
        if (refObj instanceof RuntimeFieldRef) {
            String fullName = ((RuntimeFieldRef) refObj).getFullName();
            if (staticTokens.containsKey(fullName)) {
                ((RuntimeFieldRef) refObj).designateStatic(staticTokens.get(fullName));
            } else if (staticTokens.containsKey(CURRENT_TOKEN)) {
                ((RuntimeFieldRef) refObj).designateStatic(staticTokens.get(CURRENT_TOKEN));
            }
        } else if (refObj instanceof RuntimeMethodRef) {
            if (staticTokens.containsKey(CURRENT_TOKEN)) {
                ((RuntimeMethodRef) refObj).designateStatic(staticTokens.get(CURRENT_TOKEN));
            }
        }
    }

    private static void verify(Method method) throws VerifyException {
        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        List<Class<? extends Annotation>> uniques = Arrays.stream(declaredAnnotations)
                .map(Annotation::annotationType)
                .filter(item -> item.getDeclaredAnnotation(Verify.Unique.class) != null).collect(Collectors.toList());
        if (uniques.size() > 1) {
            throw new VerifyException("method [" + method.getDeclaringClass() + "@" + method.getName() + "] cannot have two annotations [" +
                    uniques.stream().map(Class::getSimpleName).collect(Collectors.joining(", @", "@", "")) + "]");
        }
        for (Annotation annotation : declaredAnnotations) {
            Verify.Custom custom = annotation.annotationType().getDeclaredAnnotation(Verify.Custom.class);
            if (custom != null) {
                Verifier verifier = null;
                try {
                    verifier = custom.value().newInstance();
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new VerifyException(e);
                }
                if (!verifier.test(method, annotation)) {
                    throw new VerifyException("method [" + method.getDeclaringClass() + "@" + method.getName() + "] verify failed");
                }
            }
        }
    }
}