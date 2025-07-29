package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.define.AbsMethodDefine;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.getter.Getter;
import io.github.hhy50.linker.generate.getter.GetterDecorator;
import io.github.hhy50.linker.generate.invoker.InvokerDecorator;
import io.github.hhy50.linker.generate.setter.Setter;
import io.github.hhy50.linker.generate.setter.SetterDecorator;


/**
 * The type Bytecode factory.
 */
public class BytecodeFactory {

    /**
     * Generate getter method handle.
     *
     * @param classBuilder    the class builder
     * @param absMethodDefine the method define
     * @param fieldRef        the field ref
     * @return the method handle
     */
    public static MethodHandle generateGetter(InvokeClassImplBuilder classBuilder, AbsMethodDefine absMethodDefine, FieldRef fieldRef) {
        FieldRef prev = fieldRef.getPrev();
        while (prev != null) {
            classBuilder.defineGetter(prev.getUniqueName(), prev);
            prev = prev.getPrev();
        }
        Getter getter = classBuilder.defineGetter(fieldRef.getUniqueName(), fieldRef);
        return new GetterDecorator(getter, fieldRef, absMethodDefine);
    }

    /**
     * Generate setter method handle.
     *
     * @param classBuilder    the class builder
     * @param absMethodDefine the method define
     * @param fieldRef        the field ref
     * @return the method handle
     */
    public static MethodHandle generateSetter(InvokeClassImplBuilder classBuilder, AbsMethodDefine absMethodDefine, FieldRef fieldRef) {
        FieldRef prev = fieldRef.getPrev();
        while (prev != null) {
            classBuilder.defineGetter(prev.getUniqueName(), prev);
            prev = prev.getPrev();
        }

        Setter setter = classBuilder.defineSetter(fieldRef.getUniqueName(), fieldRef);
        return new SetterDecorator(setter, fieldRef, absMethodDefine);
    }

    /**
     * Generate invoker method handle.
     *
     * @param classBuilder    the class builder
     * @param absMethodDefine the method define
     * @param methodRef       the method ref
     * @return the method handle
     */
    public static MethodHandle generateInvoker(InvokeClassImplBuilder classBuilder, AbsMethodDefine absMethodDefine, MethodExprRef methodRef) {
        for (MethodRef method : methodRef.getMethods()) {
            FieldRef owner = method.getOwner();
            classBuilder.defineGetter(owner.getUniqueName(), owner);
        }
        return new InvokerDecorator(classBuilder.defineInvoker(methodRef), absMethodDefine);
    }
}
