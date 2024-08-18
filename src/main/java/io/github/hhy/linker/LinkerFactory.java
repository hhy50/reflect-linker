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
        InvokeClassDefine defineClass = ClassDefineParse.parseClass(define, target.getClass());
        Class<?> implClass = ClassImplGenerator.generateImplClass(defineClass);
        try {
            Constructor<?> constructor = implClass.getConstructors()[0];
            return (T) constructor.newInstance(target);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }

    public static <T, U> T createStaticLinker(Class<T> define, Class<U> bindClass) throws LinkerException {
        InvokeClassDefine defineClass = ClassDefineParse.parseClass(define, bindClass);
        Class<?> implClass = ClassImplGenerator.generateImplClass(defineClass);
        try {
            Constructor<?> constructor = implClass.getConstructors()[0];
            return (T) constructor.newInstance((U) null);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }

    public static <T> T newInstance(Class<T> define, Class<?> bindClass, Object... args) throws LinkerException {
        InvokeClassDefine defineClass = ClassDefineParse.parseClass(define, bindClass);
        Class<?> implClass = ClassImplGenerator.generateImplClass(defineClass);
        try {
            Object target = bindClass.newInstance();
            Constructor<?> constructor = implClass.getConstructors()[0];
            return (T) constructor.newInstance(target);
        } catch (Exception e) {
            throw new LinkerException("create linker exception", e);
        }
    }
}
