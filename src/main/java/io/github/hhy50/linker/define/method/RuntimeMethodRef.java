package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.invoker.RuntimeMethodInvoker;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.util.Arrays;


/**
 * The type Runtime method ref.
 */
public class RuntimeMethodRef extends MethodRef {
    private Type[] argsType;
    private Boolean designateStatic;
    private boolean isAutolink;

    /**
     * Instantiates a new Runtime method ref.
     *
     * @param name     the name
     * @param argsType the args type
     */
    public RuntimeMethodRef(String name, String[] argsType) {
        super(name);
        this.argsType = Arrays.stream(argsType)
                .map(TypeUtil::getType).toArray(Type[]::new);
    }

    @Override
    public MethodHandle defineInvoker() {
        return new RuntimeMethodInvoker(this);
    }

    @Override
    public Type getLookupType() {
        return Type.getMethodType(ObjectVar.TYPE, this.argsType);
    }

    @Override
    public Type getGenericType() {
        Type[] newArgs = new Type[this.argsType.length];
        Arrays.fill(newArgs, ObjectVar.TYPE);
        return Type.getMethodType(ObjectVar.TYPE, newArgs);
    }

    /**
     * Get args type type [ ].
     *
     * @return the type [ ]
     */
    public Type[] getArgsType() {
        return this.argsType;
    }

    /**
     * Is designate static boolean.
     *
     * @return the boolean
     */
    public Boolean isDesignateStatic() {
        return designateStatic;
    }

    /**
     * Designate static.
     *
     * @param isStatic the is static
     */
    public void setStatic(Boolean isStatic) {
        this.designateStatic = isStatic;
    }

    /**
     * Is autolink boolean.
     *
     * @return the boolean
     */
    public boolean isAutolink() {
        return isAutolink;
    }

    /**
     * Sets autolink.
     *
     * @param autolink the autolink
     * @return the autolink
     */
    public RuntimeMethodRef setAutolink(boolean autolink) {
        this.isAutolink = autolink;
        return this;
    }
}
