package io.github.hhy50.linker.exceptions;

/**
 * The type Verify exception.
 */
public class VerifyException extends RuntimeException {

    /**
     * Instantiates a new Verify exception.
     *
     * @param message the message
     */
    public VerifyException(String message) {
        super(message);
    }

    public VerifyException(Exception e) {
        super(e);
    }
}
