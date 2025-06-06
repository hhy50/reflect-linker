package io.github.hhy50.linker.generate.bytecode;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import org.objectweb.asm.FieldVisitor;
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
    protected final Type type;

    /**
     * The Field visitor.
     */
    protected FieldVisitor fieldVisitor;

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
     * Instantiates a new Member.
     *
     * @param access       the access
     * @param owner        the owner
     * @param memberName   the member name
     * @param type         the type
     * @param fieldVisitor the field visitor
     */
    public Member(int access, String owner, String memberName, Type type, FieldVisitor fieldVisitor) {
        this.access = access;
        this.owner = owner;
        this.memberName = memberName;
        this.type = type;
        this.fieldVisitor = fieldVisitor;
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
     * Gets field visitor.
     *
     * @return the field visitor
     */
    public FieldVisitor getFieldWriter() {
        return fieldVisitor;
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
    public void load(MethodBody body) {
        MethodVisitor mv = body.getWriter();
        String owner = this.owner == null ? body.getClassBuilder().getClassOwner() : this.owner;
        Type type = getType();
        if ((access & Opcodes.ACC_STATIC) > 0) {
            mv.visitFieldInsn(Opcodes.GETSTATIC, owner, this.memberName, type.getDescriptor());
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, 0); // this
            mv.visitFieldInsn(Opcodes.GETFIELD, owner, this.memberName, type.getDescriptor());
        }
    }

    /**
     * Store.
     *
     * @param body   the method body
     * @param action the action
     */
    public void store(MethodBody body, Action action) {
        MethodVisitor mv = body.getWriter();
        String owner = this.owner == null ? body.getClassBuilder().getClassOwner() : this.owner;
        if ((access & Opcodes.ACC_STATIC) > 0) {
            action.apply(body);
            mv.visitFieldInsn(Opcodes.PUTSTATIC, owner, this.memberName, this.type.getDescriptor());
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            action.apply(body);
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
        return (block) -> this.store(body, action);
    }
}
