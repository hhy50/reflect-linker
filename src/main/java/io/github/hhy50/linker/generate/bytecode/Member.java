package io.github.hhy50.linker.generate.bytecode;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * <p>Abstract Member class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public abstract class Member implements LoadAction {

    protected int access;

    /**
     * 所属类
     */
    protected String owner;

    /**
     * 成员名称
     */
    protected String memberName;

    /**
     * 类型
     */
    protected Type type;

    /**
     * <p>Constructor for Member.</p>
     *
     * @param access a int.
     * @param owner a {@link java.lang.String} object.
     * @param memberName a {@link java.lang.String} object.
     * @param type a {@link org.objectweb.asm.Type} object.
     */
    public Member(int access, String owner, String memberName, Type type) {
        this.access = access;
        this.owner = owner;
        this.memberName = memberName;
        this.type = type;
    }

    /**
     * <p>Getter for the field <code>memberName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMemberName() {
        return memberName;
    }

    /**
     * <p>Getter for the field <code>access</code>.</p>
     *
     * @return a int.
     */
    public int getAccess() {
        return access;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link org.objectweb.asm.Type} object.
     */
    public Type getType() {
        return type;
    }

    /** {@inheritDoc} */
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
     * <p>store.</p>
     *
     * @param methodBody a {@link MethodBody} object.
     * @param action a {@link Action} object.
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
}
