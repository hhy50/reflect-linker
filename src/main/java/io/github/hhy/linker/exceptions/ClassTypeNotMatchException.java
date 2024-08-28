package io.github.hhy.linker.exceptions;

public class ClassTypeNotMatchException extends RuntimeException {

    public ClassTypeNotMatchException(String class1, String class2) {
        super(class1+" not transform to class "+class2);
    }
}
