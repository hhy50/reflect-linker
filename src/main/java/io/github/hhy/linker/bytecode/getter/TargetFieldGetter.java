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
        methodBody.append(writer -> {
            writer.visitVarInsn(Opcodes.ALOAD, 0);
            writer.visitFieldInsn(Opcodes.GETFIELD, ClassUtil.className2path(implClass), "target", "Ljava/lang/Object;");
            writer.visitVarInsn(Opcodes.ASTORE, targetIndex);
        });
        return new ObjectVar(targetIndex);
    }
}
