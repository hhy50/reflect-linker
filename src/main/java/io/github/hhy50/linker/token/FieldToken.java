package io.github.hhy50.linker.token;


/**
 * The type Field token.
 */
public class FieldToken extends Token {

    /**
     * The Field name.
     */
    public String fieldName;

    /**
     * Instantiates a new Field token.
     *
     * @param fieldName the field name
     */
    public FieldToken(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String value() {
        return fieldName;
    }

    @Override
    public String toString() {
        return fieldName;
    }
}
