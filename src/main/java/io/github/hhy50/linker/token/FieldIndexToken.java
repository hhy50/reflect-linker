package io.github.hhy50.linker.token;

import java.util.List;

/**
 * The type Field index token.
 */
public class FieldIndexToken extends FieldToken {

    /**
     * The Index.
     */
    public List<String> index;

    /**
     * Instantiates a new Array index field token.
     *
     * @param fieldName the field name
     * @param index     the index
     */
    public FieldIndexToken(String fieldName, List<String> index) {
        super(fieldName);
        this.index = index;
    }

    @Override
    public String toString() {
        return fieldName+"["+String.join("][", index)+"]";
    }
}
