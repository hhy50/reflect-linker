package io.github.hhy50.linker.entity;

/**
 * The type Field holder.
 */
public class FieldDescriptor {
    private final String owner;
    private final String fieldName;
    private final String fieldDesc;

    /**
     * Instantiates a new Field holder.
     *
     * @param owner     the owner
     * @param fieldName the field name
     * @param fieldDesc the field desc
     */
    public FieldDescriptor(String owner, String fieldName, String fieldDesc) {
        this.owner = owner;
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
    }

    /**
     * Of field descriptor.
     *
     * @param owner     the owner
     * @param fieldName the field name
     * @param fieldDesc the field desc
     * @return the field descriptor
     */
    public static FieldDescriptor of(String owner, String fieldName, String fieldDesc) {
        return new FieldDescriptor(owner, fieldName, fieldDesc);
    }

    /**
     * Gets owner.
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Gets field name.
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return fieldDesc;
    }
}
