package io.github.hhy50.linker.define.md;

import java.lang.annotation.Annotation;
import java.util.*;

import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Runtime;
import io.github.hhy50.linker.annotations.Typed;
import io.github.hhy50.linker.exceptions.VerifyException;

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

    private boolean constructor;

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
    }

    public boolean isAutolink() {
        return false;
    }

    public boolean isConstructor() {
        return uniqueAnno instanceof io.github.hhy50.linker.annotations.Method.Constructor;
    }

    public java.lang.reflect.Method getMethod() {
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
//        String methodExpr = Optional.ofNullable(expr).map(io.github.hhy50.linker.annotations.Method.Expr::value)
//                .orElseGet(() -> method.getName() + "(" + IntStream.range(0, method.getParameterCount())
//                        .mapToObj(i -> "$" + i).collect(Collectors.joining(",")) + ")");
        return null;
    }
}
