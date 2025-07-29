package io.github.hhy50.linker.generate.bytecode.action;

import org.objectweb.asm.Type;

import static java.util.Objects.requireNonNull;

/**
 * The interface Action.
 * 部分TypedAction在执行前可能拿不到具体的类型, 只有执行后才确定类型
 * 这部分被 {@link LazyTypedAction}  标记
 */
public interface TypedAction extends Action {

    /**
     * Get action result type
     *
     * @return type type
     */
    Type getType();

    /**
     * Return this action result
     *
     * @return action action
     */
    default Action thenReturn() {
        return body -> {
            Type type = getType();
            requireNonNull(type);

            apply(body);
            Actions.areturn(type).apply(body);
        };
    }
}
