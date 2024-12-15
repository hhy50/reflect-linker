package io.github.hhy50.linker.define;

import java.util.List;


/**
 * The type Interface class define.
 */
public class InterfaceImplClassDefine {
    private Class<?> define;
    private Class<?> targetClass;
    private List<MethodDefine> methodDefines;
    private String className;
    private byte[] bytecode;

    /**
     * Instantiates a new Interface class define.
     *
     * @param define        the define
     * @param targetClass   the target class
     * @param methodDefines the method defines
     */
    public InterfaceImplClassDefine(Class<?> define, Class<?> targetClass, List<MethodDefine> methodDefines) {
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
    public InterfaceImplClassDefine setBytecode(byte[] bytecode) {
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

    /**
     * Sets class name.
     *
     * @param className the class name
     */
    public void setClassName(String className) {
        this.className = className;
    }
}
