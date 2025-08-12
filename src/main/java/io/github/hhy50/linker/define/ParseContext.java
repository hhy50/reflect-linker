package io.github.hhy50.linker.define;

import io.github.hhy50.linker.annotations.Builtin;
import io.github.hhy50.linker.annotations.Verify;
import io.github.hhy50.linker.define.field.*;
import io.github.hhy50.linker.define.method.*;
import io.github.hhy50.linker.exceptions.ClassTypeNotMatchException;
import io.github.hhy50.linker.exceptions.ParseException;
import io.github.hhy50.linker.exceptions.VerifyException;
import io.github.hhy50.linker.generate.ArgsDepAnalysis;
import io.github.hhy50.linker.token.*;
import io.github.hhy50.linker.util.*;
import org.objectweb.asm.Type;

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
                throw new VerifyException("@Typed of field '"+fieldEntry.getKey()+"' defined twice is inconsistent");
            }
        }
        for (Map.Entry<String, Boolean> fieldEntry : AnnotationUtils.getDesignateStaticTokens(method, CURRENT_TOKEN).entrySet()) {
            String name = fieldEntry.getKey();
            Boolean isStatic = this.staticTokens.put(name, fieldEntry.getValue());
            if (isStatic != null && !isStatic.equals(fieldEntry.getValue())) {
                throw new VerifyException("@Static of field '"+name+"' defined twice is inconsistent");
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
        Map<String, String> typeDefines = ClassUtil.getTypeDefines(this.defineClass);
        for (Map.Entry<String, String> fieldEntry : typeDefines.entrySet()) {
            String type = this.typedFields.put(fieldEntry.getKey(), fieldEntry.getValue());
            if (type != null && !type.equals(fieldEntry.getValue())) {
                throw new VerifyException("@Typed of field '"+fieldEntry.getKey()+"' defined twice is inconsistent");
            }
        }

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
     * @throws ParseException         the parse exception
     */
    public AbsMethodDefine parseMethod(Method method) throws VerifyException, ClassNotFoundException, ParseException {
        io.github.hhy50.linker.annotations.Field.Getter getter = method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Field.Getter.class);
        io.github.hhy50.linker.annotations.Field.Setter setter = method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Field.Setter.class);
        io.github.hhy50.linker.annotations.Method.Expr expr = method.getDeclaredAnnotation(io.github.hhy50.linker.annotations.Method.Expr.class);

        AbsMethodDefine absMethodDefine = new AbsMethodDefine(method);
        if (getter != null || setter != null) {
            String exprStr = Util.getOrElseDefault(Optional.ofNullable(getter).map(io.github.hhy50.linker.annotations.Field.Getter::value)
                    .orElseGet(() -> setter.value()), method.getName());
            Tokens tokens = tokenParser.parse(exprStr);
            absMethodDefine.fieldRef = parseFieldExpr(targetClass, tokens);
        } else if (absMethodDefine.hasConstructor()) {
            String[] argsType = parseArgsType(method, null, true);
            Constructor<?> constructor = ReflectUtil.matchConstructor(targetClass, argsType);
            if (constructor == null) {
                throw new ParseException("Constructor not found in class '"+targetClass+"' with args "+Arrays.toString(argsType));
            }
            absMethodDefine.methodRef = new ConstructorRef(targetRoot, method.getName(), constructor);
        } else {
            String methodExpr = Optional.ofNullable(expr).map(io.github.hhy50.linker.annotations.Method.Expr::value).orElseGet(() ->
                    method.getName()+"("+IntStream.range(0, method.getParameterCount()).mapToObj(i -> "$"+i).collect(Collectors.joining(","))+")"
            );
            absMethodDefine.methodRef = parseMethodExpr(method, tokenParser.parse(methodExpr));
        }
        return absMethodDefine;
    }

    private MethodRef parseMethodExpr(Method methodDefine, final Tokens tokens) throws ClassNotFoundException {
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

        ArgsDepAnalysis argsDepAnalysis = new ArgsDepAnalysis(methodDefine.getParameterTypes());
        Class<?> curType = targetClass;
        ArrayList<MethodRef> methods = new ArrayList<>();
        for (SplitToken splitToken : tokenList) {
            Tokens fieldToken = (Tokens) splitToken.prefix;
            MethodToken methodToken = (MethodToken) splitToken.suffix;

            FieldRef owner = null;
            if (fieldToken != null && fieldToken.size() > 0) {
                owner = parseFieldExpr(curType, fieldToken);
            } else if (methods.size() > 0) {
                MethodRef methodRef = methods.get(methods.size()-1);
                MethodTmpFieldRef fieldRef = new MethodTmpFieldRef(methodRef, methodRef.getName());
                fieldRef.setActualType(curType);
                owner = fieldRef;
            } else {
                owner = targetRoot;
            }
            if (methodToken != null) {
                String[] argsType = parseArgsType(methodDefine, methodToken, false);
                Method method = ReflectUtil.matchMethod(owner.getActualType(), methodToken.methodName, invokeSuper, argsType);

                MethodRef m;
                if (method != null) {
                    m = new EarlyMethodRef(owner, method);
                    curType = method.getReturnType();
                } else {
                    m = new RuntimeMethodRef(owner, methodToken.methodName, argsType)
                            .setAutolink(AnnotationUtils.isAutolink(methodDefine));
                    curType = Object.class;
                }
                m.setSuperClass(invokeSuper);
                methods.add(m);
                argsDepAnalysis.analyse(m.getMethodType(), methodToken.getArgsToken());
            }
        }
        return new MethodExprRef(methodDefine, methods, argsDepAnalysis);
    }

    private FieldRef parseFieldExpr(Class<?> rootType, final Tokens tokens) throws ClassNotFoundException {
        Class<?> currentType = rootType;
        FieldRef lastField = targetRoot;
        String fullField = null;
        for (Token item : tokens) {
            if (!(item instanceof FieldToken)) {
                throw new ParseException("Field token expected, but "+item.getClass().getSimpleName()+" found");
            }
            FieldToken token = (FieldToken) item;
            String fieldName = token.fieldName;
            List<Object> index = token.getIndexVal();
            Field earlyField = token.getField(currentType);
            currentType = earlyField == null ? Object.class : Util.expandIndexType(index, earlyField.getType());
            fullField = Optional.ofNullable(fullField).map(i -> i+"."+fieldName).orElse(fieldName);
            // 使用@Typed指定的类型
            Class<?> assignedType = getFieldTyped(fullField, fieldName);
            if (assignedType != null) {
                if (earlyField != null && !ClassUtil.isAssignableFrom(assignedType, earlyField.getType())) {
                    throw new ClassTypeNotMatchException(assignedType.getName(), earlyField.getType().getName());
                }
                currentType = index == null ? assignedType : Util.expandIndexType(index, assignedType);;
            }
            lastField = earlyField != null ? new EarlyFieldRef(lastField, earlyField, assignedType) : new RuntimeFieldRef(lastField, fieldName);
            lastField.setFullName(fullField);
            lastField.setNullable(token.isNullable());
//            lastField.setDefaultValue();
            designateStatic(lastField);
            if (index != null && index.size() > 0) {
                lastField = new FieldIndexRef(lastField, index);
            }
        }
        return lastField;
    }

    private String[] parseArgsType(Method methodDefine, MethodToken methodToken, boolean isConstructor) {
        if (isConstructor) {
            return Arrays.stream(methodDefine.getParameters())
                    .map(ParseUtil::getRawType).toArray(String[]::new);
        }
        List<ArgType> args = methodToken.getArgsType();
        return args.stream()
                .map(item -> {
                    Type type = item.getType(this, methodDefine);
                    return type;
                })
                .map(Type::getClassName).toArray(String[]::new);
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
            throw new VerifyException("method ["+method.getDeclaringClass()+"@"+method.getName()+"] cannot have two annotations ["+
                    uniques.stream().map(Class::getSimpleName).collect(Collectors.joining(", @", "@", ""))+"]");
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
                    throw new VerifyException("method ["+method.getDeclaringClass()+"@"+method.getName()+"] verify failed");
                }
            }
        }
    }
}