package io.github.hhy.linker.token;


/**
 * <p>FieldToken class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class FieldToken extends Token {

    public String fieldName;

    /**
     * <p>Constructor for FieldToken.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     */
    public FieldToken(String fieldName) {
        this.fieldName = fieldName;
    }

    /** {@inheritDoc} */
    @Override
    public String value() {
        return fieldName;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return fieldName;
    }
}
