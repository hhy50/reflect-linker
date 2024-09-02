package io.github.hhy.linker.define.cl;

public class SysLinkerClassLoader extends java.lang.ClassLoader {

    private static final SysLinkerClassLoader INSTANCE = new SysLinkerClassLoader();

    private SysLinkerClassLoader() {

    }

    public static SysLinkerClassLoader getInstance() {
        return INSTANCE;
    }

    public Class<?> load(String className, byte[] bytecode) {
        return defineClass(className, bytecode, 0, bytecode.length);
    }
}

