package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.define.MethodDefine;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.ConstructorRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.constructor.Constructor;
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
     * @param classBuilder the class builder
     * @param methodDefine the method define
     * @param fieldRef     the field ref
     * @return the method handle
     */
    public static MethodHandle generateGetter(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, FieldRef fieldRef) {
        FieldRef prev = fieldRef.getPrev();
        while (prev != null) {
            classBuilder.defineGetter(prev.getUniqueName(), prev);
            prev = prev.getPrev();
        }
        Getter<?> getter = classBuilder.defineGetter(fieldRef.getUniqueName(), fieldRef);
        return new GetterDecorator(getter, fieldRef, methodDefine);
    }

    /**
     * Generate setter method handle.
     *
     * @param classBuilder the class builder
     * @param methodDefine the method define
     * @param fieldRef     the field ref
     * @return the method handle
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
     * Generate invoker method handle.
     *
     * @param classBuilder the class builder
     * @param methodDefine the method define
     * @param methodRef    the method ref
     * @return the method handle
     */
    public static MethodHandle generateInvoker(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, MethodRef methodRef) {
        FieldRef owner = methodRef.getOwner();
        classBuilder.defineGetter(owner.getUniqueName(), owner);

        FieldRef prev = owner.getPrev();
        while (prev != null) {
            classBuilder.defineGetter(prev.getUniqueName(), prev);
            prev = prev.getPrev();
        }
        return new InvokerDecorator(classBuilder.defineInvoker(methodRef), methodDefine);
    }

    /**
     * Generate constructor method handle.
     *
     * @param classBuilder   the class builder
     * @param methodDefine   the method define
     * @param constructorRef the class ConstructorRef
     * @return method handle
     */
    public static MethodHandle generateConstructor(InvokeClassImplBuilder classBuilder, MethodDefine methodDefine, ConstructorRef constructorRef) {
        return new InvokerDecorator(
                new Constructor(classBuilder.getClassName(), constructorRef), methodDefine);
    }
}
