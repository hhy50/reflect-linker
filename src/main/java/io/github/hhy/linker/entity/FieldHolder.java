package io.github.hhy.linker.entity;

/**
 * <p>FieldHolder class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class FieldHolder {
    private final String owner;
    private final String fieldName;
    private final String fieldDesc;

    /**
     * <p>Constructor for FieldHolder.</p>
     *
     * @param owner a {@link java.lang.String} object.
     * @param fieldName a {@link java.lang.String} object.
     * @param fieldDesc a {@link java.lang.String} object.
     */
    public FieldHolder(String owner, String fieldName, String fieldDesc) {
        this.owner = owner;
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
    }

    /**
     * <p>Getter for the field <code>owner</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * <p>Getter for the field <code>fieldName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * <p>getDesc.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDesc() {
        return fieldDesc;
    }
}
