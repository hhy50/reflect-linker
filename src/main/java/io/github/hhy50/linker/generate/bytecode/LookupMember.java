package io.github.hhy50.linker.generate.bytecode;


import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.vars.LookupVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;


/**
 * <p>LookupMember class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class LookupMember extends Member {

    /**
     * <p>Constructor for LookupMember.</p>
     *
     * @param access     a int.
     * @param owner      a {@link java.lang.String} object.
     * @param lookupName a {@link java.lang.String} object.
     */
    public LookupMember(int access, String owner, String lookupName) {
        super(access, owner, lookupName, LookupVar.TYPE);
    }

    /**
     * 初始化静态lookup, 生成以下代码
     * <pre>
     * static {
     *     lookup = Runtime.lookup(Type.class);
     * }
     * </pre>
     *
     * @param clinit      a {@link MethodBody} object.
     * @param classLoadAc a {@link Action} object.
     */
    public void staticInit(MethodBody clinit, Action classLoadAc) {
        this.store(clinit, RuntimeAction.lookup(classLoadAc));
    }

    /**
     * <p>staticInit.</p>
     *
     * @param clinit     a {@link MethodBody} object.
     * @param staticType a {@link org.objectweb.asm.Type} object.
     */
    public void staticInit(MethodBody clinit, Type staticType) {
        this.store(clinit, RuntimeAction.lookup(LdcLoadAction.of(staticType)));
    }


    /**
     * <p>reinit.</p>
     *
     * @param methodBody     a {@link MethodBody} object.
     * @param typeLoadAction a {@link Action} object.
     */
    public void reinit(MethodBody methodBody, Action typeLoadAction) {
        this.store(methodBody, RuntimeAction.lookup(typeLoadAction));
    }

    /**
     * 生成运行时校验lookup的代码
     *
     * @param methodBody   a {@link MethodBody} object.
     * @param varInst      a {@link VarInst} object.
     * @param lookupAssign a {@link Action} object.
     */
    public void runtimeCheck(MethodBody methodBody, VarInst varInst, Action lookupAssign) {
        /*
         * if (lookup == null || (obj != null && obj.getClass() != lookup.lookupClass())) {
         *     // goto @Label lookupAssign
         * }
         * // goto checkMh
         */
        methodBody.append(() -> new ConditionJumpAction(
                Condition.must(Condition.isNull(this), Condition.notNull(varInst)),
                lookupAssign, null)
        );
    }

    /**
     * <p>lookupClass.</p>
     *
     * @return a {@link MethodInvokeAction} object.
     */
    public MethodInvokeAction lookupClass() {
        return new MethodInvokeAction(MethodHolder.LOOKUP_LOOKUP_CLASS)
                .setInstance(this);
    }
}
