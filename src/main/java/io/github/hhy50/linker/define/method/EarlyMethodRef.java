package io.github.hhy50.linker.define.method;


import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.invoker.EarlyMethodInvoker;
import io.github.hhy50.linker.generate.invoker.Invoker;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static io.github.hhy50.linker.util.ClassUtil.isPublic;

/**
 * The type Early method ref.
 */
public class EarlyMethodRef extends MethodRef {
    public Method method;

    /**
     * Instantiates a new Early method ref.
     *
     * @param owner  the owner
     * @param method the method
     */
    public EarlyMethodRef(FieldRef owner, Method method) {
        super(owner, method.getName());
        this.method = method;
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
     * Gets lookup class.
     *
     * @return the lookup class
     */
    public Type getLookupClass() {
        return Type.getType(method.getDeclaringClass());
    }

    public Type getMethodType() {
        Type type = Type.getType(this.method);
        if (isInvisible()) {
            return genericType(type);
        }
        return type;
    }

    @Override
    public Invoker<?> defineInvoker() {
        return new EarlyMethodInvoker((EarlyMethodRef) this);
    }

    /**
     * Gets declare type.
     *
     * @return the declare type
     */
    public Type getDeclareType() {
        return Type.getType(method);
    }

    @Override
    public void setSuperClass(String superClass) {
        this.superClass = method.getDeclaringClass().getName();
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    /**
     * Is invisible boolean.
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

    /**
     * Generic type type.
     *
     * @param methodType the method type
     * @return the type
     */
    static Type genericType(Type methodType) {
        Type rType = methodType.getReturnType();
        Type[] argsType = methodType.getArgumentTypes();
        if (!rType.equals(Type.VOID_TYPE) && AsmUtil.isObjectType(rType)) {
            rType = ObjectVar.TYPE;
        }
        for (int i = 0; i < argsType.length; i++) {
            if (!argsType[i].equals(Type.VOID_TYPE) && AsmUtil.isObjectType(argsType[i])) {
                argsType[i] = ObjectVar.TYPE;
            }
        }
        return Type.getMethodType(rType, argsType);
    }
}
