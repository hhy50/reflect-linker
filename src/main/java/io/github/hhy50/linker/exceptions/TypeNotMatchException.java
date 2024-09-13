package io.github.hhy50.linker.exceptions;

import org.objectweb.asm.Type;

/**
 * <p>TypeNotMatchException class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class TypeNotMatchException extends RuntimeException {

    /**
     * <p>Constructor for TypeNotMatchException.</p>
     *
     * @param t1 a {@link org.objectweb.asm.Type} object.
     * @param t2 a {@link org.objectweb.asm.Type} object.
     */
    public TypeNotMatchException(Type t1, Type t2) {
        super("type '"+t1.getClassName()+"' not transform to type '"+t2.getClassName()+"'");
    }
}
