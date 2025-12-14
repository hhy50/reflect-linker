package io.github.hhy50.linker.define.field;

/**
 * The type Runtime field ref.
 */
public class RuntimeFieldRef extends FieldRef {

    private Boolean designateStatic;


    /**
     * Instantiates a new Runtime field ref.
     *
     * @param fullName      the prev
     * @param fieldName the field name
     */
    public RuntimeFieldRef(String fullName, String fieldName) {
        super(fullName, fieldName);
    }

    /**
     * Is designate static boolean.
     *
     * @return the boolean
     */
    public Boolean isDesignateStatic() {
        return designateStatic;
    }

    /**
     * Designate static.
     *
     * @param isStatic the is static
     */
    public void designateStatic(boolean isStatic) {
        this.designateStatic = isStatic;
    }
}
