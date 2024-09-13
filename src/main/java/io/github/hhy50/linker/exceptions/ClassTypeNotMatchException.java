package io.github.hhy50.linker.exceptions;

/**
 * <p>ClassTypeNotMatchException class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class ClassTypeNotMatchException extends RuntimeException {

    /**
     * <p>Constructor for ClassTypeNotMatchException.</p>
     *
     * @param class1 a {@link java.lang.String} object.
     * @param class2 a {@link java.lang.String} object.
     */
    public ClassTypeNotMatchException(String class1, String class2) {
        super(class1+" not transform to class "+class2);
    }
}
