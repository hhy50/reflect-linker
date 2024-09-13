package io.github.hhy50.linker.define.cl;

/**
 * <p>SysLinkerClassLoader class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class SysLinkerClassLoader extends java.lang.ClassLoader {

    private static final SysLinkerClassLoader INSTANCE = new SysLinkerClassLoader();

    private SysLinkerClassLoader() {

    }

    /**
     * <p>getInstance.</p>
     *
     * @return a {@link SysLinkerClassLoader} object.
     */
    public static SysLinkerClassLoader getInstance() {
        return INSTANCE;
    }

    /**
     * <p>load.</p>
     *
     * @param className a {@link java.lang.String} object.
     * @param bytecode an array of {@link byte} objects.
     * @return a {@link java.lang.Class} object.
     */
    public Class<?> load(String className, byte[] bytecode) {
        return defineClass(className, bytecode, 0, bytecode.length);
    }
}

