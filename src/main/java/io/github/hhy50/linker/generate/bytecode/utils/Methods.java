package io.github.hhy50.linker.generate.bytecode.utils;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.SmartMethodDescriptor;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.action.SmartMethodInvokeAction;
import io.github.hhy50.linker.util.ReflectUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * The type Methods.
 */
public class Methods {

    /**
     * Invoke method invoke action.
     *
     * @param clazz    the clazz
     * @param name     the name
     * @param argsType the args type
     * @return the method invoke action
     * @throws NoSuchMethodException the no such method exception
     */
    public static MethodInvokeAction invoke(Class<?> clazz, String name, Class<?>... argsType) throws NoSuchMethodException {
        Method method = ReflectUtil.matchMethod(clazz, name, null, Arrays.stream(argsType).map(Class::getName).toArray(String[]::new));
        if (method == null) {
            throw new NoSuchMethodException("not found method '"+name+"' in class "+clazz.getName());
        }
        return new MethodInvokeAction(MethodDescriptor.of(method));
    }

    /**
     * Invoke interface method invoke action.
     *
     * @param md the md
     * @return method invoke action
     */
    public static MethodInvokeAction invokeInterface(MethodDescriptor md) {
        return new InvokeInterface(md);
    }

    /**
     * Invoke super method invoke action.
     *
     * @return the method invoke action
     */
    public static MethodInvokeAction invokeSuper() {
        return invokeSuper(null, null);
    }

    /**
     * Invoke super method invoke action.
     *
     * @param superOwner the super owner
     * @return the method invoke action
     */
    public static MethodInvokeAction invokeSuper(String superOwner) {
        return invokeSuper(superOwner, null);
    }

    /**
     * Invoke super method invoke action.
     *
     * @param md the md
     * @return the method invoke action
     */
    public static MethodInvokeAction invokeSuper(MethodDescriptor md) {
        return invokeSuper(null, md);
    }

    /**
     * Invoke super method invoke action.
     *
     * @param superOwner the super owner
     * @param md         the md
     * @return the method invoke action
     */
    public static MethodInvokeAction invokeSuper(String superOwner, MethodDescriptor md) {
        return new InvokeSupper(superOwner, md);
    }

    /**
     * Invoke method invoke action.
     *
     * @param descriptor the descriptor
     * @return method invoke action
     */
    public static MethodInvokeAction invoke(MethodDescriptor descriptor) {
        return new MethodInvokeAction(descriptor);
    }

    /**
     * Invoke method invoke action.
     *
     * @param methodName the method name
     * @param methodType the method type
     * @return method invoke action
     */
    public static MethodInvokeAction invoke(String methodName, Type methodType) {
        return new SmartMethodInvokeAction(new SmartMethodDescriptor(methodName, methodType));
    }

    /**
     * The type Invoke interface.
     */
    static class InvokeInterface extends MethodInvokeAction {
        /**
         * Instantiates a new Method invoke action.
         *
         * @param methodDescriptor the method holder
         */
        public InvokeInterface(MethodDescriptor methodDescriptor) {
            super(methodDescriptor);
        }

        @Override
        public int getOpCode() {
            return Opcodes.INVOKEINTERFACE;
        }
    }

    /**
     * invokeSuper method
     */
    static class InvokeSupper extends SmartMethodInvokeAction {

        private String superOwner;

        /**
         * Instantiates a new Invoke supper.
         *
         * @param superOwner the super owner
         * @param md         the md
         */
        public InvokeSupper(String superOwner, MethodDescriptor md) {
            super(md);
            this.instance = LoadAction.LOAD0;
            this.superOwner = superOwner;
        }

        @Override
        public MethodDescriptor getMethodDescriptor(MethodBody body) {
            MethodDescriptor descriptor = super.getMethodDescriptor(body);
            if (this.superOwner == null) {
                this.superOwner = body.getClassBuilder().getSuperOwner();
            }
            if (!Objects.equals(descriptor.getOwner(), superOwner)) {
                if (descriptor instanceof SmartMethodDescriptor) {
                    ((SmartMethodDescriptor) descriptor).setOwner(superOwner);
                }
            }
            return descriptor;
        }

        public int getOpCode() {
            return Opcodes.INVOKESPECIAL;
        }

        @Override
        public MethodInvokeAction setInstance(Action instance) {
            throw new UnsupportedOperationException("InvokeSupper() method not support set invoke object");
        }
    }
}
