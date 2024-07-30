package io.github.hhy.linker.define;

public class BytecodeClassLoader extends java.lang.ClassLoader {

    public Class<?> load(String className, byte[] bytecode) {
        return defineClass(className, bytecode, 0, bytecode.length);
    }
}
