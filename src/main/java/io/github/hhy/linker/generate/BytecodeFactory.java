package io.github.hhy.linker.generate;

import io.github.hhy.linker.define.MethodDefine;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.define.method.EarlyMethodRef;
import io.github.hhy.linker.define.method.MethodRef;
import io.github.hhy.linker.generate.getter.Getter;
import io.github.hhy.linker.generate.getter.GetterDecorator;
import io.github.hhy.linker.generate.invoker.EarlyMethodInvoker;
import io.github.hhy.linker.generate.invoker.Invoker;
import io.github.hhy.linker.generate.invoker.InvokerDecorator;
import io.github.hhy.linker.generate.setter.Setter;
import io.github.hhy.linker.generate.setter.SetterDecorator;


public class BytecodeFactory {

    public static MethodHandle generateGetter(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, FieldRef fieldRef) {
        FieldRef prev = fieldRef.getPrev();
        while (prev != null) {
            classBuilder.defineGetter(prev.getUniqueName(), prev);
            prev = prev.getPrev();
        }
        Getter<?> getter = classBuilder.defineGetter(fieldRef.getUniqueName(), fieldRef);
        return new GetterDecorator(getter, fieldRef, methodDefine.define);
    }

    public static MethodHandle generateSetter(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, FieldRef fieldRef) {
        FieldRef prev = fieldRef.getPrev();
        while (prev != null) {
            classBuilder.defineGetter(prev.getUniqueName(), prev);
            prev = prev.getPrev();
        }

        Setter<?> setter = classBuilder.defineSetter(fieldRef.getUniqueName(), fieldRef);
        return new SetterDecorator(setter, fieldRef, methodDefine);
    }

    public static MethodHandle generateInvoker(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, MethodRef methodRef) {
        FieldRef owner = methodRef.getOwner();
        classBuilder.defineGetter(owner.getUniqueName(), owner);

        FieldRef prev = owner.getPrev();
        while (prev != null) {
            classBuilder.defineGetter(prev.getUniqueName(), prev);
            prev = prev.getPrev();
        }
        Invoker<?> invoker = new EarlyMethodInvoker(classBuilder.getClassName(), (EarlyMethodRef) methodRef);
        return new InvokerDecorator(classBuilder.getClassName(), invoker, methodDefine);
    }
}
