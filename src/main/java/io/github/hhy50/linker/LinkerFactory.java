package io.github.hhy50.linker;

import io.github.hhy50.linker.define.ClassDefineParse;
import io.github.hhy50.linker.define.InterfaceClassDefine;
import io.github.hhy50.linker.define.cl.SysLinkerClassLoader;
import io.github.hhy50.linker.exceptions.LinkerException;
import io.github.hhy50.linker.generate.ClassImplGenerator;

import java.lang.reflect.Constructor;

/**
 * <p>LinkerFactory class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class LinkerFactory {

    /**
     * <p>createLinker.</p>
     *
     * @param define a {@link java.lang.Class} object.
     * @param target a {@link java.lang.Object} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws io.github.hhy50.linker.exceptions.LinkerException if any.
     */
    public static <T> T createLinker(Class<T> define, Object target) throws LinkerException {
        if (target == null) {
            throw new NullPointerException("target");
        }

        try {
            ClassLoader classLoader = target.getClass().getClassLoader();
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }

            InterfaceClassDefine defineClass = ClassDefineParse.parseClass(define, classLoader);
            Class<?> implClass = ClassImplGenerator.generateImplClass(defineClass, classLoader);
            Constructor<?> constructor = implClass.getConstructors()[0];
            return (T) constructor.newInstance(target);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }

    /**
     * <p>createStaticLinker.</p>
     *
     * @param define a {@link java.lang.Class} object.
     * @param classLoader a {@link java.lang.ClassLoader} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws io.github.hhy50.linker.exceptions.LinkerException if any.
     */
    public static <T> T createStaticLinker(Class<T> define, ClassLoader classLoader) throws LinkerException {
        try {
            InterfaceClassDefine defineClass = ClassDefineParse.parseClass(define, classLoader);
            Class<?> implClass = ClassImplGenerator.generateImplClass(defineClass, classLoader);

            Constructor<?> constructor = implClass.getConstructors()[0];
            return (T) constructor.newInstance((Object) null);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }

    static <T> T createSysLinker(Class<T> define, Object obj) throws LinkerException {
        try {
            InterfaceClassDefine defineClass = ClassDefineParse.parseClass(define, SysLinkerClassLoader.getInstance());
            Class<?> implClass = ClassImplGenerator.generateImplClass(defineClass, SysLinkerClassLoader.getInstance());

            Constructor<?> constructor = implClass.getConstructors()[0];
            return (T) constructor.newInstance(obj);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }
}
