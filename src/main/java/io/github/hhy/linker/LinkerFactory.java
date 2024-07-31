package io.github.hhy.linker;

import io.github.hhy.linker.bytecode.ClassImplGenerator;
import io.github.hhy.linker.define.InvokeClassDefine;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;

public class LinkerFactory {
    public static  <T> T newInstance(Class<T> define, String targetClass) throws ClassNotFoundException {
        return newInstance(define, Class.forName(targetClass));
    }

    @SneakyThrows
    public static <T> T newInstance(Class<T> define, Class<?> targetClass, Object... args) {
        InvokeClassDefine defineClass = InvokeClassDefine.parse(define, targetClass);
        Class<?> implClass = ClassImplGenerator.generateImplClass(defineClass);

        Object o = targetClass.newInstance();
        Constructor<?> constructor = implClass.getConstructors()[0];
        return (T) constructor.newInstance(o);
    }

    @SneakyThrows
    public static <T> T createLinker(Class<T> define, Object target) {
        InvokeClassDefine defineClass = InvokeClassDefine.parse(define, target.getClass());
        Class<?> implClass = ClassImplGenerator.generateImplClass(defineClass);

        Constructor<?> constructor = implClass.getConstructors()[0];
        return (T) constructor.newInstance(target);
    }
}
