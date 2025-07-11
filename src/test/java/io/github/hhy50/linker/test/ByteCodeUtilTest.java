package io.github.hhy50.linker.test;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.util.ReflectUtil;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.lang.reflect.Method;

import static io.github.hhy50.linker.ext.InjectUtil.*;

public class ByteCodeUtilTest {

    static final Type[] TYPES = new Type[]{
            Type.BYTE_TYPE,
            Type.SHORT_TYPE,
            Type.INT_TYPE,
            Type.LONG_TYPE,
            Type.FLOAT_TYPE,
            Type.DOUBLE_TYPE,
            Type.CHAR_TYPE,
            Type.BOOLEAN_TYPE,
            ObjectVar.TYPE,
    };


    @Test
    public void testGetter() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        String className = "io.github.hhy50.linker.test.accessor.ByteCodeUtilTest$TestGetter";
        AsmClassBuilder classBuilder = new AsmClassBuilder(Opcodes.ACC_PUBLIC, className,
                "java/lang/Object", null, null);
        for (char i = 'a'; i <= 'z'; i++) {
            Type t = TYPES[i%TYPES.length];
            String filedName = "field_"+i;
            classBuilder.defineField(Opcodes.ACC_PUBLIC, filedName, t, null, null);
            injectGetter(classBuilder, filedName, "get"+filedName);
        }
        injectConstruct(classBuilder);
        classBuilder.end();

        byte[] bytecode = classBuilder.toBytecode();
        CustomClassLoader customClassLoader = new CustomClassLoader(className, bytecode);
        Class<?> clazz = customClassLoader.loadClass(className);

        Object o = clazz.newInstance();
        Assert.assertNotNull(o);
        for (char i = 'a'; i <= 'z'; i++) {
            String filedName = "field_"+i;
            Method method = ReflectUtil.getMethod(clazz, "get"+filedName);
            Assert.assertNotNull(method);
        }
    }


    @Test
    public void testSetter() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, NoSuchMethodException {
        String className = "io.github.hhy50.linker.test.accessor.ByteCodeUtilTest$TestSetter";
        AsmClassBuilder classBuilder = new AsmClassBuilder(Opcodes.ACC_PUBLIC, className,
                "java/lang/Object", null, null);
        for (char i = 'a'; i <= 'z'; i++) {
            Type t = TYPES[i%TYPES.length];
            String filedName = "field_"+i;
            classBuilder.defineField(Opcodes.ACC_PUBLIC, filedName, t, null, null);
            injectSetter(classBuilder, filedName, "set"+filedName);
        }
        injectConstruct(classBuilder);
        classBuilder.end();

        byte[] bytecode = classBuilder.toBytecode();
        CustomClassLoader customClassLoader = new CustomClassLoader(className, bytecode);
        Class<?> clazz = customClassLoader.loadClass(className);

        Object o = clazz.newInstance();
        Assert.assertNotNull(o);
        for (char i = 'a'; i <= 'z'; i++) {
            String filedName = "field_"+i;
            Method method = ReflectUtil.getMethod(clazz, "set"+filedName);
            Assert.assertNotNull(method);
        }
    }

    static class CustomClassLoader extends ClassLoader {
        private final String className;
        private final byte[] bytecode;

        public CustomClassLoader(String className, byte[] bytecode) {
            this.className = className;
            this.bytecode = bytecode;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.equalsIgnoreCase( className)) {
                return this.defineClass(className, bytecode, 0, bytecode.length);
            }
            return super.findClass(name);
        }
    }
}
