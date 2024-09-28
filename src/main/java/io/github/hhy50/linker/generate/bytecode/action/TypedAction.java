package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.asm.AsmUtil;
import org.objectweb.asm.Type;

/**
 * The interface Action.
 */
public interface TypedAction extends Action {

    /**
     * Get action result type
     * @return
     */
    Type getType();

    /**
     * Return this action result
     * @return
     */
    default Action thenReturn() {
        return body ->  {
            apply(body);
            AsmUtil.areturn(body.getWriter(), getType());
        };
    }
}
