package io.github.hhy50.linker.token;


/**
 * The type Map key token.
 */
public class MapKeyToken extends FieldToken {

    /**
     * The Key.
     */
    public String key;

    /**
     * Instantiates a new Map key token.
     *
     * @param fieldName the field name
     * @param key       the key
     */
    public MapKeyToken(String fieldName, String key) {
        super(fieldName);
        this.key = key;
    }

    @Override
    public String toString() {
        return fieldName+"['"+key+"']";
    }
}
