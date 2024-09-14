package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.bytecode.LookupMember;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.runtime.Runtime;

/**
 * <p>RuntimeAction class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class RuntimeAction {
    /**
     * <p>findLookup.</p>
     *
     * @param lookup a {@link LookupMember} object.
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link MethodInvokeAction} object.
     */
    public static MethodInvokeAction findLookup(LookupMember lookup, String fieldName) {
        return new MethodInvokeAction(Runtime.FIND_LOOKUP)
                .setArgs(new MethodInvokeAction(MethodHolder.LOOKUP_LOOKUP_CLASS)
                        .setInstance(lookup), LdcLoadAction.of(fieldName));
    }

    /**
     * <p>lookup.</p>
     *
     * @param action a {@link Action} object.
     * @return a {@link MethodInvokeAction} object.
     */
    public static MethodInvokeAction lookup(Action action) {
        return new MethodInvokeAction(Runtime.LOOKUP)
                .setArgs(action);
    }

    /**
     * <p>findSetter.</p>
     *
     * @param lookupMember a {@link LookupMember} object.
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link MethodInvokeAction} object.
     */
    public static MethodInvokeAction findSetter(VarInst lookupVar, String fieldName) {
        return new MethodInvokeAction(Runtime.FIND_SETTER)
                .setArgs(lookupVar, LdcLoadAction.of(fieldName));
    }
}
