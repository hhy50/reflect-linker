package io.github.hhy50.linker.define;

import io.github.hhy50.linker.annotations.Builtin;
import io.github.hhy50.linker.annotations.Verify;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.define.md.AbsInterfaceMetadata;
import io.github.hhy50.linker.define.md.AbsMethodMetadata;
import io.github.hhy50.linker.define.method.*;
import io.github.hhy50.linker.exceptions.ClassTypeNotMatchException;
import io.github.hhy50.linker.exceptions.ParseException;
import io.github.hhy50.linker.exceptions.VerifyException;
import io.github.hhy50.linker.token.*;
import io.github.hhy50.linker.util.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * The type Parse context.
 */
public class ParseContext {
    private static final String CURRENT_TOKEN = "$";
    private static final String FIRST_OBJ_NAME = "target";

    /**
     * The Class loader.
     */
    ClassLoader classLoader;

    /**
     * The Target root.
     */
    Class<?> rootType;

    AbsInterfaceMetadata classMetadata;

    /**
     * 解析过程中解析到的字段和method
     */
    final Map<String, FieldRef> fieldUnits = new HashMap<>();
    final Map<String, FieldRef> methodUnits = new HashMap<>();

    /**
     * The Typed fields.
     */
    final Map<String, String> typedFields = new HashMap<>();

    /**
     * The Token parser.
     */
    TokenParser tokenParser = new TokenParser();

    /**
     * Instantiates a new Parse context.
     */
    ParseContext(AbsInterfaceMetadata classMetadata, Class<?> targetClass) {
        this.classMetadata = classMetadata;
        this.rootType = targetClass;
    }

    /**
     * Parse list.
     *
     * @return the list
     * @throws ClassNotFoundException the class not found exception
     * @throws ParseException         the parse exception
     */
    public List<MethodExprRef> parse() throws ClassNotFoundException, ParseException {
        return doParseClass();
    }

    private List<MethodExprRef> doParseClass() throws ClassNotFoundException, ParseException {
        List<MethodExprRef> methods = new ArrayList<>();
        for (Method method : this.classMetadata.getMethods()) {
            if (!Modifier.isAbstract(method.getModifiers()))
                continue;
            if (AnnotationUtils.hasAnnotation(method.getDeclaringClass(), Builtin.class)) {
                continue;
            }
            AbsMethodMetadata metadata = preParse(classMetadata, method);
            MethodExprRef methodExprRef = parseMethod(metadata);
            postParse(methodExprRef);
            methods.add(methodExprRef);
        }
        return methods;
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
     */
    void postParse(MethodExprRef methodExpr) {
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
     * Parse method abs method define.
     *
     * @param metadata the method metadata
     * @return the abs method define
     * @throws VerifyException        the verify exception
     * @throws ClassNotFoundException the class not found exception
     * @throws ParseException         the parse exception
     */
    public MethodExprRef parseMethod(AbsMethodMetadata metadata) throws VerifyException, ClassNotFoundException, ParseException {
        if (metadata.isConstructor()) {
            String[] argsType = parseArgsType(metadata, null, true);
            Constructor<?> constructor = ReflectUtil.matchConstructor(rootType, argsType);
            if (constructor == null) {
                throw new ParseException(
                        "Constructor not found in class '" + rootType + "' with args " + Arrays.toString(argsType));
            }
            return null;
        } else {
            String expr = metadata.getExpr();
            MethodExprRef methodExprRef = parseMethodExpr(metadata, tokenParser.parse(expr));
            return methodExprRef;
        }
    }

    private MethodExprRef parseMethodExpr(AbsMethodMetadata metadata, final Tokens tokens) throws ClassNotFoundException {
        String invokeSuper = metadata.getInvokeSuper();

        // 提前全部解析
        Iterator<SplitToken> tokensIterator = tokens.splitWithMethod();
        ArrayList<SplitToken> tokenList = new ArrayList<>();
        while (tokensIterator.hasNext()) {
            tokenList.add(tokensIterator.next());
        }

        MethodExprRef methodExprRef = new MethodExprRef(metadata);
        Class<?> curType = this.rootType;
        for (SplitToken splitToken : tokenList) {
            Tokens fieldsToken = (Tokens) splitToken.prefix;
            MethodToken methodToken = (MethodToken) splitToken.suffix;

            if (fieldsToken != null && fieldsToken.size() > 0) {
                List<FieldRef> fieldRefs = parseFieldExpr(metadata, curType, fieldsToken);
                Iterator<FieldRef> iter = fieldRefs.iterator();
                while (iter.hasNext()) {
                    FieldRef fieldRef = iter.next();
                    boolean isLast = !iter.hasNext();
                    if (isLast && metadata.isSetter()) {
                        ArgsToken args = new ArgsToken();
                        args.add(new PlaceholderToken(0));
                        methodExprRef.addStepMethod(new FieldSetterMethodRef(fieldRef), args);
                    } else {
                        methodExprRef.addStepMethod(new FieldGetterMethodRef(fieldRef), null);
                    }
                    curType = fieldRef.getActualType();
                }
            }
            if (methodToken != null) {
                String[] argsType = parseArgsType(metadata, methodToken, false);
                Method method = ReflectUtil.matchMethod(curType, methodToken.methodName, invokeSuper, argsType);

                MethodRef m;
                if (method != null) {
                    m = new EarlyMethodRef(method);
                    curType = method.getReturnType();
                } else {
                    m = new RuntimeMethodRef(methodToken.methodName, argsType)
                            .setAutolink(metadata.isAutolink());
                    curType = Object.class;
                }
                m.setSuperClass(invokeSuper);
                methodExprRef.addStepMethod(m, methodToken.getArgsToken());
            }
        }
        return methodExprRef;
    }

    private List<FieldRef> parseFieldExpr(AbsMethodMetadata metadata, Class<?> rootType, final Tokens tokens) throws ClassNotFoundException {
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
            // TODO
            Class<?> assignedType = getFieldTyped(fullField, fieldName);
            if (assignedType != null) {
                if (earlyField != null && !ClassUtil.isAssignableFrom(assignedType, earlyField.getType())) {
                    throw new ClassTypeNotMatchException(assignedType.getName(), earlyField.getType().getName());
                }
                currentType = index == null ? assignedType : Util.expandIndexType(index, assignedType);
            }
            Boolean designateStatic = metadata.isDesignateStatic(fullField);
            FieldRef fieldRef = earlyField != null ? new EarlyFieldRef(fullField, earlyField)
                    : new RuntimeFieldRef(fullField, fieldName);
            fieldRef.setNullable(token.isNullable());
            fieldRef.setIndex(index);
            if (designateStatic != null) {
                fieldRef.setStatic(designateStatic);
            }
            fields.add(fieldRef);
        }
        return fields;
    }

    private String[] parseArgsType(AbsMethodMetadata metadata, MethodToken methodToken, boolean isConstructor) {
        Parameter[] parameters = metadata.getParameters();
        if (isConstructor) {
            return Arrays.stream(parameters)
                    .map(ParseUtil::getRawType).toArray(String[]::new);
        }
        ArgsToken args = methodToken.getArgsToken();
        return args.stream().map(item -> {
            if (item.kind() == Token.Kind.Placeholder) {
                int i = ((PlaceholderToken) item).index;
                Class argType = parameters[i].getType();
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
}