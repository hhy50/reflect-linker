package io.github.hhy50.linker.define.method;


import io.github.hhy50.linker.define.field.FieldRef;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * <p>EarlyMethodRef class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class EarlyMethodRef extends MethodRef {
    private Method method;
    private final Type methodType;

    /**
     * <p>Constructor for EarlyMethodRef.</p>
     *
     * @param owner a {@link io.github.hhy50.linker.define.field.FieldRef} object.
     * @param method a {@link java.lang.reflect.Method} object.
     */
    public EarlyMethodRef(FieldRef owner, Method method) {
        super(owner, method.getName());
        this.method = method;
        this.methodType = Type.getType(method);
    }

    /**
     * <p>isStatic.</p>
     *
     * @return a boolean.
     */
    public boolean isStatic() {
        return Modifier.isStatic(method.getModifiers());
    }

    /**
     * <p>Getter for the field <code>methodType</code>.</p>
     *
     * @return a {@link org.objectweb.asm.Type} object.
     */
    public Type getMethodType() {
        return this.methodType;
    }

    /**
     * <p>getArgsType.</p>
     *
     * @return an array of {@link org.objectweb.asm.Type} objects.
     */
    public Type[] getArgsType() {
        return this.methodType.getArgumentTypes();
    }

    /** {@inheritDoc} */
    @Override
    public void setSuperClass(String superClass) {
        this.superClass = method.getDeclaringClass().getName();
    }

    public Type getDeclareType() {
        return Type.getType(method.getDeclaringClass());
    }
}
