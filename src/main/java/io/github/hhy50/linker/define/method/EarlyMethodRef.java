package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.invoker.EarlyMethodInvoker;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Objects;

import static io.github.hhy50.linker.util.ClassUtil.isPublic;

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
    public Type getLookupType() {
        return Type.getType(reflect);
    }

    @Override
    public MethodHandle defineInvoker() {
        return new EarlyMethodInvoker(this);
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

    @Override
    public Type getGenericType() {
        Type lookupType = Type.getType(reflect);
        if (isInvisible()) {
            return TypeUtil.genericType(lookupType);
        }
        return lookupType;
    }

    /**
     * Is invisible boolean.
     *
     * @return the boolean
     */
    public boolean isInvisible() {
        if (!isPublic(reflect.getReturnType())) {
            return true;
        }
        for (Class<?> parameterType : reflect.getParameterTypes()) {
            if (!isPublic(parameterType)) {
                return true;
            }
        }
        return false;
    }
}
