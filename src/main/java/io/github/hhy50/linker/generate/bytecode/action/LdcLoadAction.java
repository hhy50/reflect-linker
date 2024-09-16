package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static io.github.hhy50.linker.asm.AsmUtil.adaptLdcClassType;

/**
 * <p>LdcLoadAction class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class LdcLoadAction implements LoadAction {

    private Object ldcConstVar;

    /**
     * <p>Constructor for LdcLoadAction.</p>
     *
     * @param ldcConstVar a {@link java.lang.Object} object.
     */
    public LdcLoadAction(Object ldcConstVar) {
        this.ldcConstVar = ldcConstVar;
    }

    /** {@inheritDoc} */
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
     * <p>of.</p>
     *
     * @param ldcConstVar a {@link java.lang.Object} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.LdcLoadAction} object.
     */
    public static LdcLoadAction of(Object ldcConstVar) {
        return new LdcLoadAction(ldcConstVar);
    }
}
