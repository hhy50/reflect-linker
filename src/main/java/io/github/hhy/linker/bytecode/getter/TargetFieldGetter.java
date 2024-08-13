package io.github.hhy.linker.bytecode.getter;

import io.github.hhy.linker.bytecode.InvokeClassImplBuilder;
import io.github.hhy.linker.bytecode.MethodBody;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.define.FieldRef;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;

public class TargetFieldGetter extends Getter {

    private final String implClass;

    public TargetFieldGetter(String implClass) {
        super(FieldRef.TARGET);
        this.implClass = implClass;
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        this.lookupMember = classImplBuilder.defineLookup(FieldRef.TARGET.getLookupName());
    }

    @Override
    public ObjectVar invoke(MethodBody methodBody) {
        int targetIndex = methodBody.lvbIndex++;
        methodBody.append(mv -> {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, ClassUtil.className2path(implClass), FieldRef.TARGET.getFullName(), "Ljava/lang/Object;");
            mv.visitVarInsn(Opcodes.ASTORE, targetIndex);
        });
        return new ObjectVar(targetIndex);
    }
}
