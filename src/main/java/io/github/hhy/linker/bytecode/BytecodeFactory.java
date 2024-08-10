package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.define.RuntimeField;


public class BytecodeFactory {

    public static MethodHandle generateGetter(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, RuntimeField targetPoint) {
        RuntimeField prev = targetPoint.getPrev();
        while (prev != null) {
            prev.getter = classBuilder.defineGetter(prev);
            prev = prev.getPrev();
        }
        Getter getter = classBuilder.defineGetter(targetPoint);
        getter.methodDefine = methodDefine;
        getter.define(classBuilder);
        return getter;
    }
}
