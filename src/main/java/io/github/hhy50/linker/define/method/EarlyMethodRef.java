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
    private final Method reflectMethod;

    /**
     * Instantiates a new Early method ref.
     *
     * @param reflectMethod the method
     */
    public EarlyMethodRef(Method reflectMethod) {
        super(reflectMethod.getName());
        this.reflectMethod = reflectMethod;
    }

    /**
     * Gets lookup class.
     *
     * @return the lookup class
     */
    public Type getLookupClass() {
        return Type.getType(this.reflectMethod.getDeclaringClass());
    }

    @Override
    public Type getLookupType() {
        return Type.getType(reflectMethod);
    }

    @Override
    public MethodHandle defineInvoker() {
        return new EarlyMethodInvoker(this);
    }

    @Override
    public void setSuperClass(String superClass) {
        if (Objects.equals("", superClass))
            this.superClass = reflectMethod.getDeclaringClass().getName();
        else
            this.superClass = superClass;
    }

    /**
     * Gets reflect.
     *
     * @return the reflect
     */
    public Method getReflect() {
        return this.reflectMethod;
    }

    @Override
    public Type getGenericType() {
        Type lookupType = getLookupType();
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
        if (!isPublic(reflectMethod.getReturnType())) {
            return true;
        }
        for (Class<?> parameterType : reflectMethod.getParameterTypes()) {
            if (!isPublic(parameterType)) {
                return true;
            }
        }
        return false;
    }
}
