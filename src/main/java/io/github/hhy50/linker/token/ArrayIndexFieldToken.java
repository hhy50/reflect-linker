package io.github.hhy50.linker.token;

/**
 * The type Array index field token.
 */
public class ArrayIndexFieldToken extends FieldToken {

    /**
     * The Index.
     */
    public String index;

    /**
     * Instantiates a new Array index field token.
     *
     * @param fieldName the field name
     * @param index     the index
     */
    public ArrayIndexFieldToken(String fieldName, String index) {
        super(fieldName);
        this.index = index;
    }

    @Override
    public String toString() {
        return fieldName+"["+index+"]";
    }
}
