package io.github.hhy50.linker.generate.bytecode;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * The type Member.
 */
public class Member implements LoadAction {

    /**
     * The Access.
     */
    protected int access;

    /**
     * The Owner.
     */
    protected String owner;

    /**
     * The Member name.
     */
    protected String memberName;

    /**
     * The Type.
     */
    protected Type type;

    /**
     * Instantiates a new Member.
     *
     * @param access     the access
     * @param owner      the owner
     * @param memberName the member name
     * @param type       the type
     */
    public Member(int access, String owner, String memberName, Type type) {
        this.access = access;
        this.owner = owner;
        this.memberName = memberName;
        this.type = type;
    }

    /**
     * Gets member name.
     *
     * @return the member name
     */
    public String getMemberName() {
        return memberName;
    }

    /**
     * Gets access.
     *
     * @return the access
     */
    public int getAccess() {
        return access;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }

    @Override
    public void load(MethodBody methodBody) {
        MethodVisitor mv = methodBody.getWriter();
        if ((access & Opcodes.ACC_STATIC) > 0) {
            mv.visitFieldInsn(Opcodes.GETSTATIC, this.owner, this.memberName, this.type.getDescriptor());
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
            mv.visitFieldInsn(Opcodes.GETFIELD, this.owner, this.memberName, this.type.getDescriptor());
        }
    }

    /**
     * Store.
     *
     * @param methodBody the method body
     * @param action     the action
     */
    public void store(MethodBody methodBody, Action action) {
        MethodVisitor mv = methodBody.getWriter();
        if ((access & Opcodes.ACC_STATIC) > 0) {
            action.apply(methodBody);
            mv.visitFieldInsn(Opcodes.PUTSTATIC, this.owner, this.memberName, this.type.getDescriptor());
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            action.apply(methodBody);
            mv.visitFieldInsn(Opcodes.PUTFIELD, this.owner, this.memberName, this.type.getDescriptor());
        }
    }

    /**
     * Store action.
     *
     * @param action the action
     * @return the action
     */
    public Action store(Action action) {
        return (body) -> this.store(body, action);
    }
}
