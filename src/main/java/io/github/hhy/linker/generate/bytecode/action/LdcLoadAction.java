package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static io.github.hhy.linker.asm.AsmUtil.adaptLdcClassType;

public class LdcLoadAction implements LoadAction {

    private Object ldcConstVar;

    public LdcLoadAction(Object ldcConstVar) {
        this.ldcConstVar = ldcConstVar;
    }

    @Override
    public void load(MethodBody body) {
        MethodVisitor mv = body.getWriter();
        if (ldcConstVar instanceof Type) {
            adaptLdcClassType(mv, (Type) ldcConstVar);
        } else if (ldcConstVar instanceof Class) {
            adaptLdcClassType(mv, Type.getType((Class) ldcConstVar));
        } else {
            mv.visitLdcInsn(ldcConstVar);
        }
    }

    public static LdcLoadAction of(Object ldcConstVar) {
        return new LdcLoadAction(ldcConstVar);
    }
}
