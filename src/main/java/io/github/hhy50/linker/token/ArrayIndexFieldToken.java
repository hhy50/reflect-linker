package io.github.hhy50.linker.token;

/**
 * <p>ArrayIndexFieldToken class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class ArrayIndexFieldToken extends FieldToken {

    public String index;

    /**
     * <p>Constructor for ArrayIndexFieldToken.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @param index a {@link java.lang.String} object.
     */
    public ArrayIndexFieldToken(String fieldName, String index) {
        super(fieldName);
        this.index = index;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return fieldName+"["+index+"]";
    }
}
