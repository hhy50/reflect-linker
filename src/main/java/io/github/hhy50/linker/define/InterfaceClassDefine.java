package io.github.hhy50.linker.define;

import java.util.List;


/**
 * <p>InterfaceClassDefine class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class InterfaceClassDefine {
    private Class<?> define;
    private Class<?> targetClass;
    private List<MethodDefine> methodDefines;
    private byte[] bytecode;

    /**
     * <p>Constructor for InterfaceClassDefine.</p>
     *
     * @param define a {@link java.lang.Class} object.
     * @param targetClass a {@link java.lang.Class} object.
     * @param methodDefines a {@link java.util.List} object.
     */
    public InterfaceClassDefine(Class<?> define, Class<?> targetClass, List<MethodDefine> methodDefines) {
        this.define = define;
        this.targetClass = targetClass;
        this.methodDefines = methodDefines;
    }

    /**
     * <p>Getter for the field <code>define</code>.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<?> getDefine() {
        return define;
    }

    /**
     * <p>Getter for the field <code>targetClass</code>.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<?> getTargetClass() {
        return targetClass;
    }

    /**
     * <p>Getter for the field <code>methodDefines</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<MethodDefine> getMethodDefines() {
        return methodDefines;
    }

    /**
     * <p>Setter for the field <code>bytecode</code>.</p>
     *
     * @param bytecode an array of {@link byte} objects.
     * @return a {@link io.github.hhy50.linker.define.InterfaceClassDefine} object.
     */
    public InterfaceClassDefine setBytecode(byte[] bytecode) {
        this.bytecode = bytecode;
        return this;
    }

    /**
     * <p>Getter for the field <code>bytecode</code>.</p>
     *
     * @return an array of {@link byte} objects.
     */
    public byte[] getBytecode() {
        return bytecode;
    }
}
