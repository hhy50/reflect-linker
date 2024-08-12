package io.github.hhy.linker.bytecode.getter;

import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.RuntimeField;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;

public class TargetFieldGetter extends Getter {

    private final String implClass;

    public TargetFieldGetter(String implClass) {
        super(RuntimeField.TARGET);
        this.implClass = implClass;
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        int targetIndex = methodBody.lvbIndex++;
        methodBody.append(mv -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, ClassUtil.className2path(implClass), RuntimeField.TARGET.getFullName(), "Ljava/lang/Object;");
            mv.visitVarInsn(Opcodes.ASTORE, targetIndex);
        });
        return new ObjectVar(targetIndex);
    }
}
