package io.github.hhy50.linker.define;

import java.util.List;


/**
 * The type Interface impl class.
 */
public class InterfaceImplClass {
    private final String className;
    private final List<MethodDefine> methodDefines;
    private byte[] bytecode;

    /**
     * Instantiates a new Interface impl class.
     *
     * @param className     the class name
     * @param methodDefines the method defines
     */
    public InterfaceImplClass(String className, List<MethodDefine> methodDefines) {
        this.className = className;
        this.methodDefines = methodDefines;
    }

    /**
     * Gets method defines.
     *
     * @return the method defines
     */
    public List<MethodDefine> getMethodDefines() {
        return methodDefines;
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
