package io.github.hhy.linker;

import io.github.hhy.linker.bytecode.ClassImplGenerator;
import io.github.hhy.linker.define.ClassDefineParse;
import io.github.hhy.linker.define.InvokeClassDefine;
import io.github.hhy.linker.exceptions.LinkerException;

import java.lang.reflect.Constructor;

public class LinkerFactory {

    public static <T> T createLinker(Class<T> define, Object target) throws LinkerException {
        if (target == null) {
            throw new NullPointerException("target");
        }

        try {
            InvokeClassDefine defineClass = ClassDefineParse.parseClass(define, target.getClass().getClassLoader());
            Class<?> implClass = ClassImplGenerator.generateImplClass(defineClass);
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
            Class<?> implClass = ClassImplGenerator.generateImplClass(defineClass);

            Constructor<?> constructor = implClass.getConstructors()[0];
            return (T) constructor.newInstance((Object) null);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }
}
