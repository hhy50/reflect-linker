package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.runtime.Runtime;

/**
 * <p>RuntimeAction class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class RuntimeAction {

    /**
     * <p>lookup.</p>
     *
     * @param action a {@link io.github.hhy50.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction} object.
     */
    public static MethodInvokeAction lookup(Action action) {
        return new MethodInvokeAction(Runtime.LOOKUP)
                .setArgs(action);
    }
}
