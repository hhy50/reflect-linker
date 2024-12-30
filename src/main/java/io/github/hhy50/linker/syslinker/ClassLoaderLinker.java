package io.github.hhy50.linker.syslinker;

import io.github.hhy50.linker.annotations.Runtime;


/**
 * The interface Class loader linker.
 */
@Runtime
public interface ClassLoaderLinker {

    /**
     * Define class class.
     *
     * @param name the name
     * @param b    the b
     * @param off  the off
     * @param len  the len
     * @return the class
     */
    Class<?> defineClass(String name, byte[] b, int off, int len);
}
