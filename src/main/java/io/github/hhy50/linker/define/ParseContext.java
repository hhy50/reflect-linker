package io.github.hhy50.linker.define;

import io.github.hhy50.linker.annotations.Builtin;
import io.github.hhy50.linker.annotations.Verify;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.define.md.AbsInterfaceMetadata;
import io.github.hhy50.linker.define.md.AbsMethodMetadata;
import io.github.hhy50.linker.define.method.*;
import io.github.hhy50.linker.define.parameter.ParameterLoader;
import io.github.hhy50.linker.define.parameter.ParameterParser;
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
            ParameterParser parametersParser = new ParameterParser(this, metadata, ArgsToken.ofAll());
            String[] types = parametersParser.getParametersTypes();
            Constructor<?> constructor = ReflectUtil.matchConstructor(rootType, types);
            if (constructor == null) {
                throw new ParseException(
                        "Constructor not found in class '" + rootType + "' with args " + Arrays.toString(types));
            }
            MethodExprRef methodExprRef = new MethodExprRef(metadata);
            methodExprRef.addStepMethod(new ConstructorRef(metadata.getName(), constructor), ParameterLoader.DEFAULT);
            return methodExprRef;
        } else if (metadata.isSetter()) {
            String expr = metadata.getExpr();
            return parseFieldSetter(metadata, tokenParser.parse(expr));
        } else {
            String expr = metadata.getExpr();
            return parseMethodExpr(metadata, tokenParser.parse(expr));
        }
    }

    /**
     * Parse field setter method expr ref.
     *
     * @param metadata the metadata
     * @param tokens   the tokens
     * @return the method expr ref
     * @throws ClassNotFoundException the class not found exception
     */
    public MethodExprRef parseFieldSetter(AbsMethodMetadata metadata, final Tokens tokens) throws ClassNotFoundException {
        List<FieldRef> fieldRefs = this.parseFieldExpr(metadata, this.rootType, tokens);
        MethodExprRef methodExprRef = new MethodExprRef(metadata);
        for (int i = 0; i < fieldRefs.size()-1; i++) {
            methodExprRef.addStepMethod(new FieldGetterMethodRef(fieldRefs.get(i)));
        }
        methodExprRef.addStepMethod(new FieldSetterMethodRef(fieldRefs.get(fieldRefs.size()-1)), ParameterLoader.DEFAULT);
        return methodExprRef;
    }

    /**
     * Parse method expr ref.
     *
     * @param metadata the metadata
     * @param tokens   the tokens
     * @return the method expr ref
     * @throws ClassNotFoundException the class not found exception
     */
    public MethodExprRef parseMethodExpr(AbsMethodMetadata metadata, final Tokens tokens) throws ClassNotFoundException {
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
                for (FieldRef fieldRef : fieldRefs) {
                    methodExprRef.addStepMethod(new FieldGetterMethodRef(fieldRef));
                    curType = fieldRef.getActualType();
                }
            }
            if (methodToken != null) {
                ParameterParser parametersParser = new ParameterParser(this, metadata, methodToken.getArgsToken());
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
                m.setIndexs(methodToken.getIndexs());
                m.setNullable(methodToken.isNullable());
                methodExprRef.addStepMethod(m, parametersParser.getParameterLoader());
            }
        }
        return methodExprRef;
    }

    /**
     * Parse field expr list.
     *
     * @param metadata the metadata
     * @param rootType the root type
     * @param tokens   the tokens
     * @return the list
     * @throws ClassNotFoundException the class not found exception
     */
    public List<FieldRef> parseFieldExpr(AbsMethodMetadata metadata, Class<?> rootType, final Tokens tokens) throws ClassNotFoundException {
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
}
