package io.github.hhy.linker.token;


/**
 * <p>MapKeyToken class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class MapKeyToken extends FieldToken {

    public String key;

    /**
     * <p>Constructor for MapKeyToken.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @param key a {@link java.lang.String} object.
     */
    public MapKeyToken(String fieldName, String key) {
        super(fieldName);
        this.key = key;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return fieldName+"['"+key+"']";
    }
}
