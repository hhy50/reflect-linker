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
import io.github.hhy50.linker.generate.invoker.MethodExprInvoker;
import io.github.hhy50.linker.token.*;
import io.github.hhy50.linker.util.*;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import static io.github.hhy50.linker.util.ParseUtil.getRawType;

/**
 * The type Parse context.
 */
public class ParseContext {

    /**
     * The Class loader.
     */
    ClassLoader classLoader;

    /**
     * The Target root.
     */
    Class<?> rootType;

    /**
     * The Class metadata.
     */
    AbsInterfaceMetadata classMetadata;

    /**
     * The Token parser.
     */
    TokenParser tokenParser = new TokenParser();

    /**
     * Instantiates a new Parse context.
     *
     * @param classMetadata the class metadata
     * @param targetClass   the target class
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
        List<AbsMethodMetadata> metadatas = new ArrayList<>();
        for (Method method : this.classMetadata.getMethods()) {
            if (!Modifier.isAbstract(method.getModifiers()))
                continue;
            if (AnnotationUtils.hasAnnotation(method.getDeclaringClass(), Builtin.class)) {
                continue;
            }
            AbsMethodMetadata metadata = preParse(classMetadata, method);
            metadatas.add(metadata);
        }

        List<MethodExprRef> methods = new ArrayList<>();
        for (AbsMethodMetadata metadata : metadatas) {
            MethodExprRef methodExprRef = parseMethod(metadata);
            postParse(methodExprRef);
            methods.add(methodExprRef);
        }
        return methods;
    }

    /**
     * Pre parse.
     *
     * @param classMetadata the class metadata
     * @param method        the method
     * @return the abs method metadata
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
     * @param methodExpr the method expr
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
            ParametersParser parametersParser = new ParametersParser(this, metadata, ArgsToken.ofAll());
            String[] types = parametersParser.getParametersTypes();
            Constructor<?> constructor = ReflectUtil.matchConstructor(rootType, types);
            if (constructor == null) {
                throw new ParseException(
                        "Constructor not found in class '" + rootType + "' with args " + Arrays.toString(types));
            }

            MethodExprRef methodExprRef = new MethodExprRef(metadata);
            methodExprRef.addStepMethod(new ConstructorRef(metadata.getName(), constructor));
            return methodExprRef;
        } else {
            String expr = metadata.getExpr();
            MethodExprRef methodExprRef = parseMethodExpr(metadata, tokenParser.parse(expr));
            return methodExprRef;
        }
    }

    private MethodExprRef parseMethodExpr(AbsMethodMetadata metadata, final Tokens tokens) throws ClassNotFoundException {
        return parseMethodExpr(metadata, tokens, metadata.isSetter());
    }

    private MethodExprRef parseMethodExpr(AbsMethodMetadata metadata, final Tokens tokens, boolean setterContext) throws ClassNotFoundException {
        String invokeSuper = metadata.getInvokeSuper();

        // Parse all steps first.
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
                    if (isLast && setterContext) {
                        methodExprRef.addStepMethod(new FieldSetterMethodRef(fieldRef));
                    } else {
                        methodExprRef.addStepMethod(new FieldGetterMethodRef(fieldRef));
                    }
                    curType = fieldRef.getActualType();
                }
            }
            if (methodToken != null) {
                ParametersParser parametersParser = new ParametersParser(this, metadata, methodToken.getArgsToken());
                String[] types = parametersParser.getParametersTypes();

                Method method = ReflectUtil.matchMethod(curType, methodToken.methodName, invokeSuper, types);
                MethodRef m;
                if (method != null) {
                    m = new EarlyMethodRef(method);
                    curType = method.getReturnType();
                } else {
                    Boolean designateStatic = metadata.isDesignateStatic(methodToken.methodName);
                    m = new RuntimeMethodRef(methodToken.methodName, types)
                            .setAutolink(metadata.isAutolink());
                    curType = Object.class;
                    ((RuntimeMethodRef) m).setStatic(designateStatic);
                }
                m.setSuperClass(invokeSuper);
                m.setParametersLoader(parametersParser.getParametersLoader());
                m.setIndexs(methodToken.getIndexs());
                m.setNullable(methodToken.isNullable());
                methodExprRef.addStepMethod(m);
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
            Class<?> assignedType = Optional.ofNullable(metadata.getTyped(fullField, fieldName))
                    .map(t -> Util.getClass(this.classLoader, t))
                    .orElse(null);
            if (assignedType != null) {
                if (earlyField != null && !ClassUtil.isAssignableFrom(assignedType, earlyField.getType())) {
                    throw new ClassTypeNotMatchException(assignedType.getName(), earlyField.getType().getName());
                }
                currentType = index == null ? assignedType : Util.expandIndexType(index, assignedType);
            }
            Boolean designateStatic = metadata.isDesignateStatic(fullField);
            FieldRef fieldRef = earlyField != null ? new EarlyFieldRef(earlyField)
                    : new RuntimeFieldRef(fieldName);
            fieldRef.setNullable(token.isNullable());
            fieldRef.setIndex(index);
            if (designateStatic != null) {
                fieldRef.setStatic(designateStatic);
            }
            fields.add(fieldRef);
        }
        return fields;
    }

    static class ParametersParser {
        private final ParseContext parseContext;
        private final AbsMethodMetadata metadata;
        private final ArgsToken tokens;
        private final List<ParameterLoader> parameters;
        private ParametersLoader parametersLoader;

        public ParametersParser(ParseContext parseContext, AbsMethodMetadata metadata, ArgsToken tokens) throws ClassNotFoundException {
            this.parseContext = parseContext;
            this.metadata = metadata;
            this.tokens = tokens;
            this.parameters = new ArrayList<>();

            parse();
        }

        String[] getParametersTypes() {
            return this.parameters.stream().map(ParameterLoader::getType).toArray(String[]::new);
        }

        ParametersLoader getParametersLoader() {
            return this.parametersLoader;
        }

        void parse() throws ClassNotFoundException {
            Parameter[] parameters = metadata.getParameters();
            if (metadata.isConstructor() || tokens.isPlaceholderAll()) {
                for (int i = 0; i < parameters.length; i++) {
                    this.parameters.add(ParameterLoader.placeholder(i, ParseUtil.getRawType(parameters[i])));
                }
                this.parametersLoader = ParametersLoader.all();
                return;
            }

            for (int i = 0; i < tokens.size(); i++) {
                Token item = tokens.get(i);
                if (item.kind() == Token.Kind.Placeholder) {
                    int p = ((PlaceholderToken) item).index;
                    if (p < 0 || p >= parameters.length) {
                        throw new ParseException("Parameter index out of bounds for parameter: $" + p);
                    }
                    this.parameters.add(ParameterLoader.placeholder(p, getRawType(parameters[p])));
                } else if (item.kind() == Token.Kind.IntConst) {
                    this.parameters.add(ParameterLoader.constant("int", ((ConstToken) item).getValue()));
                } else if (item.kind() == Token.Kind.StrConst) {
                    this.parameters.add(ParameterLoader.constant("java.lang.String", ((ConstToken) item).getValue()));
                } else if (item.kind() == Token.Kind.Tokens
                        || item.kind() == Token.Kind.Field
                        || item.kind() == Token.Kind.Method) {
                    MethodExprInvoker argExprInvoker = parseArgInvoker(item);
                    Type argType = argExprInvoker.getMethodType().getReturnType();
                    if (argType == Type.VOID_TYPE) {
                        throw new ParseException("Invalid argument type");
                    }
                    this.parameters.add(ParameterLoader.expr(argType.getClassName(), argExprInvoker));
                } else {
                    throw new ParseException("Invalid argument type");
                }
            }
            this.parametersLoader = ParametersLoader.of(this.parameters);
        }

        private MethodExprInvoker parseArgInvoker(Token token) throws ClassNotFoundException {
            if (token instanceof Tokens) {
                return this.parseContext.parseMethodExpr(metadata, (Tokens) token, false).defineInvoker();
            }
            Tokens tokens = new Tokens();
            tokens.add(token);
            return this.parseContext.parseMethodExpr(metadata, tokens, false).defineInvoker();
        }
    }
}
