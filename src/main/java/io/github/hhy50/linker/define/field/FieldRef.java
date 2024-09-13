package io.github.hhy50.linker.define.field;


import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.Type;

/**
 * 用来表示目标字段
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public abstract class FieldRef {

    /**
     * 对象名
     */
    private String objName;

    /**
     * 字段名
     */
    public String fieldName;

    private String fullName;

    /**
     * 上一级字段
     */
    private FieldRef prev;

    /**
     * <p>Constructor for FieldRef.</p>
     *
     * @param prev a {@link FieldRef} object.
     * @param objName a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     */
    public FieldRef(FieldRef prev, String objName, String name) {
        this.prev = prev;
        this.objName = objName;
        this.fieldName = name;
    }

    /**
     * <p>getUniqueName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUniqueName() {
        String prefix = "";
        if (prev != null) {
            prefix = prev.getUniqueName()+"_$_";
        }
        return prefix+fieldName;
    }

    /**
     * <p>getGetterName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGetterName() {
        return getUniqueName()+"_getter_mh";
    }

    /**
     * <p>getSetterName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSetterName() {
        return getUniqueName()+"_setter_mh";
    }

    /**
     * <p>getType.</p>
     *
     * @return a {@link org.objectweb.asm.Type} object.
     */
    public Type getType() {
        return ObjectVar.TYPE;
    }

    /**
     * <p>Getter for the field <code>prev</code>.</p>
     *
     * @return a {@link FieldRef} object.
     */
    public FieldRef getPrev() {
        return prev;
    }

    /**
     * <p>Setter for the field <code>fullName</code>.</p>
     *
     * @param fullName a {@link java.lang.String} object.
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * <p>Getter for the field <code>fullName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFullName() {
        if (fullName == null) {
            return getUniqueName();
        }
        return fullName;
    }

    /**
     * <p>toRuntime.</p>
     *
     * @return a {@link FieldRef} object.
     */
    public FieldRef toRuntime() {
        if (this instanceof RuntimeFieldRef) {
            return this;
        }

        RuntimeFieldRef runtime = new RuntimeFieldRef(this.getPrev(), this.objName, this.fieldName);
        runtime.designateStatic(((EarlyFieldRef) this).isStatic());
        return runtime;
    }
}
