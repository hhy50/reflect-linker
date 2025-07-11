package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

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
    public void load(MethodBody body) {
        MethodVisitor mv = body.getWriter();

        mv.visitTypeInsn(Opcodes.NEW, objType.getInternalName());
        mv.visitInsn(Opcodes.DUP);

        if (args != null) {
            for (Action arg : args) {
                body.append(arg);
            }
        }
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, objType.getInternalName(), "<init>",
                Type.getMethodDescriptor(Type.VOID_TYPE, argsType), false);
    }

    @Override
    public Type getType() {
        return objType;
    }
}
