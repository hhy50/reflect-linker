package io.github.hhy50.linker.generate.bytecode.action;

/**
 * 有些类型执行apply（也就是有了body之后）后才能确定
 * 这个接口负责标记这些Action
 */
public interface LazyTypedAction extends TypedAction {
    default Action thenReturn() {
        return body ->  {
            apply(body);
            Actions.areturn(getType()).apply(body);
        };
    }
}
