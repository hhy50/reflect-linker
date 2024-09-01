package io.github.hhy.linker.define;


import io.github.hhy.linker.AccessTool;
import io.github.hhy.linker.exceptions.LinkerException;
import io.github.hhy.linker.sys.LinkerClassLoader;

import java.util.HashMap;
import java.util.Map;

public class BytecodeClassLoader extends ClassLoader {

    private static final Map<ClassLoader, Map<String, Class<?>>> NAMESPACE = new HashMap<>();

    static {

    }

    public synchronized static Class<?> load(ClassLoader classLoader, String className, byte[] bytecode) {
        Map<String, Class<?>> clNameSpace = NAMESPACE.computeIfAbsent(classLoader, cl -> new HashMap<>());
        try {
            LinkerClassLoader clLinker = AccessTool.createSysLinker(LinkerClassLoader.class, classLoader);
            return clNameSpace.computeIfAbsent(className, n -> clLinker.defineClass(className, bytecode, 0, bytecode.length));
        } catch (LinkerException e) {
            throw new RuntimeException(e);
        }
    }
}
