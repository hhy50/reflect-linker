package io.github.hhy50.linker;

import io.github.hhy50.linker.define.BytecodeClassLoader;
import io.github.hhy50.linker.define.ClassDefineParse;
import io.github.hhy50.linker.define.InterfaceImplClassDefine;
import io.github.hhy50.linker.define.cl.SysLinkerClassLoader;
import io.github.hhy50.linker.exceptions.LinkerException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * <p>LinkerFactory class.</p>
 *
 * @author hanhaiyang
 * @version $Id : $Id
 */
public class LinkerFactory {

    /**
     *
     * @param define
     * @param target
     * @return
     * @param <T>
     * @throws LinkerException
     */
    public static <T> T createLinkerCollect(Class<T> define, Collection<?> target) throws LinkerException {
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
     * <p>createLinker.</p>
     *
     * @param <T>    a T object.
     * @param define a {@link java.lang.Class} object.
     * @param target a {@link java.lang.Object} object.
     * @return a T object.
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
     * <p>createStaticLinker.</p>
     *
     * @param <T>         a T object.
     * @param define      a {@link java.lang.Class} object.
     * @param targetClass a {@link java.lang.Class} object.
     * @return a T object.
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
     * <p>createStaticLinker.</p>
     *
     * @param <T>    a T object.
     * @param define a {@link java.lang.Class} object.
     * @param cl     a {@link java.lang.ClassLoader} object.
     * @return a T object.
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
        InterfaceImplClassDefine defineClass = ClassDefineParse.parseClass(define, targetClass);
        return BytecodeClassLoader.load(cl, defineClass.getClassName(), defineClass.getBytecode());
    }

    static Class<?> create(Class<?> define, ClassLoader cl) throws ClassNotFoundException, IOException {
        InterfaceImplClassDefine defineClass = ClassDefineParse.parseClass(define, cl);
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
     * <p>setOutputPath.</p>
     *
     * @param path a {@link java.lang.String} object.
     */
    public static void setOutputPath(String path) {
        System.setProperty("linker.output.path", path);
        File outputPath = new File(path);
        if (!outputPath.exists()) {
            outputPath.mkdirs();
        }
    }

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
