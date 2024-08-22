package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.bytecode.getter.Getter;
import io.github.hhy.linker.bytecode.getter.GetterWrapper;
import io.github.hhy.linker.bytecode.setter.Setter;
import io.github.hhy.linker.bytecode.setter.SetterWrapper;
import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.define.field2.FieldRef;


public class BytecodeFactory {

    public static MethodHandle generateGetter(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, FieldRef fieldRef) {
        FieldRef prev = fieldRef.getPrev();
        while (prev != null) {
            classBuilder.defineGetter(prev.getFullName(), prev);
            prev = prev.getPrev();
        }
        Getter<?> getter = classBuilder.defineGetter(fieldRef.getFullName(), fieldRef);
        return new GetterWrapper(getter, fieldRef, methodDefine.define);
    }

    public static MethodHandle generateSetter(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, FieldRef fieldRef) {
        FieldRef prev = fieldRef.getPrev();
        while (prev != null) {
            classBuilder.defineGetter(prev.getFullName(), prev);
            prev = prev.getPrev();
        }

        Setter setter = classBuilder.defineSetter(fieldRef.getFullName(), fieldRef);
        return new SetterWrapper(setter, fieldRef, methodDefine);
    }
}
