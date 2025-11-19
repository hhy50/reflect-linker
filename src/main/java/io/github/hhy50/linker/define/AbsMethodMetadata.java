package io.github.hhy50.linker.define;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.hhy50.linker.exceptions.VerifyException;

public class AbsMethodMetadata {

    AbsInterfaceMetadata parent;

    /**
     *
     */
    private java.lang.reflect.Method reflect;

    /**
     *
     */
    private Annotation uniqueAnno;

    private List<Annotation> annotations = new ArrayList<>();

    private Map<String, String> types = new HashMap<>();

    private boolean isRuntime;

    private boolean isAutolink;

    public AbsMethodMetadata(AbsInterfaceMetadata parent, Method reflect) {
        this.parent = parent;
        this.reflect = reflect;
    }

    public void setReflectMethod(Method reflect) {
        this.reflect = reflect;
    }
    
    public void setUniqueAnnotation(Annotation uniqueAnno) {
        if (this.uniqueAnno != null) {
            throw new VerifyException("method [" + reflect.getDeclaringClass() + "@" + reflect.getName()+ "] cannot have two annotations [" +
                this.uniqueAnno.annotationType() + ", "+ uniqueAnno.annotationType() + "]");
        }
        this.uniqueAnno = uniqueAnno;
    }

    public void addAnnotation(Annotation annotation) {
        this.annotations.add(annotation);
    }
}
