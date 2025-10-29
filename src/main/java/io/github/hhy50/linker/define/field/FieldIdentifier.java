package io.github.hhy50.linker.define.field;

import org.objectweb.asm.Type;



public class FieldIdentifier {
    private final String fieldName;
    private final String fullName;
    private final Type fieldType;

    public FieldIdentifier(String fullName, String fieldName, Type fieldType) {
        this.fieldName = fieldName;
        this.fullName = fullName;
        this.fieldType = fieldType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FieldIdentifier) {
            FieldIdentifier other = (FieldIdentifier) obj;
            return this.fullName.equals(other.fullName) && this.fieldName.equals(other.fieldName)
                    && this.fieldType.equals(other.fieldType);
        }
        return false;
    }
}
