package io.github.hhy.linker.exceptions;

/**
 * <p>LinkerException class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class LinkerException extends Exception {
    /**
     * <p>Constructor for LinkerException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause a {@link java.lang.Exception} object.
     */
    public LinkerException(String message, Exception cause) {
        super(message, cause);
    }
}
