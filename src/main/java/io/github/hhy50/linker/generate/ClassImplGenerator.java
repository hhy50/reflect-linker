package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.define.AbsMethod;
import io.github.hhy50.linker.define.GeneratedClass;
import io.github.hhy50.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy50.linker.util.ClassUtil;
import io.github.hhy50.linker.util.StringUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.List;

/**
 * The type Class impl generator.
 */
public class ClassImplGenerator {
    /**
     * Generate bytecode.
     *
     * @param interfaces  the interfaces
     * @throws IOException the io exception
     */
    public static GeneratedClass generateBytecode(String implClassName, List<AbsMethod> absMethods, List<Class<?>> interfaces) throws IOException {
        InvokeClassImplBuilder classBuilder = InvokeClassImplBuilder
                .builder(Opcodes.ACC_PUBLIC | Opcodes.ACC_OPEN, implClassName, DefaultTargetProviderImpl.class.getName(), interfaces.stream()
                        .map(Class::getName).toArray(String[]::new), "");

        for (AbsMethod absMethod : absMethods) {
            Method reflect = absMethod.getReflect();
            classBuilder.defineMethod(Opcodes.ACC_PUBLIC, reflect.getName(), Type.getType(reflect), null)
                    .intercept(body -> generateMethodImpl(classBuilder, body, absMethod));
        }

        byte[] bytecode = classBuilder.end().toBytecode();
        String outputPath = System.getProperty("linker.output.path");
        if (!StringUtil.isEmpty(outputPath)) {
            Files.write(new File(outputPath, ClassUtil.toSimpleName(implClassName) + ".class").toPath(), bytecode);
        }
        return new GeneratedClass(implClassName, bytecode);
    }

    private static void generateMethodImpl(InvokeClassImplBuilder classBuilder, MethodBody body, AbsMethod absMethod) {
//        MethodHandle mh = absMethod.methodHandle();
//        if (absMethodDefine.methodRef != null) {
//            mh = BytecodeFactory.generateInvoker(classBuilder, absMethodDefine, absMethodDefine.methodRef);
//        } else {
//            AsmUtil.throwNoSuchMethod(body.getWriter(), absMethodDefine.method.getName());
//        }
//        if (mh != null) {
//            mh.define(classBuilder);
//            body.append(mh.invoke(null, ChainAction.of(MethodBody::getArgs)));
//        }
    }
}
