package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.define.AbsMethod;
import io.github.hhy50.linker.define.md.AbsInterfaceMetadata;

import java.util.List;

public class BuilderParameter {
    private AbsInterfaceMetadata classMetadata;
    private Class<?> targetClass;
    private String implClassName;
    private List<Class<?>> interfaces;
    private List<AbsMethod> absMethods;

    public void setClassMetadata(AbsInterfaceMetadata classMetadata) {
        this.classMetadata = classMetadata;
    }

    public AbsInterfaceMetadata getClassMetadata() {
        return classMetadata;
    }

    public void setImplClassName(String implClassName) {
        this.implClassName = implClassName;
    }

    public String getImplClassName() {
        return implClassName;
    }

    public void setAbsMethods(List<AbsMethod> absMethods) {
        this.absMethods = absMethods;
    }

    public List<AbsMethod> getAbsMethods() {
        return absMethods;
    }

    public void setInterfaces(List<Class<?>> interfaces) {
        this.interfaces = interfaces;
    }

    public List<Class<?>> getInterfaces() {
        return interfaces;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }
}
