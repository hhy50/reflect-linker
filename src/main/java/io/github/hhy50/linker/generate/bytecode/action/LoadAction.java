package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.of;
import static io.github.hhy50.linker.generate.bytecode.action.Actions.withVisitor;

/**
 * The interface Load action.
 */
public interface LoadAction extends Action {
    /**
     * The constant LOAD_0.
     */
    LOAD0Action LOAD_0 = new LOAD0Action();

    /**
     * The constant LOAD0.
     */
    Action LOAD0 = LOAD_0;

    /**
     * Aload action.
     *
     * @param i the
     * @return the action
     */
    static Action aload(int i) {
        return withVisitor(mv -> mv.visitVarInsn(Opcodes.ALOAD, i));
    }

    @Override
    default void apply(MethodBody body) {
        body.append(this.load());
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
     * @return the action
     */
    Action load();

    /**
     * The type Load 0 action.
     */
    class LOAD0Action implements LoadAction, LazyTypedAction {
        /**
         * The This type.
         */
        Type thisType;

        @Override
        public Action load() {
            return of(body -> this.thisType = Type.getObjectType(body.getClassBuilder().getClassOwner()),
                    withVisitor(mv -> mv.visitVarInsn(Opcodes.ALOAD, 0)));
        }

        @Override
        public Type getType() {
            return thisType;
        }
    }
}
