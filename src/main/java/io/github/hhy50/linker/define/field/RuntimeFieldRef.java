package io.github.hhy50.linker.define.field;

/**
 * <p>RuntimeFieldRef class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class RuntimeFieldRef extends FieldRef {

    /**
     * 是否指定了静态
     */
    private boolean designateStatic;

    /**
     * 是否是静态
     */
    private boolean isStatic;

    /**
     * <p>Constructor for RuntimeFieldRef.</p>
     *
     * @param prev a {@link FieldRef} object.
     * @param objName a {@link java.lang.String} object.
     * @param fieldName a {@link java.lang.String} object.
     */
    public RuntimeFieldRef(FieldRef prev, String objName, String fieldName) {
        super(prev, objName, fieldName);
    }

    /**
     * <p>isDesignateStatic.</p>
     *
     * @return a boolean.
     */
    public boolean isDesignateStatic() {
        return designateStatic;
    }

    /**
     * <p>isStatic.</p>
     *
     * @return a boolean.
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * <p>designateStatic.</p>
     *
     * @param isStatic a boolean.
     */
    public void designateStatic(boolean isStatic) {
        this.designateStatic = true;
        this.isStatic = isStatic;
    }
}
