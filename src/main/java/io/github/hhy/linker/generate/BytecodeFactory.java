package io.github.hhy.linker.generate;

import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.generate.getter.Getter;
import io.github.hhy.linker.generate.getter.GetterWrapper;
import io.github.hhy.linker.generate.setter.Setter;
import io.github.hhy.linker.generate.setter.SetterWrapper;


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

        Setter<?> setter = classBuilder.defineSetter(fieldRef.getFullName(), fieldRef);
        return new SetterWrapper(setter, fieldRef, methodDefine);
    }
}
