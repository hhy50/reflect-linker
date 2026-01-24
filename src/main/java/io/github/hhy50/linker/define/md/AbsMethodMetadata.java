package io.github.hhy50.linker.define.md;

import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Runtime;
import io.github.hhy50.linker.annotations.Typed;
import io.github.hhy50.linker.exceptions.VerifyException;
import io.github.hhy50.linker.util.AnnotationUtils;
import io.github.hhy50.linker.util.StringUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    private Map<String, Boolean> staticToken = new HashMap<>();

    private String invokeSuper;

    public AbsMethodMetadata(AbsInterfaceMetadata parent, java.lang.reflect.Method reflect) {
        this.parent = parent;
        this.reflect = reflect;
    }

    public void setUniqueAnnotation(Annotation uniqueAnno) {
        if (this.uniqueAnno != null) {
            throw new VerifyException("method [" + reflect.getDeclaringClass() + "@" + reflect.getName() + "] cannot have two annotations [" +
                    this.uniqueAnno.annotationType() + ", " + uniqueAnno.annotationType() + "]");
        }
        this.uniqueAnno = uniqueAnno;
    }

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
                Boolean isStatic = this.staticToken.put(s, staticAnno.value());
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

    public boolean isAutolink() {
        return AnnotationUtils.isAutolink(reflect);
    }

    public java.lang.reflect.Method getReflect() {
        return reflect;
    }

    public String getExpr() {
        if (uniqueAnno instanceof Method.Expr) {
            return ((Method.Expr) uniqueAnno).value();
        } else if (uniqueAnno instanceof Field.Getter) {
            return ((Field.Getter) uniqueAnno).value();
        } else if (uniqueAnno instanceof Field.Setter) {
            return ((Field.Setter) uniqueAnno).value();
        }

        return reflect.getName()+"("+ IntStream.range(0, reflect.getParameterCount())
                .mapToObj(i -> "$"+i).collect(Collectors.joining(","))+")";
    }

    public String getName() {
        return reflect.getName();
    }

    public String getInvokeSuper() {
        return invokeSuper;
    }

    public Parameter[] getParameters() {
        return reflect.getParameters();
    }

    public Boolean isDesignateStatic(String tokenVal) {
        Boolean b = staticToken.get(tokenVal);
        if (b == null) {
            b = parent.isDesignateStatic(tokenVal);
        }
        return b;
    }

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

    public boolean isSetter() {
        return uniqueAnno instanceof Field.Setter;
    }

    public boolean isConstructor() {
        return uniqueAnno instanceof io.github.hhy50.linker.annotations.Method.Constructor;
    }
}
