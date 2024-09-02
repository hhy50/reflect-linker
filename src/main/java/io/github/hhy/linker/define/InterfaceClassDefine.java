package io.github.hhy.linker.define;

import java.util.List;


public class InterfaceClassDefine {
    private Class<?> define;
    private Class<?> targetClass;
    private List<MethodDefine> methodDefines;

    public InterfaceClassDefine(Class<?> define, Class<?> targetClass, List<MethodDefine> methodDefines) {
        this.define = define;
        this.targetClass = targetClass;
        this.methodDefines = methodDefines;
    }

    public Class<?> getDefine() {
        return define;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public List<MethodDefine> getMethodDefines() {
        return methodDefines;
    }
}
