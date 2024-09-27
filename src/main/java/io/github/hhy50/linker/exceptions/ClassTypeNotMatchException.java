package io.github.hhy50.linker.exceptions;

/**
 * The type Class type not match exception.
 */
public class ClassTypeNotMatchException extends RuntimeException {

    /**
     * Instantiates a new Class type not match exception.
     *
     * @param class1 the class 1
     * @param class2 the class 2
     */
    public ClassTypeNotMatchException(String class1, String class2) {
        super(class1+" not transform to class "+class2);
    }
}
