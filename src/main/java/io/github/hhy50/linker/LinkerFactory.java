package io.github.hhy50.linker;

import io.github.hhy50.linker.define.BytecodeClassLoader;
import io.github.hhy50.linker.define.ClassDefineParse;
import io.github.hhy50.linker.define.InterfaceImplClass;
import io.github.hhy50.linker.define.cl.SysLinkerClassLoader;
import io.github.hhy50.linker.exceptions.LinkerException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The type Linker factory.
 */
public class LinkerFactory {

    /**
     * Create linker collect collection.
     *
     * @param <T>    the type parameter
     * @param define the define
     * @param target the target
     * @return the collection
     * @throws LinkerException the linker exception
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> createLinkerCollect(Class<T> define, Collection<?> target) throws LinkerException {
        if (target == null || target.isEmpty()) {
            return (Collection<T>) target;
        }

        List<T> linkers = new ArrayList<>();
        for (Object o : target) {
            linkers.add(createLinker(define, o));
        }
        return (Collection<T>) linkers;
    }

    /**
     * Create linker t.
     *
     * @param <T>    the type parameter
     * @param define the define
     * @param target the target
     * @return the t
     * @throws LinkerException the linker exception
     */
    public static <T> T createLinker(Class<T> define, Object target) throws LinkerException {
        if (target == null) {
            throw new NullPointerException("target");
        }
        try {
            ClassLoader cl = define.getClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
            Class<?> gClass = create(define, target.getClass(), cl);
            return (T) newInstance(target, null, gClass);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }

    /**
     * Create static linker t.
     *
     * @param <T>         the type parameter
     * @param define      the define
     * @param targetClass the target class
     * @return the t
     * @throws LinkerException the linker exception
     */
    public static <T> T createStaticLinker(Class<T> define, Class<?> targetClass) throws LinkerException {
        try {
            ClassLoader cl = define.getClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }

            Class<?> gClass = create(define, targetClass, cl);
            return (T) newInstance(null, targetClass, gClass);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }

    /**
     * Create static linker t.
     *
     * @param <T>    the type parameter
     * @param define the define
     * @param cl     the cl
     * @return the t
     * @throws LinkerException the linker exception
     */
    public static <T> T createStaticLinker(Class<T> define, ClassLoader cl) throws LinkerException {
        try {
            Class<?> gClass = create(define, cl);
            return (T) newInstance(null, null, gClass);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }

    /**
     * Create class.
     *
     * @param define      the define
     * @param targetClass the target class
     * @param cl          the cl
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     * @throws IOException            the io exception
     */
    static Class<?> create(Class<?> define, Class<?> targetClass, ClassLoader cl) throws ClassNotFoundException, IOException {
        InterfaceImplClass defineClass = ClassDefineParse.parseClass(define, targetClass);
        return BytecodeClassLoader.load(cl, defineClass.getClassName(), defineClass.getBytecode());
    }

    /**
     * Create class.
     *
     * @param define the define
     * @param cl     the cl
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     * @throws IOException            the io exception
     */
    static Class<?> create(Class<?> define, ClassLoader cl) throws ClassNotFoundException, IOException {
        InterfaceImplClass defineClass = ClassDefineParse.parseClass(define, cl);
        return BytecodeClassLoader.load(cl, defineClass.getClassName(), defineClass.getBytecode());
    }

    /**
     * Create sys linker t.
     *
     * @param <T>    the type parameter
     * @param define the define
     * @param obj    the obj
     * @return the t
     * @throws LinkerException the linker exception
     */
    static <T> T createSysLinker(Class<T> define, Object obj) throws LinkerException {
        try {
            Class<?> gClass;
            if (obj == null) {
                gClass = create(define, SysLinkerClassLoader.getInstance());
            } else {
                gClass = create(define, obj.getClass(), SysLinkerClassLoader.getInstance());
            }
            return (T) newInstance(obj, null, gClass);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }

    /**
     * Sets output path.
     *
     * @param path the path
     */
    public static void setOutputPath(String path) {
        System.setProperty("linker.output.path", path);
        File outputPath = new File(path);
        if (!outputPath.exists()) {
            outputPath.mkdirs();
        }
    }

    /**
     * New instance object.
     *
     * @param obj       the obj
     * @param clazz     the clazz
     * @param implClass the impl class
     * @return the object
     * @throws InvocationTargetException the invocation target exception
     * @throws InstantiationException    the instantiation exception
     * @throws IllegalAccessException    the illegal access exception
     */
    static Object newInstance(Object obj, Class<?> clazz, Class<?> implClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?>[] constructors = implClass.getConstructors();
        if (constructors.length == 1) {
            return constructors[0].newInstance(obj);
        }
        if (constructors[0].getParameterCount() == 2) {
            return constructors[0].newInstance(obj, clazz);
        }
        return constructors[1].newInstance(obj, clazz);
    }
}
