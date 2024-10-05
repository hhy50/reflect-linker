package io.github.hhy50.linker.exceptions;

/**
 * The type Method not found exception.
 */
public class MethodNotFoundException extends RuntimeException {
    /**
     * Instantiates a new Method not found exception.
     *
     * @param clazz the clazz
     * @param name  the name
     */
    public MethodNotFoundException(Class<?> clazz, String name) {
        super(String.format("not found method '%s' in class '%s'", clazz, name));
    }
}
