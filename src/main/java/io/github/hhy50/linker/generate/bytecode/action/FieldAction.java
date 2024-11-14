package io.github.hhy50.linker.generate.bytecode.action;


import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


/**
 * The type Field action.
 */
public class FieldAction implements LoadAction {

    private final LoadAction owner;
    private final String fieldName;
    private final Type fieldType;
    private boolean isStatic;

    /**
     * Instantiates a new Field action.
     *
     * @param owner     the owner
     * @param fieldName the name
     */
    public FieldAction(LoadAction owner, String fieldName, Type fieldType) {
        this.owner = owner;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.isStatic = false;
    }

    public FieldAction setStatic() {
        this.isStatic = true;
        return this;
    }

    @Override
    public void load(MethodBody body) {
        MethodVisitor mv = body.getWriter();
        body.append(this.owner);
        mv.visitFieldInsn(isStatic ? Opcodes.GETSTATIC : Opcodes.GETFIELD, owner.getType().getInternalName(), this.fieldName, fieldType.getDescriptor());
    }

    public Action store(Action action) {
        return (body) -> {
            MethodVisitor mv = body.getWriter();
            body.append(this.owner);
            action.apply(body);
            mv.visitFieldInsn(isStatic ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD, owner.getType().getInternalName(), this.fieldName, fieldType.getDescriptor());
        };
    }

    @Override
    public Type getType() {
        return fieldType;
    }
}
