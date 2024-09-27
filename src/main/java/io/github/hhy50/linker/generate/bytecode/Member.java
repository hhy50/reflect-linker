package io.github.hhy50.linker.generate.bytecode;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Objects;

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
    public boolean equals(Object obj) {
        if (obj instanceof Member) {
            return Objects.equals(owner, ((Member) obj).owner)
                    && Objects.equals(memberName, ((Member) obj).memberName);
        }
        return false;
    }

    @Override
    public void load(MethodBody methodBody) {
        MethodVisitor mv = methodBody.getWriter();
        String owner = this.owner == null ? methodBody.getClassBuilder().getClassDesc() : this.owner;
        if ((access & Opcodes.ACC_STATIC) > 0) {
            mv.visitFieldInsn(Opcodes.GETSTATIC, owner, this.memberName, this.type.getDescriptor());
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
            mv.visitFieldInsn(Opcodes.GETFIELD, owner, this.memberName, this.type.getDescriptor());
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
        String owner = this.owner == null ? methodBody.getClassBuilder().getClassDesc() : this.owner;
        if ((access & Opcodes.ACC_STATIC) > 0) {
            action.apply(methodBody);
            mv.visitFieldInsn(Opcodes.PUTSTATIC, owner, this.memberName, this.type.getDescriptor());
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            action.apply(methodBody);
            mv.visitFieldInsn(Opcodes.PUTFIELD, owner, this.memberName, this.type.getDescriptor());
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

    /**
     * Of member.
     *
     * @param memberName the member name
     * @param type       the type
     * @return the member
     */
    public static Member of(String memberName, Type type) {
        return new Member(Opcodes.ACC_PUBLIC, null, memberName, type);
    }

    /**
     * Of static member.
     *
     * @param memberName the member name
     * @param type       the type
     * @return the member
     */
    public static Member ofStatic(String memberName, Type type) {
        return new Member(Opcodes.ACC_PUBLIC|Opcodes.ACC_STATIC, null, memberName, type);
    }
}
