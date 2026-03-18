package io.github.hhy50.linker.generate.bytecode.action;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.of;
import static io.github.hhy50.linker.generate.bytecode.action.Actions.withVisitor;

/**
 * The type New object action.
 */
public class NewObjectAction implements LoadAction, TypedAction {

    private final Type objType;
    private final Type[] argsType;
    private Action[] args;

    /**
     * Instantiates a new New object action.
     *
     * @param objType  the obj type
     * @param argsType the args type
     */
    public NewObjectAction(Type objType, Type... argsType) {
        this.objType = objType;
        this.argsType = argsType;
    }

    /**
     * Sets args.
     *
     * @param args the args
     * @return the args
     */
    public NewObjectAction setArgs(Action... args) {
        this.args = args;
        return this;
    }

    @Override
    public Action load() {
        return of(
                withVisitor(mv -> {
                    mv.visitTypeInsn(Opcodes.NEW, objType.getInternalName());
                    mv.visitInsn(Opcodes.DUP);
                }),
                of(args),
                withVisitor(mv ->
                        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, objType.getInternalName(), "<init>",
                                Type.getMethodDescriptor(Type.VOID_TYPE, argsType), false))
        );
    }

    @Override
    public Type getType() {
        return objType;
    }
}
