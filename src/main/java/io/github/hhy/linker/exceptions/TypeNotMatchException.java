package io.github.hhy.linker.exceptions;

import org.objectweb.asm.Type;

public class TypeNotMatchException extends RuntimeException {

    public TypeNotMatchException(Type t1, Type t2) {
        super("type '"+t1.getClassName()+"' not transform to type '"+t2.getClassName()+"'");
    }
}
