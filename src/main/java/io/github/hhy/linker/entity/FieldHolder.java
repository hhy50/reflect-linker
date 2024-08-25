package io.github.hhy.linker.entity;

public class FieldHolder {
    private final String owner;
    private final String fieldName;
    private final String fieldDesc;

    public FieldHolder(String owner, String fieldName, String fieldDesc) {
        this.owner = owner;
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
    }

    public String getOwner() {
        return owner;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDesc() {
        return fieldDesc;
    }
}
