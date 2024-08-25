package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.entity.FieldHolder;
import io.github.hhy.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class FieldLoadAction implements LoadAction {

    private final FieldHolder fieldHolder;
    private Action instance;

    public FieldLoadAction(FieldHolder fieldHolder) {
        this.fieldHolder = fieldHolder;
    }

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
