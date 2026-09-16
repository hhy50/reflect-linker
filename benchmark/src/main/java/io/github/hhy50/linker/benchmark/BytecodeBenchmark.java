package io.github.hhy50.linker.benchmark;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.utils.Members;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FieldAccessor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 字节码生成性能基准测试
 *
 * 对比四个库生成相同 POJO 类（2字段 + getter/setter + 构造器）的吞吐量：
 *   1. reflect-linker (AsmClassBuilder API)
 *   2. Raw ASM (ClassWriter 直接操作)
 *   3. ByteBuddy (1.14.18)
 *   4. Javassist (3.30.2)
 *
 * 运行方式：
 *   cd benchmark && mvn clean package && java -jar target/benchmarks.jar
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 3)
@Fork(1)
public class BytecodeBenchmark {

    /** 用于生成唯一类名，避免各库内部缓存影响测试结果 */
    private int id;

    @Setup(Level.Iteration)
    public void setup() {
        id = 0;
    }

    private int nextId() {
        return id++;
    }

    // ======================== reflect-linker ========================
    @Benchmark
    public byte[] reflectLinker() {
        String className = "benchmark/ReflectLinkerPerson" + nextId();
        AsmClassBuilder builder = new AsmClassBuilder(
                Opcodes.ACC_PUBLIC, className, null, null, null);

        builder.defineField(Opcodes.ACC_PRIVATE, "name", String.class);
        builder.defineField(Opcodes.ACC_PRIVATE, "age",  int.class);

        builder.defineConstruct(Opcodes.ACC_PUBLIC)
                .intercept(Methods.invokeSuper().thenReturn());

        builder.defineMethod(Opcodes.ACC_PUBLIC, "getName",
                        Type.getMethodType(Type.getType(String.class)), null)
                .intercept(Members.ofLoad("name").thenReturn());

        builder.defineMethod(Opcodes.ACC_PUBLIC, "setName",
                        Type.getMethodType(Type.VOID_TYPE, Type.getType(String.class)), null)
                .intercept(Members.ofStore("name", Args.of(0)), Actions.vreturn());

        builder.defineMethod(Opcodes.ACC_PUBLIC, "getAge",
                        Type.getMethodType(Type.INT_TYPE), null)
                .intercept(Members.ofLoad("age").thenReturn());

        builder.defineMethod(Opcodes.ACC_PUBLIC, "setAge",
                        Type.getMethodType(Type.VOID_TYPE, Type.INT_TYPE), null)
                .intercept(Members.ofStore("age", Args.of(0)), Actions.vreturn());

        return builder.toBytecode();
    }

    // ======================== Raw ASM ========================

    @Benchmark
    public byte[] rawAsm() {
        String owner = "benchmark/AsmPerson" + nextId();
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, owner, null, "java/lang/Object", null);

        // 字段
        cw.visitField(Opcodes.ACC_PRIVATE, "name", "Ljava/lang/String;", null, null);
        cw.visitField(Opcodes.ACC_PRIVATE, "age",  "I",                null, null);

        // 无参构造器
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // getName
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getName", "()Ljava/lang/String;", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, owner, "name", "Ljava/lang/String;");
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // setName
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "setName", "(Ljava/lang/String;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, owner, "name", "Ljava/lang/String;");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

        // getAge
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "getAge", "()I", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, owner, "age", "I");
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // setAge
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "setAge", "(I)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ILOAD, 1);
        mv.visitFieldInsn(Opcodes.PUTFIELD, owner, "age", "I");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();

        cw.visitEnd();
        return cw.toByteArray();
    }

    // ======================== ByteBuddy ========================

    @Benchmark
    public byte[] byteBuddy() {
        String className = "benchmark.ByteBuddyPerson" + nextId();
        return new ByteBuddy()
                .subclass(Object.class)
                .name(className)
                .defineField("name", String.class, Opcodes.ACC_PRIVATE)
                .defineField("age",  int.class,    Opcodes.ACC_PRIVATE)
                // getName
                .defineMethod("getName", String.class, Opcodes.ACC_PUBLIC)
                    .intercept(FieldAccessor.ofField("name"))
                // setName
                .defineMethod("setName", void.class, Opcodes.ACC_PUBLIC)
                    .withParameters(String.class)
                    .intercept(FieldAccessor.ofField("name"))
                // getAge
                .defineMethod("getAge", int.class, Opcodes.ACC_PUBLIC)
                    .intercept(FieldAccessor.ofField("age"))
                // setAge
                .defineMethod("setAge", void.class, Opcodes.ACC_PUBLIC)
                    .withParameters(int.class)
                    .intercept(FieldAccessor.ofField("age"))
                .make()
                .getBytes();
    }

    // ======================== Javassist ========================

    @Benchmark
    public byte[] javassist() throws Exception {
        String className = "benchmark.JavassistPerson" + nextId();
        CtClass cc = ClassPool.getDefault().makeClass(className);

        // 字段
        cc.addField(new CtField(ClassPool.getDefault().get("java.lang.String"), "name", cc));
        CtField ageField = new CtField(CtClass.intType, "age", cc);
        cc.addField(ageField);

        // getName / setName
        cc.addMethod(CtNewMethod.getter("getName", cc.getField("name")));
        cc.addMethod(CtNewMethod.setter("setName", cc.getField("name")));

        // getAge / setAge
        cc.addMethod(CtNewMethod.getter("getAge", ageField));
        cc.addMethod(CtNewMethod.setter("setAge", ageField));

        byte[] bytes = cc.toBytecode();
        cc.detach();
        return bytes;
    }

    // ======================== Runner ========================

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(BytecodeBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}