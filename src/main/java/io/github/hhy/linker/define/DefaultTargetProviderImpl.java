package io.github.hhy.linker.define;

public abstract class DefaultTargetProviderImpl implements TargetProvider{

    protected Object target;

    public DefaultTargetProviderImpl(Object target) {
        this.target = target;
    }

    public Object getTarget() {
        return target;
    }
}
