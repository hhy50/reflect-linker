package io.github.hhy.linker.exceptions;

public class ClassTypeNotMuchException extends RuntimeException {

    public ClassTypeNotMuchException(String class1, String class2) {
        super(class1+" not transform to class "+class2);
    }
}
