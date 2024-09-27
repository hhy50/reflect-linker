package io.github.hhy50.linker.entity;

/**
 * The type Field holder.
 */
public class FieldHolder {
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
    public FieldHolder(String owner, String fieldName, String fieldDesc) {
        this.owner = owner;
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
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
