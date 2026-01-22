package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.MethodHandle;
import org.objectweb.asm.Type;


/**
 * The type Method ref.
 */
public abstract class MethodRef {
    /**
     * The Name.
     */
    protected String name;

    /**
     * The Super class.
     */
    protected String superClass;

    /**
     * Instantiates a new Method ref.
     *
     * @param name the name
     */
    public MethodRef(String name) {
        this.name = name;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets super class.
     *
     * @return the super class
     */
    public String getSuperClass() {
        return superClass;
    }

    /**
     * Sets super class.
     *
     * @param superClass the super class
     */
    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    /**
     *
     * @return
     */
    public abstract MethodHandle defineInvoker();

    /**
     * Gets full name.
     *
     * @return
     */
    public abstract String getFullName();

    /**
     * 这个方法返回来的类型用来定位具体的methodhandle, 所以类型是具体的类型
     * @return
     */
    public abstract Type getLookupMhType();

    public boolean isRuntime() {
        return true;
    }
}
