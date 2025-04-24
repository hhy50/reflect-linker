package io.github.hhy50.linker.define.method;


import io.github.hhy50.linker.define.field.FieldRef;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static io.github.hhy50.linker.util.ClassUtil.isPublic;

/**
 * The type Early method ref.
 */
public class EarlyMethodRef extends MethodRef {
    private Method method;
    private final Type methodType;

    /**
     * Instantiates a new Early method ref.
     *
     * @param owner  the owner
     * @param method the method
     */
    public EarlyMethodRef(FieldRef owner, Method method) {
        super(owner, method.getName());
        this.method = method;
        this.methodType = Type.getType(method);
    }

    /**
     * Is static boolean.
     *
     * @return the boolean
     */
    public boolean isStatic() {
        return Modifier.isStatic(method.getModifiers());
    }

    /**
     * Gets method type.
     *
     * @return the method type
     */
    public Type getMethodType() {
        return this.methodType;
    }

    public Type[] getArgsType() {
        return this.methodType.getArgumentTypes();
    }

    @Override
    public void setSuperClass(String superClass) {
        this.superClass = method.getDeclaringClass().getName();
    }

    /**
     * Gets declare type.
     *
     * @return the declare type
     */
    public Type getDeclareType() {
        return Type.getType(method.getDeclaringClass());
    }


    /**
     * Is unreachable boolean.
     *
     * @return the boolean
     */
    public boolean isInvisible() {
        if (!isPublic(method.getReturnType())) {
            return true;
        }
        for (Class<?> parameterType : method.getParameterTypes()) {
            if (!isPublic(parameterType)) {
                return true;
            }
        }
        return false;
    }
}
