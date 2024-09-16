package io.github.hhy50.linker.asm;

import io.github.hhy50.linker.util.ClassUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;


/**
 * <p>AsmClassBuilder class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class AsmClassBuilder {

    protected String className;
    protected MethodVisitor clinitMethodWriter;
    protected ClassWriter classWriter = new ClassWriter(COMPUTE_MAXS|COMPUTE_FRAMES);

    /**
     * <p>Constructor for AsmClassBuilder.</p>
     *
     * @param access a int.
     * @param className a {@link java.lang.String} object.
     * @param superName a {@link java.lang.String} object.
     * @param interfaces an array of {@link java.lang.String} objects.
     * @param signature a {@link java.lang.String} object.
     */
    public AsmClassBuilder(int access, String className, String superName, String[] interfaces, String signature) {
        this.classWriter.visit(Opcodes.V1_8, access, ClassUtil.className2path(className), signature,
                superName != null ? ClassUtil.className2path(superName) : "java/lang/Object", Arrays.stream(interfaces).map(ClassUtil::className2path).toArray(String[]::new));
        this.className = className;
    }

    /**
     * <p>defineField.</p>
     *
     * @param access a int.
     * @param fieldName a {@link java.lang.String} object.
     * @param fieldDesc a {@link java.lang.String} object.
     * @param fieldSignature a {@link java.lang.String} object.
     * @param value a {@link java.lang.Object} object.
     * @return a {@link io.github.hhy50.linker.asm.AsmClassBuilder} object.
     */
    public AsmClassBuilder defineField(int access, String fieldName, String fieldDesc, String fieldSignature, Object value) {
        this.classWriter.visitField(access, fieldName, fieldDesc, fieldSignature, value);
        return this;
    }

    /**
     * <p>defineConstruct.</p>
     *
     * @param access a int.
     * @param argsType an array of {@link java.lang.String} objects.
     * @param exceptions an array of {@link java.lang.String} objects.
     * @param sign a {@link java.lang.String} object.
     * @return a {@link io.github.hhy50.linker.asm.MethodBuilder} object.
     */
    public MethodBuilder defineConstruct(int access, String[] argsType, String[] exceptions, String sign) {
        MethodVisitor methodVisitor = this.classWriter.visitMethod(access, "<init>", "("+toDesc(argsType)+")V", sign, exceptions);
        return new MethodBuilder(this, methodVisitor);
    }

    /**
     * <p>defineMethod.</p>
     *
     * @param access a int.
     * @param methodName a {@link java.lang.String} object.
     * @param methodDesc a {@link java.lang.String} object.
     * @param exceptions an array of {@link java.lang.String} objects.
     * @param methodSign a {@link java.lang.String} object.
     * @return a {@link io.github.hhy50.linker.asm.MethodBuilder} object.
     */
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

    /**
     * <p>end.</p>
     *
     * @return a {@link io.github.hhy50.linker.asm.AsmClassBuilder} object.
     */
    public AsmClassBuilder end() {
        this.classWriter.visitEnd();
        if (this.clinitMethodWriter != null) {
            this.clinitMethodWriter.visitInsn(Opcodes.RETURN);
            this.clinitMethodWriter.visitMaxs(0, 0);
        }

        return this;
    }

    /**
     * <p>toBytecode.</p>
     *
     * @return an array of {@link byte} objects.
     */
    public byte[] toBytecode() {
        return classWriter.toByteArray();
    }

    /**
     * <p>Getter for the field <code>className</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getClassName() {
        return this.className;
    }
}
