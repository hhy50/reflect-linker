package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.GeneratedClass;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.md.AbsInterfaceMetadata;
import io.github.hhy50.linker.define.md.AbsMethodMetadata;
import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.getter.TargetFieldGetter;
import io.github.hhy50.linker.util.ClassUtil;
import io.github.hhy50.linker.util.StringUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * The type Class impl generator.
 */
public class ClassImplGenerator {
    /**
     * Generate bytecode.
     *
     * @throws IOException the io exception
     */
    public static GeneratedClass generateBytecode(AbsInterfaceMetadata classMetadata, String implClassName, List<MethodExprRef> methodExprs, List<Class<?>> interfaces) throws IOException {
        EarlyFieldRef targetField = new EarlyFieldRef("target", classMetadata.getTargetClass());
        TargetFieldGetter targetGetter = new TargetFieldGetter(targetField, classMetadata);

        InvokeClassImplBuilder classBuilder = InvokeClassImplBuilder
                .builder(Opcodes.ACC_PUBLIC | Opcodes.ACC_OPEN, implClassName, DefaultTargetProviderImpl.class.getName(), interfaces.stream()
                        .map(Class::getName).toArray(String[]::new), "")
                .setTarget(targetGetter);

        for (MethodExprRef methodExpr : methodExprs) {
            AbsMethodMetadata metadata = methodExpr.getMetadata();
            classBuilder.defineMethod(Opcodes.ACC_PUBLIC, metadata.getName(), Type.getType(metadata.getReflect()), null)
                    .intercept(generateMethodImpl(classBuilder, methodExpr));
        }

        byte[] bytecode = classBuilder.end().toBytecode();
        String outputPath = System.getProperty("linker.output.path");
        if (!StringUtil.isEmpty(outputPath)) {
            Files.write(new File(outputPath, ClassUtil.toSimpleName(implClassName) + ".class").toPath(), bytecode);
        }
        return new GeneratedClass(implClassName, bytecode);
    }

    private static Action generateMethodImpl(InvokeClassImplBuilder classBuilder, MethodExprRef methodExpr) {
        MethodHandle mh = BytecodeFactory.generateInvoker(methodExpr);
        if (mh == null) {
            return Actions.withVisitor(mv -> AsmUtil.throwNoSuchMethod(mv, methodExpr.getMetadata().getReflect().getName()));
        } else {
            mh.define(classBuilder);
            return mh.invoke(ChainAction.of(MethodBody::getArgs));
        }
    }
}
