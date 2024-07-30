package io.github.hhy.linker.define;

public abstract class DefaultTargetProviderImpl implements TargetProvider{

    private Object origin;

    public DefaultTargetProviderImpl(Object obj) {
        this.origin = obj;
    }

    public Object getTarget() {
        return origin;
    }
}
