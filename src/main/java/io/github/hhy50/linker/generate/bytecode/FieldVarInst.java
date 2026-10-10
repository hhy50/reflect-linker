package io.github.hhy50.linker.generate.bytecode;

import io.github.hhy50.linker.asm.AsmField;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Objects;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.of;
import static io.github.hhy50.linker.generate.bytecode.action.Actions.withVisitor;

/**
 * The type FieldVarInst.
 */
public class FieldVarInst extends VarInst {

    /**
     * The Access.
     */
    protected int access;

    /**
     * The Owner.
     */
    protected String owner;

    /**
     * The FieldVarInst name.
     */
    protected String name;

    /**
     * The Type.
     */
    protected final Type type;

    /**
     * The FieldVarInst visitor.
     */
    protected FieldVisitor fieldVisitor;

    /**
     * Instantiates a new FieldVarInst.
     *
     * @param access    the access
     * @param owner     the owner
     * @param name the field name
     * @param type      the type
     */
    public FieldVarInst(int access, String owner, String name, Type type) {
        this.access = access;
        this.owner = owner;
        this.name = name;
        this.type = type;
    }

    /**
     * Instantiates a new FieldVarInst.
     *
     * @param field the field
     */
    public FieldVarInst(AsmField field) {
        this(field.access, field.owner, field.name, field.type);
        this.fieldVisitor = field;
    }

    /**
     * Gets field name.
     *
     * @return the field name
     */
    public String getName() {
        return name;
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
        if (obj instanceof FieldVarInst) {
            return Objects.equals(owner, ((FieldVarInst) obj).owner)
                    && Objects.equals(name, ((FieldVarInst) obj).name);
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
                mv.visitFieldInsn(Opcodes.GETSTATIC, owner, this.name, type.getDescriptor());
            } else {
                mv.visitVarInsn(Opcodes.ALOAD, 0); // this
                mv.visitFieldInsn(Opcodes.GETFIELD, owner, this.name, type.getDescriptor());
            }
        };
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
            mv.visitFieldInsn(Opcodes.PUTSTATIC, owner, this.name, this.type.getDescriptor());
        } else {
            mv.visitFieldInsn(Opcodes.PUTFIELD, owner, this.name, this.type.getDescriptor());
        }
    }

    /**
     * Store action.
     *
     * @param action the action
     * @return the action
     */
    public Action store(Action action) {
        boolean isStatic = (access & Opcodes.ACC_STATIC) > 0;
        return of(withVisitor(mv -> {
                    if (!isStatic) {
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                    }
                }),
                action,
                body -> {
                    this.owner = this.owner == null ? body.getClassBuilder().getClassOwner() : this.owner;
                },
                withVisitor(
                        mv -> mv.visitFieldInsn(isStatic ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD, owner, this.name, this.type.getDescriptor())));
    }
}
