package io.github.hhy50.linker.define.method;


import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.field.FieldRef;
import org.objectweb.asm.Type;

import java.util.Arrays;


/**
 * The type Runtime method ref.
 */
public class RuntimeMethodRef extends MethodRef {
    private Type[] argsType;
    private Class<?> returnType;
    private boolean designateStatic;
    private boolean isStatic;

    /**
     * Instantiates a new Runtime method ref.
     *
     * @param owner      the owner
     * @param name       the name
     * @param argsType   the args type
     * @param returnType the return type
     */
    public RuntimeMethodRef(FieldRef owner, String name, String[] argsType, Class<?> returnType) {
        super(owner, name);
        this.argsType = Arrays.stream(argsType)
                .map(AsmUtil::getType).toArray(Type[]::new);
        this.returnType = returnType;
    }

    public Type[] getArgsType() {
        return argsType;
    }

    /**
     * Is designate static boolean.
     *
     * @return the boolean
     */
    public boolean isDesignateStatic() {
        return designateStatic;
    }

    /**
     * Is static boolean.
     *
     * @return the boolean
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Designate static.
     *
     * @param isStatic the is static
     */
    public void designateStatic(boolean isStatic) {
        this.designateStatic = true;
        this.isStatic = isStatic;
    }
}
