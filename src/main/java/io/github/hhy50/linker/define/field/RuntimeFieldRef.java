package io.github.hhy50.linker.define.field;

/**
 * The type Runtime field ref.
 */
public class RuntimeFieldRef extends FieldRef {

    private boolean designateStatic;

    private boolean isStatic;

    /**
     * Instantiates a new Runtime field ref.
     *
     * @param prev      the prev
     * @param fieldName the field name
     */
    public RuntimeFieldRef(FieldRef prev, String fieldName) {
        super(prev, fieldName);
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
}
