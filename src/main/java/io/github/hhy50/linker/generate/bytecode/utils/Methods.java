package io.github.hhy50.linker.generate.bytecode.utils;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.exceptions.MethodNotFoundException;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.util.ReflectUtil;
import org.objectweb.asm.Opcodes;

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
     */
    public static MethodInvokeAction invoke(Class<?> clazz, String name, Class<?>... argsType) {
        Method method = ReflectUtil.matchMethod(clazz, name, null, Arrays.stream(argsType).map(Class::getName).toArray(String[]::new));
        if (method == null) {
            throw new MethodNotFoundException(clazz, name);
        }
        return new MethodInvokeAction(MethodDescriptor.of(method));
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
     * invokeSuper method
     */
    static class InvokeSupper extends MethodInvokeAction {

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
        public void apply(MethodBody body) {
            if (args == null) {
                args = body.getArgs();
            }
            if (this.superOwner == null) {
                this.superOwner = body.getClassBuilder().getSuperOwner();
            }
            if (this.methodDescriptor == null) {
                this.methodDescriptor = body.getMethodDescriptor();
            }
            if (!Objects.equals(methodDescriptor.getOwner(), superOwner)) {
                methodDescriptor.setOwner(superOwner);
            }
            super.apply(body);
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
