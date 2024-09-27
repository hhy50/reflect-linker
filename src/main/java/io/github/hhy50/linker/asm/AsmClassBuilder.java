package io.github.hhy50.linker.asm;

import io.github.hhy50.linker.generate.bytecode.Member;
import io.github.hhy50.linker.util.ClassUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;


/**
 * The type Asm class builder.
 */
public class AsmClassBuilder {

    /**
     * The Class desc.
     */
    protected String classDesc;
    /**
     * The Class name.
     */
    protected String className;
    /**
     * The Clinit method writer.
     */
    protected MethodVisitor clinitMethodWriter;
    /**
     * The Class writer.
     */
    protected ClassWriter classWriter;

    /**
     * Instantiates a new Asm class builder.
     *
     * @param access     the access
     * @param className  the class name
     * @param superName  the super name
     * @param interfaces the interfaces
     * @param signature  the signature
     */
    public AsmClassBuilder(int access, String className, String superName, String[] interfaces, String signature) {
        this(COMPUTE_MAXS|COMPUTE_FRAMES, access, className, superName, interfaces, signature);
    }

    /**
     * Instantiates a new Asm class builder.
     *
     * @param asmFlags   the asm flags
     * @param access     the access
     * @param className  the class name
     * @param superName  the super name
     * @param interfaces the interfaces
     * @param signature  the signature
     */
    public AsmClassBuilder(int asmFlags, int access, String className, String superName, String[] interfaces, String signature) {
        this.className = className;
        this.classDesc = ClassUtil.className2path(className);
        this.classWriter = new ClassWriter(asmFlags);
        this.classWriter.visit(Opcodes.V1_8, access, this.classDesc, signature,
                superName != null ? ClassUtil.className2path(superName) : "java/lang/Object", Arrays.stream(interfaces).map(ClassUtil::className2path).toArray(String[]::new));
    }

    /**
     * Define field member.
     *
     * @param access         the access
     * @param fieldName      the field name
     * @param fieldType      the field type
     * @param fieldSignature the field signature
     * @param value          the value
     * @return the member
     */
    public Member defineField(int access, String fieldName, Type fieldType, String fieldSignature, Object value) {
        this.classWriter.visitField(access, fieldName, fieldType.getDescriptor(), fieldSignature, value);
        return new Member(access, classDesc, fieldName, fieldType);
    }

    /**
     * Define construct method builder.
     *
     * @param access     the access
     * @param argsType   the args type
     * @param exceptions the exceptions
     * @param sign       the sign
     * @return the method builder
     */
    public MethodBuilder defineConstruct(int access, String[] argsType, String[] exceptions, String sign) {
        MethodVisitor methodVisitor = this.classWriter.visitMethod(access, "<init>", "("+toDesc(argsType)+")V", sign, exceptions);
        return new MethodBuilder(this, methodVisitor, "("+toDesc(argsType)+")V");
    }

    /**
     * Define method method builder.
     *
     * @param access     the access
     * @param methodName the method name
     * @param methodDesc the method desc
     * @param exceptions the exceptions
     * @return the method builder
     */
    public MethodBuilder defineMethod(int access, String methodName, String methodDesc, String[] exceptions) {
        MethodVisitor methodVisitor = this.classWriter.visitMethod(access, methodName, methodDesc, null, exceptions);
        return new MethodBuilder(this, methodVisitor, methodDesc);
    }

    private static String toDesc(String[] types) {
        String typeDesc = "";
        if (types != null && types.length > 0) {
            typeDesc = Arrays.stream(types).map(AsmUtil::toTypeDesc).collect(Collectors.joining());
        }
        return typeDesc;
    }

    /**
     * End asm class builder.
     *
     * @return the asm class builder
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
     * To bytecode byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] toBytecode() {
        return classWriter.toByteArray();
    }

    /**
     * Gets class name.
     *
     * @return the class name
     */
    public String getClassName() {
        return this.className;
    }
}
