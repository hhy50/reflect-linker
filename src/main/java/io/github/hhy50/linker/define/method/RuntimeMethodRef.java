package io.github.hhy50.linker.define.method;


import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.FieldRef;
import org.objectweb.asm.Type;

import java.util.Arrays;


/**
 * <p>RuntimeMethodRef class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class RuntimeMethodRef extends MethodRef {
    private Type[] argsType;
    private Class<?> returnType;

    /**
     * <p>Constructor for RuntimeMethodRef.</p>
     *
     * @param owner a {@link io.github.hhy50.linker.define.field.FieldRef} object.
     * @param name a {@link java.lang.String} object.
     * @param argsType an array of {@link java.lang.String} objects.
     * @param returnType a {@link java.lang.Class} object.
     */
    public RuntimeMethodRef(FieldRef owner, String name, String[] argsType, Class<?> returnType) {
        super(owner, name);
        this.argsType = Arrays.stream(argsType)
                .map(AsmUtil::getType).toArray(Type[]::new);
        this.returnType = returnType;
    }

    /**
     * <p>Getter for the field <code>argsType</code>.</p>
     *
     * @return an array of {@link org.objectweb.asm.Type} objects.
     */
    public Type[] getArgsType() {
        return argsType;
    }

    /**
     * <p>getRetunrType.</p>
     *
     * @return a {@link org.objectweb.asm.Type} object.
     */
    public Type getRetunrType() {
        return Type.getType(returnType);
    }

    /**
     * <p>getMethodType.</p>
     *
     * @return a {@link org.objectweb.asm.Type} object.
     */
    public Type getMethodType() {
        return Type.getMethodType(getRetunrType(), getArgsType());
    }
}
