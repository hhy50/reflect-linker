package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.invoker.EarlyMethodInvoker;
import io.github.hhy50.linker.util.ClassUtil;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Objects;


/**
 * The type Early method ref.
 */
public class EarlyMethodRef extends MethodRef {
    /**
     * The Method.
     */
    private final Method reflect;

    /**
     * Instantiates a new Early method ref.
     *
     * @param reflect the method
     */
    public EarlyMethodRef(Method reflect) {
        super(reflect.getName());
        this.reflect = reflect;
    }

    @Override
    public MethodHandle defineInvoker() {
        return new EarlyMethodInvoker(this);
    }

    @Override
    public Type getMethodType() {
        Type lookupType = Type.getType(reflect);
        if (isInvisible()) {
            return TypeUtil.genericType(lookupType);
        }
        return lookupType;
    }

    @Override
    public void setSuperClass(String superClass) {
        if (Objects.equals("", superClass))
            this.superClass = reflect.getDeclaringClass().getName();
        else
            this.superClass = superClass;
    }

    /**
     * Gets reflect.
     *
     * @return the reflect
     */
    public Method getReflect() {
        return this.reflect;
    }

    /**
     * Is invisible boolean.
     *
     * @return the boolean
     */
    public boolean isInvisible() {
        if (!ClassUtil.isPublic(reflect.getReturnType())) {
            return true;
        }
        for (Class<?> parameterType : reflect.getParameterTypes()) {
            if (!ClassUtil.isPublic(parameterType)) {
                return true;
            }
        }
        return false;
    }
}
