package io.github.hhy50.linker.define;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbsInterfaceMetadata {

    /**
     *
     */
    private java.lang.Class<?> clazz;


    private List<Annotation> annotations = new ArrayList<>();

    private Map<String, String> types = new HashMap<>();

    private boolean isRuntime;

    private boolean isAutolink;

    public AbsInterfaceMetadata(Class<?> clazz) {
        this.clazz = clazz;
    }
}
