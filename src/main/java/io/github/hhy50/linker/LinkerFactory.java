package io.github.hhy50.linker;

import io.github.hhy50.linker.define.BytecodeClassLoader;
import io.github.hhy50.linker.define.ClassDefineParse;
import io.github.hhy50.linker.define.InterfaceImplClassDefine;
import io.github.hhy50.linker.define.cl.SysLinkerClassLoader;
import io.github.hhy50.linker.exceptions.LinkerException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * <p>LinkerFactory class.</p>
 *
 * @author hanhaiyang
 * @version $Id : $Id
 */
public class LinkerFactory {

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
            Class<?> targetClass = target.getClass();
            ClassLoader cl = target.getClass().getClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
            Constructor<?> constructor = create(define, targetClass, cl).getConstructors()[0];
            return (T) constructor.newInstance(target);
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
            ClassLoader cl = targetClass.getClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
//            Constructor<?>[] constructors = create(define, targetClass, cl).getConstructors();
//            if (constructors.length == 1) {
//                return (T) constructors[0].newInstance(targetClass);
//            }
            Constructor<?> constructor = create(define, targetClass, cl).getConstructors()[0];
            return (T) constructor.newInstance(targetClass);
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
            Constructor<?> constructor = create(define, null, cl).getConstructors()[0];
            return (T) constructor.newInstance((Object) null);
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
        InterfaceImplClassDefine defineClass = ClassDefineParse.parseClass(define, targetClass, cl);
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
            Constructor<?> constructor = create(define, obj != null ? obj.getClass() : null, SysLinkerClassLoader.getInstance()).getConstructors()[0];
            return (T) constructor.newInstance(obj);
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
}
