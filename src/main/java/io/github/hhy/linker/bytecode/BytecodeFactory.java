package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.bytecode.getter.Getter;
import io.github.hhy.linker.bytecode.getter.GetterWrapper;
import io.github.hhy.linker.bytecode.setter.Setter;
import io.github.hhy.linker.bytecode.setter.SetterWrapper;
import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.define.FieldRef;
import org.objectweb.asm.Type;


public class BytecodeFactory {

    public static MethodHandle generateGetter(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, FieldRef targetPoint) {
        FieldRef prev = targetPoint.getPrev();
        while (prev != null) {
            prev.getter = classBuilder.defineGetter(prev, null);
            prev = prev.getPrev();
        }
        Getter getter = classBuilder.defineGetter(targetPoint, Type.getType(methodDefine.define));
        return new GetterWrapper(getter, methodDefine.define);
    }

    public static MethodHandle generateSetter(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, FieldRef targetPoint) {
        FieldRef prev = targetPoint.getPrev();
        while (prev != null) {
            prev.getter = classBuilder.defineGetter(prev, null);
            prev = prev.getPrev();
        }
        Setter getter = classBuilder.defineSetter(targetPoint, Type.getType(methodDefine.define));
        return new SetterWrapper(getter, methodDefine);
    }
}
