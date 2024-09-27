package io.github.hhy50.linker.exceptions;

import org.objectweb.asm.Type;

/**
 * The type Type not match exception.
 */
public class TypeNotMatchException extends RuntimeException {

    /**
     * Instantiates a new Type not match exception.
     *
     * @param t1 the t 1
     * @param t2 the t 2
     */
    public TypeNotMatchException(Type t1, Type t2) {
        super("type '"+t1.getClassName()+"' not transform to type '"+t2.getClassName()+"'");
    }
}
