package io.github.hhy50.linker.define;

import java.util.List;


/**
 * The type Interface impl class.
 */
public class InterfaceImplClass {

    /**
     * 实现类的类名
     */
    private final String className;

    /**
     * 需要实现的抽象方法
     */
    private final List<MethodExpr> absMethods;

    /**
     * 字节码
     */
    private byte[] bytecode;

    /**
     * Instantiates a new Interface impl class.
     *
     * @param className  the class name
     * @param absMethods the method defines
     */
    public InterfaceImplClass(String className, List<MethodExpr> absMethods) {
        this.className = className;
        this.absMethods = absMethods;
    }

    public List<MethodExpr> getAbsMethods() {
        return this.absMethods;
    }

    /**
     * Sets bytecode.
     *
     * @param bytecode the bytecode
     * @return the bytecode
     */
    public InterfaceImplClass setBytecode(byte[] bytecode) {
        this.bytecode = bytecode;
        return this;
    }

    /**
     * Get bytecode byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getBytecode() {
        return bytecode;
    }

    /**
     * Gets class name.
     *
     * @return the class name
     */
    public String getClassName() {
        return className;
    }
}
