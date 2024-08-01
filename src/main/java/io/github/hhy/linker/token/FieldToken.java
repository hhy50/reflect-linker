package io.github.hhy.linker.token;


public class FieldToken extends Token {

    public String fieldName;

    public Token next;

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
