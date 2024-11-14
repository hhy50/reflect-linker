package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.ClassVar;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static io.github.hhy50.linker.asm.AsmUtil.adaptLdcClassType;

/**
 * The type Ldc load action.
 */
public class LdcLoadAction implements LoadAction {

    private Object ldcConstVar;

    /**
     * Instantiates a new Ldc load action.
     *
     * @param ldcConstVar the ldc const var
     */
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

    /**
     * Of ldc load action.
     *
     * @param ldcConstVar the ldc const var
     * @return the ldc load action
     */
    public static LdcLoadAction of(Object ldcConstVar) {
        return new LdcLoadAction(ldcConstVar);
    }

    @Override
    public Type getType() {
        if (ldcConstVar instanceof String) {
            return Type.getType(String.class);
        } else if (ldcConstVar instanceof Boolean) {
            return Type.BOOLEAN_TYPE;
        } else if (ldcConstVar instanceof Character) {
            return Type.CHAR_TYPE;
        } else if (ldcConstVar instanceof Byte) {
            return Type.BYTE_TYPE;
        } else if (ldcConstVar instanceof Short) {
            return Type.SHORT_TYPE;
        } else if (ldcConstVar instanceof Integer) {
            return Type.INT_TYPE;
        } else if (ldcConstVar instanceof Float) {
            return Type.FLOAT_TYPE;
        } else if (ldcConstVar instanceof Long) {
            return Type.LONG_TYPE;
        } else if (ldcConstVar instanceof Type) {
            return ClassVar.TYPE;
        }
        return ObjectVar.TYPE;
    }
}
