package io.github.hhy50.linker.asm;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.exceptions.ClassBuildException;
import io.github.hhy50.linker.exceptions.LinkerException;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.Member;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.util.ClassUtil;
import io.github.hhy50.linker.util.TypeUtils;
import org.objectweb.asm.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;


/**
 * The type Asm class builder.
 */
public class AsmClassBuilder {
    /**
     * The Auto compute.
     */
    protected static final int AUTO_COMPUTE = COMPUTE_MAXS | COMPUTE_FRAMES;
    /**
     * The Class name.
     */
    protected String className;
    /**
     * The Class desc.
     */
    protected String classOwner;
    /**
     * The Super desc.
     */
    protected String superOwner;
    /**
     * The Clinit method body.
     */
    protected MethodBody clinit;
    /**
     * The Class writer.
     */
    protected ClassVisitor classWriter;
    /**
     * The Members.
     */
    protected Map<String, Member> members;

    /**
     * Instantiates a new Asm class builder.
     */
    protected AsmClassBuilder() {

    }

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
        this(AUTO_COMPUTE, access, className, superName, interfaces, signature);
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
        this.classOwner = ClassUtil.className2path(className);
        this.superOwner = Optional.ofNullable(superName).map(ClassUtil::className2path).orElse("java/lang/Object");
        this.members = new java.util.HashMap<>();
        this.classWriter = new ClassWriter(asmFlags);
        this.classWriter.visit(Opcodes.V1_8, access, this.classOwner, signature, this.superOwner,
                Arrays.stream(interfaces == null ? new String[0] : interfaces).map(ClassUtil::className2path).toArray(String[]::new));
    }

    /**
     * Define field member.
     *
     * @param access    the access
     * @param fieldName the field name
     * @param fieldType the field type
     * @return member member
     */
    public Member defineField(int access, String fieldName, Class<?> fieldType) {
        return defineField(access, fieldName, Type.getType(fieldType), null, null);
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
        return members.compute(fieldName, (k, v) -> {
            if (v != null) {
                throw new ClassBuildException("Repeatedly defining fields with the same name");
            }
            this.classWriter.visitField(access, fieldName, fieldType.getDescriptor(), fieldSignature, value);
            return new Member(access, classOwner, fieldName, fieldType);
        });
    }

    /**
     * Define construct method builder.
     *
     * @param access   the access
     * @param argsType the args type
     * @return the method builder
     */
    public MethodBuilder defineConstruct(int access, Class<?>... argsType) {
        return defineMethod(access, "<init>", TypeUtils.getMethodType(void.class, argsType), null);
    }

    /**
     * Define method method builder.
     *
     * @param access     the access
     * @param methodName the method name
     * @param mType      the method desc
     * @param exceptions the exceptions
     * @return the method builder
     */
    public MethodBuilder defineMethod(int access, String methodName, Type mType, String[] exceptions) {
        MethodVisitor mv = this.classWriter.visitMethod(access, methodName, mType.getDescriptor(), null, exceptions);
        return new MethodBuilder(this, access, MethodDescriptor.of(classOwner, methodName, mType), mv);
    }

    /**
     * Add annotation asm class builder.
     *
     * @param descriptor the descriptor
     * @param props      the props
     * @return the asm class builder
     */
    public AsmClassBuilder addAnnotation(String descriptor, Map<String, Object> props) {
        AnnotationVisitor annotationVisitor = this.classWriter.visitAnnotation(descriptor, true);
        if (props != null && !props.isEmpty()) {
            for (Map.Entry<String, Object> kv : props.entrySet()) {
                annotationVisitor.visit(kv.getKey(), kv.getValue());
            }
        }
        annotationVisitor.visitEnd();
        return this;
    }

    /**
     * Get clinit method body.
     *
     * @return clinit clinit
     */
    public MethodBody getClinit() {
        if (this.clinit == null) {
            MethodBuilder methodBuilder = this.defineMethod(Opcodes.ACC_STATIC, "<clinit>", Type.getMethodType(Type.getType(void.class)), null);
            this.clinit = methodBuilder.getMethodBody();
        }
        return this.clinit;
    }

    /**
     * End asm class builder.
     *
     * @return the asm class builder
     */
    public AsmClassBuilder end() {
        if (clinit != null) {
            clinit.append(Actions.vreturn());
            clinit.end();
        }
        this.classWriter.visitEnd();
        return this;
    }

    /**
     * To bytecode byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] toBytecode() {
        if (classWriter instanceof ClassWriter) {
            return ((ClassWriter) classWriter).toByteArray();
        } else if (classWriter instanceof ClassNodeWrap) {
            return ((ClassNodeWrap) classWriter).toByteArray();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Gets class desc.
     *
     * @return the class desc
     */
    public String getClassOwner() {
        return this.classOwner;
    }

    /**
     * Gets class name.
     *
     * @return the class name
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * Gets super owner.
     *
     * @return the super owner
     */
    public String getSuperOwner() {
        return superOwner;
    }

    /**
     * Get all members
     *
     * @return members members
     */
    public Map<String, Member> getMembers() {
        return members;
    }

    /**
     * Whether auto stack
     *
     * @return boolean boolean
     */
    public boolean isAutoCompute() {
        //return classWriter.hasFlags(AUTO_COMPUTE);
        return true;
    }



    public static AsmClassBuilder wrap(LClassNode classNode) throws LinkerException {
        AsmClassBuilder classBuilder = new AsmClassBuilder();
        classBuilder.classOwner = classNode.getName();
        classBuilder.className = ClassUtil.classpath2name(classNode.getName());
        classBuilder.superOwner = classNode.getSuperName();
        classBuilder.members = new HashMap<>();

        for (Object fieldO : classNode.getFields()) {
            LFieldNode field = LinkerFactory.createLinker(LFieldNode.class, fieldO);
            classBuilder.members.put(field.getName(), new Member(field.getAccess(), classNode.getName(),
                    field.getName(), Type.getType(field.getDesc())));
        }

        classBuilder.classWriter = new ClassNodeWrap(classNode);
        return classBuilder;
    }
}
