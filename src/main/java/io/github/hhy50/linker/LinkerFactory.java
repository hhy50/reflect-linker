package io.github.hhy50.linker;

import io.github.hhy50.linker.define.BytecodeClassLoader;
import io.github.hhy50.linker.define.ClassDefineParse;
import io.github.hhy50.linker.define.InterfaceClassDefine;
import io.github.hhy50.linker.define.cl.SysLinkerClassLoader;
import io.github.hhy50.linker.exceptions.LinkerException;
import io.github.hhy50.linker.generate.ClassImplGenerator;

import java.io.IOException;
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
            ClassLoader cl = target.getClass().getClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
            Constructor<?> constructor = create(define, cl).getConstructors()[0];
            return (T) constructor.newInstance(target);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }

    /**
     * <p>createStaticLinker.</p>
     *
     * @param define a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a T object.
     * @param cl a {@link java.lang.ClassLoader} object.
     * @throws io.github.hhy50.linker.exceptions.LinkerException if any.
     */
    public static <T> T createStaticLinker(Class<T> define, ClassLoader cl) throws LinkerException {
        try {
            Constructor<?> constructor = create(define, cl).getConstructors()[0];
            return (T) constructor.newInstance((Object) null);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }

    static Class<?> create(Class<?> define, ClassLoader cl) throws ClassNotFoundException, IOException {
        InterfaceClassDefine defineClass = ClassDefineParse.parseClass(define, cl);
        if (defineClass.getBytecode() == null) {
            ClassImplGenerator.generateBytecode(defineClass, cl);
        }
        return BytecodeClassLoader.load(cl, define.getName()+"$impl", defineClass.getBytecode());
    }

    static <T> T createSysLinker(Class<T> define, Object obj) throws LinkerException {
        try {
            Constructor<?> constructor = create(define, SysLinkerClassLoader.getInstance()).getConstructors()[0];
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
    }
}
