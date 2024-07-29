package io.github.hhy.linker.define;

import java.util.List;

public class InvokeClassDefine {
    private String bindTarget;
    private List<MethodDefine> methodDefines;

    public String getBindTarget() {
        return bindTarget;
    }

    public void setBindTarget(String bindTarget) {
        this.bindTarget = bindTarget;
    }

    public List<MethodDefine> getMethodDefines() {
        return methodDefines;
    }

    public void setMethodDefines(List<MethodDefine> methodDefines) {
        this.methodDefines = methodDefines;
    }
}
