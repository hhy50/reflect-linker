package io.github.hhy50.linker.test.v2.bytecode;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy50.linker.generate.bytecode.action.NewObjectAction;
import io.github.hhy50.linker.generate.bytecode.vars.LocalVarInst;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import java.lang.reflect.Method;
import java.util.List;

import static io.github.hhy50.linker.generate.bytecode.action.Actions._try;

public class TrycatchActionTest {

    private static final String GENERATED_CLASS_NAME = "io.github.hhy50.linker.test.v2.bytecode.generated.TrycatchActionGenerated";
    private static final Type STRING_TYPE = Type.getType(String.class);
    private static final Type RUNTIME_EXCEPTION_TYPE = Type.getType(RuntimeException.class);

    @Test
    public void shouldSkipCatchBlockWhenTryCompletesNormally() throws Exception {
        Class<?> generatedClass = loadGeneratedClass();

        Assert.assertEquals("try", invokeStatic(generatedClass, "normalPath"));
    }

    @Test
    public void shouldRunCatchBlockWhenTryThrows() throws Exception {
        Class<?> generatedClass = loadGeneratedClass();

        Assert.assertEquals("catch", invokeStatic(generatedClass, "exceptionPath"));
    }

    @Test
    public void shouldEmitTryCatchHandlerThatStoresCaughtException() throws Exception {
        MethodNode methodNode = getMethodNode("exceptionPath");

        List<TryCatchBlockNode> tryCatchBlocks = methodNode.tryCatchBlocks;
        Assert.assertEquals(1, tryCatchBlocks.size());

        TryCatchBlockNode tryCatchBlock = tryCatchBlocks.get(0);
        Assert.assertEquals(Type.getInternalName(RuntimeException.class), tryCatchBlock.type);

        AbstractInsnNode firstInsnInHandler = firstRealInstructionAfter(tryCatchBlock.handler);
        Assert.assertNotNull(firstInsnInHandler);
        Assert.assertEquals(Opcodes.ASTORE, firstInsnInHandler.getOpcode());
    }

    private static Object invokeStatic(Class<?> generatedClass, String methodName) throws Exception {
        Method method = generatedClass.getMethod(methodName);
        return method.invoke(null);
    }

    private static Class<?> loadGeneratedClass() throws Exception {
        byte[] bytecode = buildBytecode();
        return new ByteArrayClassLoader(GENERATED_CLASS_NAME, bytecode).loadClass(GENERATED_CLASS_NAME);
    }

    private static MethodNode getMethodNode(String methodName) throws Exception {
        ClassNode classNode = new ClassNode();
        new ClassReader(buildBytecode()).accept(classNode, 0);
        for (MethodNode method : classNode.methods) {
            if (method.name.equals(methodName)) {
                return method;
            }
        }
        throw new IllegalStateException("method not found: " + methodName);
    }

    private static AbstractInsnNode firstRealInstructionAfter(AbstractInsnNode startNode) {
        for (AbstractInsnNode insn = startNode.getNext(); insn != null; insn = insn.getNext()) {
            if (insn.getOpcode() >= 0) {
                return insn;
            }
        }
        return null;
    }

    private static byte[] buildBytecode() {
        AsmClassBuilder classBuilder = new AsmClassBuilder(
                Opcodes.ACC_PUBLIC,
                GENERATED_CLASS_NAME,
                Object.class.getName(),
                null,
                null
        );

        defineNormalPathMethod(classBuilder);
        defineExceptionPathMethod(classBuilder);

        classBuilder.end();
        return classBuilder.toBytecode();
    }

    private static void defineNormalPathMethod(AsmClassBuilder classBuilder) {
        classBuilder.defineMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "normalPath",
                Type.getMethodType(STRING_TYPE),
                null
        ).accept(body -> {
            LocalVarInst result = body.newLocalVar(STRING_TYPE, "result", Actions.loadNull());
            body.append(_try(result.store(LdcLoadAction.of("try")))
                    ._catch(RUNTIME_EXCEPTION_TYPE,
                            e -> result.store(LdcLoadAction.of("catch"))));
            body.append(result.thenReturn());
        }).end();
    }

    private static void defineExceptionPathMethod(AsmClassBuilder classBuilder) {
        classBuilder.defineMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "exceptionPath",
                Type.getMethodType(STRING_TYPE),
                null
        ).accept(body -> {
            LocalVarInst result = body.newLocalVar(STRING_TYPE, "result", Actions.loadNull());
            body.append(_try(throwException("boom"))
                    ._catch(RUNTIME_EXCEPTION_TYPE, e -> result.store(LdcLoadAction.of("catch"))));
            body.append(result.thenReturn());
        }).end();
    }

    private static Action throwException(String message) {
        return Actions.of(
                new NewObjectAction(RUNTIME_EXCEPTION_TYPE, STRING_TYPE).setArgs(LdcLoadAction.of(message)),
                Actions.withVisitor(mv -> mv.visitInsn(Opcodes.ATHROW))
        );
    }

    private static class ByteArrayClassLoader extends ClassLoader {
        private final String className;
        private final byte[] bytecode;

        private ByteArrayClassLoader(String className, byte[] bytecode) {
            this.className = className;
            this.bytecode = bytecode;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (className.equals(name)) {
                return defineClass(name, bytecode, 0, bytecode.length);
            }
            return super.findClass(name);
        }
    }
}
