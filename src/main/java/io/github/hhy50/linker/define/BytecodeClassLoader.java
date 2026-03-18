package io.github.hhy50.linker.define;


import io.github.hhy50.linker.AccessTool;
import io.github.hhy50.linker.define.cl.SysLinkerClassLoader;
import io.github.hhy50.linker.exceptions.LinkerException;
import io.github.hhy50.linker.syslinker.ClassLoaderLinker;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * The type Bytecode class loader.
 */
public class BytecodeClassLoader{

    private static final Map<ClassLoader, Map<String, SoftReference<Class<?>>>> NAMESPACE = new WeakHashMap<>();

    /**
     * Load class.
     *
     * @param classLoader the class loader
     * @param className   the class name
     * @param bytecode    the bytecode
     * @return the class
     */
    public synchronized static Class<?> load(ClassLoader classLoader, String className, byte[] bytecode) {
        Map<String, SoftReference<Class<?>>> clNameSpace = NAMESPACE.computeIfAbsent(classLoader, cl -> new HashMap<>());
        try {
            Class<?> cached = getCachedClass(clNameSpace, className);
            if (cached != null) {
                return cached;
            }

            Class<?> defined;
            if (classLoader instanceof SysLinkerClassLoader) {
                defined = ((SysLinkerClassLoader) classLoader).load(className, bytecode);
            } else {
                ClassLoaderLinker clLinker = AccessTool.createSysLinker(ClassLoaderLinker.class, classLoader);
                defined = clLinker.defineClass(className, bytecode, 0, bytecode.length);
            }
            clNameSpace.put(className, new SoftReference<>(defined));
            return defined;
        } catch (LinkerException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> getCachedClass(Map<String, SoftReference<Class<?>>> namespace, String className) {
        SoftReference<Class<?>> classRef = namespace.get(className);
        if (classRef == null) {
            return null;
        }
        Class<?> cached = classRef.get();
        if (cached == null) {
            namespace.remove(className);
        }
        return cached;
    }
}
