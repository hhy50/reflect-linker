package io.github.hhy50.linker.generate.bytecode;

import io.github.hhy50.linker.asm.AsmField;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import io.github.hhy50.linker.generate.bytecode.action.TypedAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.of;
import static io.github.hhy50.linker.generate.bytecode.action.Actions.withVisitor;

import java.util.Objects;

/**
 * The type Member.
 */
public class Member extends VarInst {

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
     * @param field the field
     */
    public Member(AsmField field) {
        this(field.access, field.owner, field.name, field.type);
        this.fieldVisitor = field;
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
    public Action load() {
        return (body) -> {
            String owner = this.owner == null ? body.getClassBuilder().getClassOwner() : this.owner;
            Type type = getType();
            MethodVisitor mv = body.getWriter();
            if ((access & Opcodes.ACC_STATIC) > 0) {
                mv.visitFieldInsn(Opcodes.GETSTATIC, owner, this.memberName, type.getDescriptor());
            } else {
                mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                mv.visitFieldInsn(Opcodes.GETFIELD, owner, this.memberName, type.getDescriptor());
            }
        };
    }

    /**
     * Store.
     *
     * @param body   the method body
     * @param action the action
     */
    public Action store(Action action) {
        return of(withVisitor(mv -> {
            if ((access & Opcodes.ACC_STATIC) > 0) {
                mv.visitVarInsn(Opcodes.ALOAD, 0);
            }
        }),
                action,
                body -> {
                    this.owner = this.owner == null ? body.getClassBuilder().getClassOwner() : this.owner;
                },
                withVisitor(
                        mv -> mv.visitFieldInsn(Opcodes.PUTFIELD, owner, this.memberName, this.type.getDescriptor())));
    }
}
