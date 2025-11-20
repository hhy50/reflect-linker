package io.github.hhy50.linker.define;

import io.github.hhy50.linker.annotations.Builtin;
import io.github.hhy50.linker.annotations.Verify;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.define.method.*;
import io.github.hhy50.linker.exceptions.ClassTypeNotMatchException;
import io.github.hhy50.linker.exceptions.ParseException;
import io.github.hhy50.linker.exceptions.VerifyException;
import io.github.hhy50.linker.token.*;
import io.github.hhy50.linker.util.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
     * The Class loader.
     */
    ClassLoader classLoader;

    /**
     * The Target root.
     */
    EarlyFieldRef root;

    List<AbsMethodMetadata> methods = new ArrayList<>();

    /**
     * The Parsed fields.
     */
    final Map<String, FieldRef> fieldUnits = new HashMap<>();
    final Map<String, FieldRef> methodUnits = new HashMap<>();

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
     */
    ParseContext(Class<?> defineClass, Class<?> targetClass) {
        this.defineClass = defineClass;
        this.root = new EarlyFieldRef(FIRST_OBJ_NAME, targetClass);
    }

    /**
     * Pre parse.
     *
     * @param classMetadata
     * @param method        the method
     */
    AbsMethodMetadata preParse(AbsInterfaceMetadata classMetadata, Method method) {
        AbsMethodMetadata metadata = new AbsMethodMetadata(classMetadata, method);

        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        for (Annotation annotation : declaredAnnotations) {
            if (annotation.annotationType().getDeclaredAnnotation(Verify.Unique.class) != null) {
                metadata.setUniqueAnnotation(annotation);
            }
            Verify.Custom custom = annotation.annotationType().getDeclaredAnnotation(Verify.Custom.class);
            if (custom != null) {
                Verifier verifier = null;
                try {
                    verifier = custom.value().newInstance();
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new VerifyException(e);
                }
                if (!verifier.test(method, annotation)) {
                    throw new VerifyException(
                            "method [" + method.getDeclaringClass() + "@" + method.getName() + "] verify failed");
                }
            }
            metadata.addAnnotation(annotation);
        }
        return metadata;
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

        AbsInterfaceMetadata classMetadata = new AbsInterfaceMetadata(this.defineClass);
        Annotation[] declaredAnnotations = this.defineClass.getDeclaredAnnotations();
        for (Annotation annotation : declaredAnnotations) {
            classMetadata.addAnnotation(annotation);
        }

        for (Method method : this.defineClass.getMethods()) {
            if (!Modifier.isAbstract(method.getModifiers()))
                continue;
            if (AnnotationUtils.hasAnnotation(method.getDeclaringClass(), Builtin.class)) {
                continue;
            }
            AbsMethodMetadata metadata = preParse(classMetadata, method);
            AbsMethodDefine absMethod = parseMethod(metadata);
            postParse(absMethod);
            absMethodDefines.add(absMethod);

            this.methods.add(metadata);
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
     * @throws ParseException         the parse exception
     */
    public AbsMethodDefine parseMethod(Method method) throws VerifyException, ClassNotFoundException, ParseException {
        io.github.hhy50.linker.annotations.Field.Getter getter = method
                .getDeclaredAnnotation(io.github.hhy50.linker.annotations.Field.Getter.class);
        io.github.hhy50.linker.annotations.Field.Setter setter = method
                .getDeclaredAnnotation(io.github.hhy50.linker.annotations.Field.Setter.class);
        io.github.hhy50.linker.annotations.Method.Expr expr = method
                .getDeclaredAnnotation(io.github.hhy50.linker.annotations.Method.Expr.class);

        AbsMethodDefine absMethodDefine = new AbsMethodDefine(method);
        Class<?> rootType = root.getActualType();
        if (getter != null) {
            String methodExpr = getter.value();
            absMethodDefine.methodRef = parseMethodExpr(method, tokenParser.parse(methodExpr));
        } else if (setter != null) {
            String exprStr = setter.value();
            Tokens tokens = tokenParser.parse(exprStr);
            List<FieldRef> fieldRefs = parseFieldExpr(rootType, tokens);
            // absMethodDefine.methodRef = new FieldGetterMethodRef(fieldRefs);
        } else if (absMethodDefine.hasConstructor()) {
            String[] argsType = parseArgsType(method, null, true);
            Constructor<?> constructor = ReflectUtil.matchConstructor(rootType, argsType);
            if (constructor == null) {
                throw new ParseException(
                        "Constructor not found in class '" + rootType + "' with args " + Arrays.toString(argsType));
            }
            // absMethodDefine.methodRef = new ConstructorRef(method.getName(),
            // constructor);
        } else {
            String methodExpr = Optional.ofNullable(expr).map(io.github.hhy50.linker.annotations.Method.Expr::value)
                    .orElseGet(() -> method.getName() + "(" + IntStream.range(0, method.getParameterCount())
                            .mapToObj(i -> "$" + i).collect(Collectors.joining(",")) + ")");
            absMethodDefine.methodRef = parseMethodExpr(method, tokenParser.parse(methodExpr));
        }
        return absMethodDefine;
    }

    private MethodExprRef parseMethodExpr(Method methodDefine, final Tokens tokens) throws ClassNotFoundException {
        String invokeSuper = Optional.ofNullable(methodDefine
                        .getAnnotation(io.github.hhy50.linker.annotations.Method.InvokeSuper.class))
                .map(io.github.hhy50.linker.annotations.Method.InvokeSuper::value)
                .orElse(null);

        // 提前全部解析
        Iterator<SplitToken> tokensIterator = tokens.splitWithMethod();
        ArrayList<SplitToken> tokenList = new ArrayList<>();
        while (tokensIterator.hasNext()) {
            tokenList.add(tokensIterator.next());
        }

        MethodExprRef methodExprRef = new MethodExprRef(methodDefine);
        Class<?> curType = root.getActualType();
        for (SplitToken splitToken : tokenList) {
            Tokens fieldsToken = (Tokens) splitToken.prefix;
            MethodToken methodToken = (MethodToken) splitToken.suffix;

            if (fieldsToken != null && fieldsToken.size() > 0) {
                for (FieldRef fieldRef : parseFieldExpr(curType, fieldsToken)) {
                    methodExprRef.addStepMethod(new FieldGetterMethodRef(fieldRef), null);
                    curType = fieldRef.getActualType();
                }
            }
            if (methodToken != null) {
                String[] argsType = parseArgsType(methodDefine, methodToken, false);
                Method method = ReflectUtil.matchMethod(curType, methodToken.methodName, invokeSuper, argsType);

                MethodRef m;
                if (method != null) {
                    m = new EarlyMethodRef("", method);
                    curType = method.getReturnType();
                } else {
                    m = new RuntimeMethodRef("", methodToken.methodName, argsType)
                            .setAutolink(AnnotationUtils.isAutolink(methodDefine));
                    curType = Object.class;
                }
                m.setSuperClass(invokeSuper);
                methodExprRef.addStepMethod(m, methodToken.getArgsToken());
            }
        }
        return methodExprRef;
    }

    private List<FieldRef> parseFieldExpr(Class<?> rootType, final Tokens tokens) throws ClassNotFoundException {
        Class<?> currentType = rootType;
        String fullField = null;

        List<FieldRef> fields = new ArrayList<>();
        for (Token item : tokens) {
            if (item.kind() != Token.Kind.Field) {
                throw new ParseException("Field token expected, but " + item.getClass().getSimpleName() + " found");
            }
            FieldToken token = (FieldToken) item;
            String fieldName = token.fieldName;
            List<Object> index = token.getIndexVal();
            Field earlyField = token.getField(currentType);
            currentType = earlyField == null ? Object.class : Util.expandIndexType(index, earlyField.getType());
            fullField = Optional.ofNullable(fullField).map(i -> i + "." + fieldName).orElse(fieldName);

            // 使用@Typed指定的类型
            Class<?> assignedType = getFieldTyped(fullField, fieldName);
            if (assignedType != null) {
                if (earlyField != null && !ClassUtil.isAssignableFrom(assignedType, earlyField.getType())) {
                    throw new ClassTypeNotMatchException(assignedType.getName(), earlyField.getType().getName());
                }
                currentType = index == null ? assignedType : Util.expandIndexType(index, assignedType);
            }
            FieldRef fieldRef = earlyField != null ? new EarlyFieldRef(fullField, earlyField)
                    : new RuntimeFieldRef(fullField, fieldName);
            // lastField.setNullable(token.isNullable());
            // lastField.setDefaultValue();
            // lastField.setIndex();
            designateStatic(fieldRef);
            fields.add(fieldRef);
        }
        return fields;
    }

    private String[] parseArgsType(Method methodDefine, MethodToken methodToken, boolean isConstructor) {
        if (isConstructor) {
            return Arrays.stream(methodDefine.getParameters())
                    .map(ParseUtil::getRawType).toArray(String[]::new);
        }
        ArgsToken args = methodToken.getArgsToken();
        return args.stream().map(item -> {
            if (item.kind() == Token.Kind.Placeholder) {
                int i = ((PlaceholderToken) item).index;
                Class argType = methodDefine.getParameterTypes()[i];
                return TypeUtil.getClassName(argType);
            } else if (item.kind() == Token.Kind.IntConst) {
                return "int";
            } else if (item.kind() == Token.Kind.StrConst) {
                return "java.lang.String";
            } else if (item.kind() == Token.Kind.Method) {
                return "java.lang.Object";
            }
            throw new ParseException("Invalid argument type");
        }).toArray(String[]::new);
    }

    private Class<?> getFieldTyped(String fullField, String tokenValue) throws ClassNotFoundException {
        return Stream.of(fullField, tokenValue)
                .filter(StringUtil::isNotEmpty)
                .map(this.typedFields::get)
                .filter(Objects::nonNull)
                .map(item -> Util.getClass(this.classLoader, item))
                .findFirst().orElse(null);
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
            throw new VerifyException("method [" + method.getDeclaringClass() + "@" + method.getName()
                    + "] cannot have two annotations [" +
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
                    throw new VerifyException(
                            "method [" + method.getDeclaringClass() + "@" + method.getName() + "] verify failed");
                }
            }
        }
    }
}