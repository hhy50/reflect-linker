package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.runtime.Runtime;

/**
 * The type Runtime action.
 */
public class RuntimeAction {

    /**
     * Lookup method invoke action.
     *
     * @param action the action
     * @return the method invoke action
     */
    public static MethodInvokeAction lookup(Action action) {
        return new MethodInvokeAction(Runtime.LOOKUP).setArgs(action);
    }
}
