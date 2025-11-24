package io.github.hhy50.linker.define;



/**
 * The type Interface impl class.
 */
public class GeneratedClass {

    /**
     * 实现类的类名
     */
    private final String className;

    /**
     * 字节码
     */
    private final byte[] bytecode;

    public GeneratedClass(String className, byte[] bytecode) {
        this.className = className;
        this.bytecode = bytecode;
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
