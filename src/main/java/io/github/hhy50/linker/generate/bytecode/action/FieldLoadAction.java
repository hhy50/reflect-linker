package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.entity.FieldHolder;
import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * The type Field load action.
 */
public class FieldLoadAction implements LoadAction {

    private final FieldHolder fieldHolder;
    private Action instance;

    /**
     * Instantiates a new Field load action.
     *
     * @param fieldHolder the field holder
     */
    public FieldLoadAction(FieldHolder fieldHolder) {
        this.fieldHolder = fieldHolder;
    }

    /**
     * Sets instance.
     *
     * @param instance the instance
     * @return the instance
     */
    public FieldLoadAction setInstance(Action instance) {
        this.instance = instance;
        return this;
    }


    @Override
    public void load(MethodBody body) {
        MethodVisitor mv = body.getWriter();
        if (instance != null) {
            instance.apply(body);
        }
        mv.visitFieldInsn(instance != null ? Opcodes.GETFIELD : Opcodes.GETSTATIC,
                fieldHolder.getOwner(), fieldHolder.getFieldName(), fieldHolder.getDesc());
    }
}
