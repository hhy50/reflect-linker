package io.github.hhy.linker.generate.getter;

import io.github.hhy.linker.define.field.EarlyFieldRef;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.vars.FieldVar;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;

public class TargetFieldGetter extends Getter<EarlyFieldRef> {

    public TargetFieldGetter(String implClass, EarlyFieldRef targetFieldRef) {
        super(implClass, targetFieldRef);
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {

    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        FieldVar objectVar = new FieldVar(methodBody.lvbIndex++, field.getType(), field.fieldName);
        methodBody.append(mv -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, ClassUtil.className2path(implClass), field.getFullName(), ObjectVar.TYPE.getDescriptor());
            objectVar.store(methodBody);
        });
        return objectVar;
    }
}
