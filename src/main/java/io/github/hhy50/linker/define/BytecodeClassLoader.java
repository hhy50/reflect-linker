package io.github.hhy50.linker.define;


import io.github.hhy50.linker.AccessTool;
import io.github.hhy50.linker.define.cl.SysLinkerClassLoader;
import io.github.hhy50.linker.exceptions.LinkerException;
import io.github.hhy50.linker.syslinker.ClassLoaderLinker;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Bytecode class loader.
 */
public class BytecodeClassLoader{

    private static final Map<ClassLoader, Map<String, Class<?>>> NAMESPACE = new HashMap<>();

    /**
     * Load class.
     *
     * @param classLoader the class loader
     * @param className   the class name
     * @param bytecode    the bytecode
     * @return the class
     */
    public synchronized static Class<?> load(ClassLoader classLoader, String className, byte[] bytecode) {
        Map<String, Class<?>> clNameSpace = NAMESPACE.computeIfAbsent(classLoader, cl -> new HashMap<>());
        try {
            if (classLoader instanceof SysLinkerClassLoader) {
                return clNameSpace.computeIfAbsent(className, n -> ((SysLinkerClassLoader) classLoader).load(className, bytecode));
            } else {
                ClassLoaderLinker clLinker = AccessTool.createSysLinker(ClassLoaderLinker.class, classLoader);
                return clNameSpace.computeIfAbsent(className, n -> clLinker.defineClass(className, bytecode, 0, bytecode.length));
            }
        } catch (LinkerException e) {
            throw new RuntimeException(e);
        }
    }
}
