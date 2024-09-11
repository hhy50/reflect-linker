package io.github.hhy.linker.define;


import io.github.hhy.linker.AccessTool;
import io.github.hhy.linker.define.cl.SysLinkerClassLoader;
import io.github.hhy.linker.exceptions.LinkerException;
import io.github.hhy.linker.syslinker.ClassLoaderLinker;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>BytecodeClassLoader class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class BytecodeClassLoader{

    private static final Map<ClassLoader, Map<String, Class<?>>> NAMESPACE = new HashMap<>();

    /**
     * <p>load.</p>
     *
     * @param classLoader a {@link java.lang.ClassLoader} object.
     * @param className a {@link java.lang.String} object.
     * @param bytecode an array of {@link byte} objects.
     * @return a {@link java.lang.Class} object.
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
