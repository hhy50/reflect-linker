package io.github.hhy.linker.define;

import io.github.hhy.linker.enums.TargetPointType;

import java.lang.reflect.Method;

public class MethodDefine {
    public Method define;
    public TargetPoint targetPoint;
    public TargetPointType targetPointType;

    public MethodDefine(Method method) {
        this.define = method;
    }
}
