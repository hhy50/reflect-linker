package io.github.hhy.linker.define.provider;

import java.util.Objects;

/**
 * <p>Abstract DefaultTargetProviderImpl class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public abstract class DefaultTargetProviderImpl implements TargetProvider {

    protected Object target;

    /**
     * <p>Constructor for DefaultTargetProviderImpl.</p>
     *
     * @param target a {@link java.lang.Object} object.
     */
    public DefaultTargetProviderImpl(Object target) {
        this.target = target;
    }

    /**
     * <p>Getter for the field <code>target</code>.</p>
     *
     * @return a {@link java.lang.Object} object.
     */
    public Object getTarget() {
        return target;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof DefaultTargetProviderImpl) {
            return Objects.equals(target, ((DefaultTargetProviderImpl) obj).target);
        }
        return Objects.equals(target, obj);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return target.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return target.hashCode();
    }
}
