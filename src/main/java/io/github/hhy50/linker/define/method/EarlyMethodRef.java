package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.invoker.EarlyMethodInvoker;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
    public EarlyMethodRef(String fullName, Method reflectMethod) {
        super(fullName, reflectMethod.getName());
        this.reflectMethod = reflectMethod;
    }

    /**
     * Is static boolean.
     *
     * @return the boolean
     */
    public boolean isStatic() {
        return Modifier.isStatic(reflectMethod.getModifiers());
    }

    /**
     * Gets lookup class.
     *
     * @return the lookup class
     */
    public Type getLookupClass() {
        return Type.getType(reflectMethod.getDeclaringClass());
    }

    public Type getMhType() {
        Type type = Type.getType(this.reflectMethod);
        if (isInvisible()) {
            return genericType(type);
        }
        return type;
    }

    @Override
    public MethodHandle defineInvoker() {
        return new EarlyMethodInvoker(this);
    }

    /**
     * Gets declare type.
     *
     * @return the declare type
     */
    public Type getDeclareType() {
        return Type.getType(reflectMethod);
    }

    @Override
    public void setSuperClass(String superClass) {
        if (Objects.equals("", superClass))
            this.superClass = reflectMethod.getDeclaringClass().getName();
        else
            this.superClass = superClass;
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

    /**
     * Generic type type.
     *
     * @param methodType the method type
     * @return the type
     */
    static Type genericType(Type methodType) {
        Type rType = methodType.getReturnType();
        Type[] argsType = methodType.getArgumentTypes();
        if (!rType.equals(Type.VOID_TYPE) && TypeUtil.isObjectType(rType)) {
            rType = ObjectVar.TYPE;
        }
        for (int i = 0; i < argsType.length; i++) {
            if (!argsType[i].equals(Type.VOID_TYPE) && TypeUtil.isObjectType(argsType[i])) {
                argsType[i] = ObjectVar.TYPE;
            }
        }
        return Type.getMethodType(rType, argsType);
    }
}
