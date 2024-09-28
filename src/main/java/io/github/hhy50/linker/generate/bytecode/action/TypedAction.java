package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.asm.AsmUtil;
import org.objectweb.asm.Type;

/**
 * The interface Action.
 */
public interface TypedAction extends Action {

    /**
     * Get action result type
     *
     * @return type
     */
    Type getType();

    /**
     * Return this action result
     *
     * @return action
     */
    default Action thenReturn() {
        return body ->  {
            apply(body);
            AsmUtil.areturn(body.getWriter(), getType());
        };
    }
}
