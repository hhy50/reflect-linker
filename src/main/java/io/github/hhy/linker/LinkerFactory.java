package io.github.hhy.linker;

import io.github.hhy.linker.define.ClassDefineParse;
import io.github.hhy.linker.define.InvokeClassDefine;
import io.github.hhy.linker.exceptions.LinkerException;
import io.github.hhy.linker.generate.ClassImplGenerator;
import io.github.hhy.linker.sys.LinkerClassLoader;

import java.lang.reflect.Constructor;

public class LinkerFactory {

    public static <T> T createLinker(Class<T> define, Object target) throws LinkerException {
        if (target == null) {
            throw new NullPointerException("target");
        }

        try {
            InvokeClassDefine defineClass = ClassDefineParse.parseClass(define, target.getClass().getClassLoader());
            Class<?> implClass = ClassImplGenerator.generateImplClass(defineClass, target.getClass().getClassLoader());
            Constructor<?> constructor = implClass.getConstructors()[0];
            return (T) constructor.newInstance(target);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }

    public static <T, U> T createStaticLinker(Class<T> define, Class<U> targetClass) throws LinkerException {
        return createStaticLinker(define, targetClass.getClassLoader());
    }

    public static <T> T createStaticLinker(Class<T> define, ClassLoader classLoader) throws LinkerException {
        try {
            InvokeClassDefine defineClass = ClassDefineParse.parseClass(define, classLoader);
            Class<?> implClass = ClassImplGenerator.generateImplClass(defineClass, classLoader);

            Constructor<?> constructor = implClass.getConstructors()[0];
            return (T) constructor.newInstance((Object) null);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }

    static <T> T createSysLinker(Class<T> define, Object obj) throws LinkerException {
        try {
            InvokeClassDefine defineClass = ClassDefineParse.parseClass(define, ClassLoader.getSystemClassLoader());
            Class<?> implClass = ClassImplGenerator.generateImplClass(defineClass, ClassLoader.getSystemClassLoader());

            Constructor<?> constructor = implClass.getConstructors()[0];
            return (T) constructor.newInstance(obj);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }
}
