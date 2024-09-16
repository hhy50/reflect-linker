package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.entity.FieldHolder;
import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * <p>FieldLoadAction class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class FieldLoadAction implements LoadAction {

    private final FieldHolder fieldHolder;
    private Action instance;

    /**
     * <p>Constructor for FieldLoadAction.</p>
     *
     * @param fieldHolder a {@link io.github.hhy50.linker.entity.FieldHolder} object.
     */
    public FieldLoadAction(FieldHolder fieldHolder) {
        this.fieldHolder = fieldHolder;
    }

    /**
     * <p>Setter for the field <code>instance</code>.</p>
     *
     * @param instance a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.FieldLoadAction} object.
     */
    public FieldLoadAction setInstance(Action instance) {
        this.instance = instance;
        return this;
    }


    /** {@inheritDoc} */
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
