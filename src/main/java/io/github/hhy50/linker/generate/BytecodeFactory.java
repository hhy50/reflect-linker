package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.define.MethodDefine;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.getter.Getter;
import io.github.hhy50.linker.generate.getter.GetterDecorator;
import io.github.hhy50.linker.generate.invoker.Invoker;
import io.github.hhy50.linker.generate.invoker.InvokerDecorator;
import io.github.hhy50.linker.generate.setter.Setter;
import io.github.hhy50.linker.generate.setter.SetterDecorator;


/**
 * <p>BytecodeFactory class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class BytecodeFactory {

    /**
     * <p>generateGetter.</p>
     *
     * @param classBuilder a {@link io.github.hhy50.linker.generate.InvokeClassImplBuilder} object.
     * @param methodDefine a {@link io.github.hhy50.linker.define.MethodDefine} object.
     * @param fieldRef a {@link io.github.hhy50.linker.define.field.FieldRef} object.
     * @return a {@link io.github.hhy50.linker.generate.MethodHandle} object.
     */
    public static MethodHandle generateGetter(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, FieldRef fieldRef) {
        FieldRef prev = fieldRef.getPrev();
        while (prev != null) {
            classBuilder.defineGetter(prev.getUniqueName(), prev);
            prev = prev.getPrev();
        }
        Getter<?> getter = classBuilder.defineGetter(fieldRef.getUniqueName(), fieldRef);
        return new GetterDecorator(getter, fieldRef, methodDefine.define);
    }

    /**
     * <p>generateSetter.</p>
     *
     * @param classBuilder a {@link io.github.hhy50.linker.generate.InvokeClassImplBuilder} object.
     * @param methodDefine a {@link io.github.hhy50.linker.define.MethodDefine} object.
     * @param fieldRef a {@link io.github.hhy50.linker.define.field.FieldRef} object.
     * @return a {@link io.github.hhy50.linker.generate.MethodHandle} object.
     */
    public static MethodHandle generateSetter(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, FieldRef fieldRef) {
        FieldRef prev = fieldRef.getPrev();
        while (prev != null) {
            classBuilder.defineGetter(prev.getUniqueName(), prev);
            prev = prev.getPrev();
        }

        Setter<?> setter = classBuilder.defineSetter(fieldRef.getUniqueName(), fieldRef);
        return new SetterDecorator(setter, fieldRef, methodDefine);
    }

    /**
     * <p>generateInvoker.</p>
     *
     * @param classBuilder a {@link io.github.hhy50.linker.generate.InvokeClassImplBuilder} object.
     * @param methodDefine a {@link io.github.hhy50.linker.define.MethodDefine} object.
     * @param methodRef a {@link io.github.hhy50.linker.define.method.MethodRef} object.
     * @return a {@link io.github.hhy50.linker.generate.MethodHandle} object.
     */
    public static MethodHandle generateInvoker(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, MethodRef methodRef) {
        FieldRef owner = methodRef.getOwner();
        classBuilder.defineGetter(owner.getUniqueName(), owner);

        FieldRef prev = owner.getPrev();
        while (prev != null) {
            classBuilder.defineGetter(prev.getUniqueName(), prev);
            prev = prev.getPrev();
        }

        Invoker<?> invoker = classBuilder.defineInvoker(methodRef);
        return new InvokerDecorator(classBuilder.getClassName(), invoker, methodDefine);
    }
}
