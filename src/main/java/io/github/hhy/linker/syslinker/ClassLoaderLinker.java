package io.github.hhy.linker.syslinker;

import io.github.hhy.linker.annotations.Target;


/**
 * <p>ClassLoaderLinker interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
@Target.Bind("java.lang.ClassLoader")
public interface ClassLoaderLinker {

    /**
     * <p>defineClass.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param b an array of {@link byte} objects.
     * @param off a int.
     * @param len a int.
     * @return a {@link java.lang.Class} object.
     */
    Class<?> defineClass(String name, byte[] b, int off, int len);
}
