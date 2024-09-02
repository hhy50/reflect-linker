package io.github.hhy.linker.syslinker;

import io.github.hhy.linker.annotations.Target;


@Target.Bind("java.lang.ClassLoader")
public interface LinkerClassLoader {

    /**
     *
     * @param name
     * @param b
     * @param off
     * @param len
     * @return
     */
    Class<?> defineClass(String name, byte[] b, int off, int len);
}
