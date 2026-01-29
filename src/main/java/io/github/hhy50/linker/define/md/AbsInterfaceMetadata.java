package io.github.hhy50.linker.define.md;

import io.github.hhy50.linker.annotations.Runtime;
import io.github.hhy50.linker.annotations.Typed;
import io.github.hhy50.linker.exceptions.VerifyException;
import io.github.hhy50.linker.generate.builtin.SetTargetProvider;
import io.github.hhy50.linker.util.AnnotationUtils;
import io.github.hhy50.linker.util.StringUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

public class AbsInterfaceMetadata {

    /**
     *
     */
    private final java.lang.Class<?> interfaceClass;

    private final List<Annotation> annotations = new ArrayList<>();

    private final Map<String, String> typedToken = new HashMap<>();

    private final Map<String, Boolean> staticToken = new HashMap<>();

    private final boolean runtime;

    private final Class<?> targetClass;

    public AbsInterfaceMetadata(Class<?> define, Class<?> targetClass) {
        this.interfaceClass = define;
        this.runtime = AnnotationUtils.isRuntime(define)
                || SetTargetProvider.class.isAssignableFrom(define)
                || define == Object.class;
        this.targetClass = targetClass;
        for (Annotation anno : define.getDeclaredAnnotations()) {
            addAnnotation(anno);
        }
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
        if (annotation instanceof io.github.hhy50.linker.annotations.Runtime.Static) {
            io.github.hhy50.linker.annotations.Runtime.Static staticAnno = (Runtime.Static) annotation;
            for (String s : staticAnno.name()) {
                Boolean isStatic = this.staticToken.put(s, staticAnno.value());
                if (isStatic != null && !isStatic.equals(staticAnno.value())) {
                    throw new VerifyException("@Static of field '" + s + "' defined twice is inconsistent");
                }
            }
            this.annotations.add(annotation);
        }
    }

    public boolean isRuntime() {
        return this.runtime;
    }

    public List<Method> getMethods() {
        return Arrays.asList(interfaceClass.getMethods());
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Boolean isDesignateStatic(String tokenVal) {
        return staticToken.get(tokenVal);
    }

    public String getTyped(String full, String tokenVal) {
        return Stream.of(full, tokenVal)
                .filter(StringUtil::isNotEmpty)
                .map(this.typedToken::get)
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
    }
}
