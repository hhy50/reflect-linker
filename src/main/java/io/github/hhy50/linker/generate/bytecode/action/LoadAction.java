package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * The interface Load action.
 */
public interface LoadAction extends TypedAction {

    /**
     * The constant LOAD0.
     */
    Action LOAD0 = body -> body.getWriter().visitVarInsn(Opcodes.ALOAD, 0);

    @Override
    default void apply(MethodBody body) {
        load(body);
    }

    /**
     * If null action.
     *
     * @param ifBlock the if block
     * @return the action
     */
    default Action ifNull(Action ifBlock) {
        return ifNull(ifBlock, null);
    }

    /**
     * If null action.
     *
     * @param ifBlock   the if block
     * @param elseBlock the else block
     * @return the action
     */
    default Action ifNull(Action ifBlock, Action elseBlock) {
        return new ConditionJumpAction(Condition.isNull(this), ifBlock, elseBlock);
    }

    /**
     * Of field.
     *
     * @param field the field
     * @param type  the type
     * @return field action
     */
    default FieldAction ofField(String field, Class<?> type) {
        return new FieldAction(this, field, Type.getType(type));
    }

    /**
     * invoke method
     *
     * @param methodName the method name
     * @param methodType the method type
     * @param args       the args
     * @return method invoke action
     */
    default MethodInvokeAction invokeMethod(String methodName, Type methodType, Action... args) {
        return Methods.invoke(methodName, methodType)
                .setInstance(this)
                .setArgs(args);
    }

    /**
     * Invoke method method invoke action.
     *
     * @param descriptor the descriptor
     * @param args       the args
     * @return the method invoke action
     */
    default MethodInvokeAction invokeMethod(MethodDescriptor descriptor, Action... args) {
        return Methods.invoke(descriptor)
                .setInstance(this)
                .setArgs(args);
    }

    /**
     * Load.
     *
     * @param body the body
     */
    void load(MethodBody body);
}
