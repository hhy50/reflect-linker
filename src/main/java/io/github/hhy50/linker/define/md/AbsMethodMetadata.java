package io.github.hhy50.linker.define.md;

import io.github.hhy50.linker.annotations.*;
import io.github.hhy50.linker.annotations.Runtime;
import io.github.hhy50.linker.exceptions.VerifyException;
import io.github.hhy50.linker.util.AnnotationUtils;
import io.github.hhy50.linker.util.StringUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The type Abs method metadata.
 */
public class AbsMethodMetadata {

    private final AbsInterfaceMetadata parent;

    /**
     *
     */
    private final java.lang.reflect.Method reflect;

    /**
     *
     */
    private Annotation uniqueAnno;

    private List<Annotation> annotations = new ArrayList<>();

    private Map<String, String> typedToken = new HashMap<>();

    private String invokeSuper;

    /**
     * Instantiates a new Abs method metadata.
     *
     * @param parent  the parent
     * @param reflect the reflect
     */
    public AbsMethodMetadata(AbsInterfaceMetadata parent, java.lang.reflect.Method reflect) {
        this.parent = parent;
        this.reflect = reflect;
    }

    /**
     * Sets unique annotation.
     *
     * @param uniqueAnno the unique anno
     */
    public void setUniqueAnnotation(Annotation uniqueAnno) {
        if (this.uniqueAnno != null) {
            throw new VerifyException("method [" + reflect.getDeclaringClass() + "@" + reflect.getName() + "] cannot have two annotations [" +
                    this.uniqueAnno.annotationType() + ", " + uniqueAnno.annotationType() + "]");
        }
        if (uniqueAnno instanceof Field.StaticGetter) {
            String f = ((Field.StaticGetter) uniqueAnno).value();
            parent.staticToken.put(f, true);
        }  else if (uniqueAnno instanceof Field.StaticSetter) {
            String f = ((Field.StaticSetter) uniqueAnno).value();
            parent.staticToken.put(f, true);
        }
        this.uniqueAnno = uniqueAnno;
    }

    /**
     * Add annotation.
     *
     * @param annotation the annotation
     */
    public void addAnnotation(Annotation annotation) {
        if (annotation instanceof Typed) {
            Typed typed = (Typed) annotation;
            String type = this.typedToken.put(typed.name(), typed.value());
            if (type != null && !type.equals(typed.value())) {
                throw new VerifyException(
                        "@Typed of field '" + typed.name() + "' defined twice is inconsistent");
            }
            this.annotations.add(annotation);
        }
        if (annotation instanceof Runtime.Static) {
            Runtime.Static staticAnno = (Runtime.Static) annotation;
            for (String s : staticAnno.name()) {
                Boolean isStatic = parent.staticToken.put(s, staticAnno.value());
                if (isStatic != null && !isStatic.equals(staticAnno.value())) {
                    throw new VerifyException("@Static of field '" + s + "' defined twice is inconsistent");
                }
            }
            this.annotations.add(annotation);
        }
        if (annotation instanceof Method.InvokeSuper) {
            this.invokeSuper = ((Method.InvokeSuper) annotation).value();
        }
    }

    /**
     * Is autolink boolean.
     *
     * @return the boolean
     */
    public boolean isAutolink() {
        if (AnnotationUtils.isAutolink(reflect)) {
            return true;
        }
        for (Class<?> parameterType : reflect.getParameterTypes()) {
            if (AnnotationUtils.isAutolink(parameterType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets reflect.
     *
     * @return the reflect
     */
    public java.lang.reflect.Method getReflect() {
        return reflect;
    }

    /**
     * Gets expr.
     *
     * @return the expr
     */
    public String getExpr() {
        if (uniqueAnno instanceof Method.Expr) {
            return ((Method.Expr) uniqueAnno).value();
        } else if (uniqueAnno instanceof Field.Getter) {
            return ((Field.Getter) uniqueAnno).value();
        } else if (uniqueAnno instanceof Field.Setter) {
            return ((Field.Setter) uniqueAnno).value();
        } else if (uniqueAnno instanceof Field.StaticGetter) {
            return ((Field.StaticGetter) uniqueAnno).value();
        } else if (uniqueAnno instanceof Field.StaticSetter) {
            return ((Field.StaticSetter) uniqueAnno).value();
        }

        return reflect.getName()+"("+ IntStream.range(0, reflect.getParameterCount())
                .mapToObj(i -> "$"+i).collect(Collectors.joining(","))+")";
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return reflect.getName();
    }

    /**
     * Gets invoke super.
     *
     * @return the invoke super
     */
    public String getInvokeSuper() {
        return invokeSuper;
    }

    /**
     * Get parameters parameter [ ].
     *
     * @return the parameter [ ]
     */
    public Parameter[] getParameters() {
        return reflect.getParameters();
    }

    /**
     * Is designate static boolean.
     *
     * @param tokenVal the token val
     * @return the boolean
     */
    public Boolean isDesignateStatic(String tokenVal) {
        Boolean b = parent.staticToken.get(tokenVal);
        if (b == null) {
            b = parent.isDesignateStatic(tokenVal);
        }
        return b;
    }

    /**
     * Gets typed.
     *
     * @param full     the full
     * @param tokenVal the token val
     * @return the typed
     */
    public String getTyped(String full, String tokenVal) {
        String typed = Stream.of(full, tokenVal)
                .filter(StringUtil::isNotEmpty)
                .map(this.typedToken::get)
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
        if (typed == null) {
            typed = parent.getTyped(full, tokenVal);
        }
        return typed;
    }

    /**
     * Is setter boolean.
     *
     * @return the boolean
     */
    public boolean isSetter() {
        return uniqueAnno instanceof Field.Setter || uniqueAnno instanceof Field.StaticSetter;
    }

    /**
     * Is constructor boolean.
     *
     * @return the boolean
     */
    public boolean isConstructor() {
        return uniqueAnno instanceof io.github.hhy50.linker.annotations.Method.Constructor;
    }

    public Class<? extends Throwable>[] getExceptions() {
        TrycatchException declaredAnnotation = this.reflect.getDeclaredAnnotation(TrycatchException.class);
        if (declaredAnnotation != null) {
            return declaredAnnotation.value();
        }
        return new Class[0];
    }
}
