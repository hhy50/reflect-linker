package io.github.hhy50.linker.define.cl;

/**
 * The type Sys linker class loader.
 */
public class SysLinkerClassLoader extends java.lang.ClassLoader {

    private static final SysLinkerClassLoader INSTANCE = new SysLinkerClassLoader();

    private SysLinkerClassLoader() {
        super(SysLinkerClassLoader.class.getClassLoader());
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SysLinkerClassLoader getInstance() {
        return INSTANCE;
    }

    /**
     * Load class.
     *
     * @param className the class name
     * @param bytecode  the bytecode
     * @return the class
     */
    public Class<?> load(String className, byte[] bytecode) {
        return defineClass(className, bytecode, 0, bytecode.length);
    }
}

