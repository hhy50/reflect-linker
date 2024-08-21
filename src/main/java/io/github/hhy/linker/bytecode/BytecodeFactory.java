package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.bytecode.getter.Getter;
import io.github.hhy.linker.bytecode.getter.GetterWrapper;
import io.github.hhy.linker.bytecode.setter.Setter;
import io.github.hhy.linker.bytecode.setter.SetterWrapper;
import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.define.field2.FieldRef;
import org.objectweb.asm.Type;


public class BytecodeFactory {

    public static MethodHandle generateGetter(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, FieldRef fieldRef) {
        String fullName = fieldRef.getFullName();

        FieldRef prev = targetPoint.getPrev();
        while (prev != null) {
            prev.getter = classBuilder.defineGetter(prev, Type.getMethodType(prev.getType()));
            prev = prev.getPrev();
        }
        Getter getter = classBuilder.defineGetter(targetPoint, Type.getMethodType(targetPoint.getType()));
        return new GetterWrapper(getter, methodDefine.define);
    }

    public static MethodHandle generateSetter(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, FieldRef targetPoint) {
        FieldRef prev = targetPoint.getPrev();
        while (prev != null) {
            prev.getter = classBuilder.defineGetter(prev, Type.getMethodType(Type.VOID_TYPE, prev.getType()));
            prev = prev.getPrev();
        }
        Setter setter = classBuilder.defineSetter(targetPoint, Type.getMethodType(Type.VOID_TYPE, targetPoint.getType()));
        return new SetterWrapper(setter, methodDefine);
    }
}
