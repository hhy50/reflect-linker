package io.github.hhy.linker.token;

public class ArrayIndexFieldToken extends FieldToken {

    public String index;

    public ArrayIndexFieldToken(String fieldName, String index) {
        super(fieldName);
        this.index = index;
    }

    @Override
    public String toString() {
        return fieldName+"["+index+"]";
    }
}
