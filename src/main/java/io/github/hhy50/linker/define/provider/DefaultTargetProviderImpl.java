package io.github.hhy50.linker.define.provider;

import io.github.hhy50.linker.exceptions.ResetTargetException;
import io.github.hhy50.linker.generate.builtin.RuntimeProvider;
import io.github.hhy50.linker.generate.builtin.SetTargetProvider;
import io.github.hhy50.linker.generate.builtin.TargetProvider;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * The type Default target provider.
 */
public class DefaultTargetProviderImpl implements TargetProvider, SetTargetProvider {

    /**
     * The Target.
     */
    protected Object target;

    /**
     * Instantiates a new Default target provider.
     *
     * @param target the target
     */
    public DefaultTargetProviderImpl(Object target) {
        this.target = target;
    }

    public Object getTarget() {
        return target;
    }

    @Override
    public void setValue(Object target) {
        requireNonNull(target);
        if (this.target.getClass().getName().equals(target.getClass().getName()) ||
                this instanceof RuntimeProvider) {
            this.target = target;
            return;
        }
        throw new ResetTargetException("Class type not match");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof TargetProvider) {
            return Objects.equals(target, ((TargetProvider) obj).getTarget());
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
