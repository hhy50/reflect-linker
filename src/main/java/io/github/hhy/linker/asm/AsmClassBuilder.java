package io.github.hhy.linker.asm;

import io.github.hhy.linker.util.ClassUtil;
import lombok.Data;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;


@Data
public class AsmClassBuilder {

    private String className;
    private MethodVisitor staticMethodWriter;
    private ClassWriter classWriter = new ClassWriter(COMPUTE_MAXS|COMPUTE_FRAMES);

    public AsmClassBuilder(int access, String className, String superName, String[] interfaces, String signature) {
        this.classWriter.visit(Opcodes.V1_8, access, ClassUtil.className2path(className), signature,
                superName != null ? ClassUtil.className2path(superName) : "java/lang/Object", Arrays.stream(interfaces).map(ClassUtil::className2path).toArray(String[]::new));
        this.className = className;
    }

    public AsmClassBuilder defineField(int access, String fieldName, String fieldDesc, String fieldSignature, Object value) {
        this.classWriter.visitField(access, fieldName, fieldDesc, fieldSignature, value);
        return this;
    }

    public MethodBuilder defineConstruct(int access, String[] argsType, String[] exceptions, String sign) {
        MethodVisitor methodVisitor = this.classWriter.visitMethod(access, "<init>", "("+toDesc(argsType)+")V", sign, exceptions);
        return new MethodBuilder(this, methodVisitor);
    }

    public MethodBuilder defineMethod(int access, String methodName, String methodDesc, String[] exceptions, String methodSign) {
        MethodVisitor methodVisitor = this.classWriter.visitMethod(access, methodName, methodDesc, methodSign, exceptions);
        return new MethodBuilder(this, methodVisitor);
    }

    private static String toDesc(String[] types) {
        String typeDesc = "";
        if (types != null && types.length > 0) {
            typeDesc = Arrays.stream(types).map(AsmUtil::toTypeDesc).collect(Collectors.joining());
        }
        return typeDesc;
    }

    public AsmClassBuilder end() {
        this.classWriter.visitEnd();
        if (this.staticMethodWriter != null) {
            this.staticMethodWriter.visitInsn(Opcodes.RETURN);
            this.staticMethodWriter.visitMaxs(0, 0);
        }
        return this;
    }

    public byte[] toBytecode() {
        return classWriter.toByteArray();
    }

    public synchronized void writeClint(Consumer<MethodVisitor> interceptor) {
        if (staticMethodWriter == null) {
            staticMethodWriter = this.defineMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null)
                    .getMethodVisitor();
        }
        interceptor.accept(staticMethodWriter);
    }
}
