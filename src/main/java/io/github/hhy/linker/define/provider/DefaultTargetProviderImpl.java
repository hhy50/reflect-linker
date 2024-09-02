package io.github.hhy.linker.define.provider;

import java.util.Objects;

public abstract class DefaultTargetProviderImpl implements TargetProvider {

    protected Object target;

    public DefaultTargetProviderImpl(Object target) {
        this.target = target;
    }

    public Object getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof DefaultTargetProviderImpl) {
            return Objects.equals(target, ((DefaultTargetProviderImpl) obj).target);
        }
        return Objects.equals(target, obj);
    }

    @Override
    public String toString() {
        return target.toString();
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }
}
