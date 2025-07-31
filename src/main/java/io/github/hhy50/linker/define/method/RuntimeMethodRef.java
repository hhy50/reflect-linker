package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.invoker.Invoker;
import io.github.hhy50.linker.generate.invoker.RuntimeMethodInvoker;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.util.Arrays;


/**
 * The type Runtime method ref.
 */
public class RuntimeMethodRef extends MethodRef {
    private Type methodType;
    private Type[] argsType;
    private boolean designateStatic;
    private boolean isStatic;
    private boolean isAutolink;

    /**
     * Instantiates a new Runtime method ref.
     *
     * @param owner      the owner
     * @param name       the name
     * @param argsType   the args type
     */
    public RuntimeMethodRef(FieldRef owner, String name, String[] argsType) {
        super(owner, name);

        Type[] newArgsType = new Type[argsType.length];
        Arrays.fill(newArgsType, ObjectVar.TYPE);
        this.methodType = Type.getMethodType(ObjectVar.TYPE, newArgsType);
        this.argsType = Arrays.stream(argsType)
                .map(TypeUtil::getType).toArray(Type[]::new);
    }

    @Override
    public Type getMethodType() {
        return methodType;
    }

    /**
     * Get args type type [ ].
     *
     * @return the type [ ]
     */
    public Type[] getArgsType() {
        return this.argsType;
    }

    public void setArgsType(Type[] argsType) {
        this.argsType = argsType;
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
     */
    public RuntimeMethodRef setAutolink(boolean autolink) {
        this.isAutolink = autolink;
        return this;
    }

    @Override
    public Invoker<?> defineInvoker() {
        return new RuntimeMethodInvoker((RuntimeMethodRef) this);
    }
}
