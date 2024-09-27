package io.github.hhy50.linker.define;

import java.util.List;


/**
 * The type Interface class define.
 */
public class InterfaceClassDefine {
    private Class<?> define;
    private Class<?> targetClass;
    private List<MethodDefine> methodDefines;
    private byte[] bytecode;

    /**
     * Instantiates a new Interface class define.
     *
     * @param define        the define
     * @param targetClass   the target class
     * @param methodDefines the method defines
     */
    public InterfaceClassDefine(Class<?> define, Class<?> targetClass, List<MethodDefine> methodDefines) {
        this.define = define;
        this.targetClass = targetClass;
        this.methodDefines = methodDefines;
    }

    /**
     * Gets define.
     *
     * @return the define
     */
    public Class<?> getDefine() {
        return define;
    }

    /**
     * Gets target class.
     *
     * @return the target class
     */
    public Class<?> getTargetClass() {
        return targetClass;
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
    public InterfaceClassDefine setBytecode(byte[] bytecode) {
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
}
