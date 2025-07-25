package io.github.hhy50.linker.asm;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.asm.tree.LClassNode;
import io.github.hhy50.linker.asm.tree.LFieldNode;
import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.exceptions.LinkerException;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.util.ClassUtil;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.*;

import java.util.*;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;


/**
 * The type Asm class builder.
 */
public class AsmClassBuilder {
    /**
     * The constant AUTO_COMPUTE.
     */
    protected static final int AUTO_COMPUTE = COMPUTE_MAXS | COMPUTE_FRAMES;
    /**
     * The Flags.
     */
    protected int flags;
    /**
     * The Access.
     */
    protected int access;
    /**
     * The Class name.
     */
    protected String className;
    /**
     * The Class owner.
     */
    protected String classOwner;
    /**
     * The Super owner.
     */
    protected String superOwner;
    /**
     * The Clinit.
     */
    protected MethodBody clinit;
    /**
     * The Class writer.
     */
    protected ClassVisitor classWriter;
    /**
     * The Members.
     */
    protected List<AsmField> fields;

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
        this.flags = asmFlags;
        this.access = access;
        this.className = className;
        this.classOwner = ClassUtil.className2path(className);
        this.superOwner = Optional.ofNullable(superName).map(ClassUtil::className2path).orElse("java/lang/Object");
        this.fields = new ArrayList<>();
        this.classWriter = new ClassWriter(asmFlags);
        this.classWriter.visit(Opcodes.V1_8, access, this.classOwner, signature, this.superOwner,
                Arrays.stream(interfaces == null ? new String[0] : interfaces).map(ClassUtil::className2path).toArray(String[]::new));
    }

    /**
     * Define field member.
     *
     * @param access the access
     * @param name   the name
     * @param type   the type
     * @return the member
     */
    public AsmField defineField(int access, String name, Class<?> type) {
        return defineField(access, name, Type.getType(type), null, null);
    }

    /**
     * Define field member.
     *
     * @param access    the access
     * @param name      the name
     * @param type      the type
     * @param signature the signature
     * @param value     the value
     * @return the member
     */
    public AsmField defineField(int access, String name, Type type, String signature, Object value) {
        FieldVisitor visitor = this.classWriter.visitField(access, name, type.getDescriptor(), signature, value);
        AsmField field = new AsmField(access, classOwner, name, type, visitor);
        this.fields.add(field);
        return field;
    }

    /**
     * Define construct method builder.
     *
     * @param access the access
     * @return the method builder
     */
    public MethodBuilder defineConstruct(int access) {
        return defineMethod(access, "<init>", Type.getMethodType(Type.VOID_TYPE), null);
    }

    /**
     * Define construct method builder.
     *
     * @param access   the access
     * @param argsType the args type
     * @return the method builder
     */
    public MethodBuilder defineConstruct(int access, Class<?>... argsType) {
        return defineMethod(access, "<init>", TypeUtil.getMethodType(void.class, argsType), null);
    }

    /**
     * Define construct method builder.
     *
     * @param access   the access
     * @param argsType the args type
     * @return the method builder
     */
    public MethodBuilder defineConstruct(int access, Type... argsType) {
        return defineMethod(access, "<init>", Type.getMethodType(Type.VOID_TYPE, argsType), null);
    }

    /**
     * Define method method builder.
     *
     * @param access     the access
     * @param name       the name
     * @param mType      the m type
     * @param exceptions the exceptions
     * @return the method builder
     */
    public MethodBuilder defineMethod(int access, String name, Type mType, String[] exceptions) {
        MethodVisitor mv = this.classWriter.visitMethod(access, name, mType.getDescriptor(), null, exceptions);
        return new MethodBuilder(this, access, MethodDescriptor.of(classOwner, name, mType), mv);
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
     * Gets clinit.
     *
     * @return the clinit
     */
    public MethodBody getClinit() {
        if (this.clinit == null) {
            MethodBuilder methodBuilder = this.defineMethod(Opcodes.ACC_STATIC, "<clinit>", Type.getMethodType(Type.VOID_TYPE), null);
            this.clinit = methodBuilder.getMethodBody();
        }
        return this.clinit;
    }

    /**
     * Get field member.
     * @param name
     * @return
     */
    public AsmField getField(String name) {
        return fields.stream().filter(f -> f.name.equals(name)).findFirst().orElse(null);
    }

    /**
     *
     * @return
     */
    public List<AsmField> getFields() {
        return fields;
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
        } else if (classWriter instanceof ClassVisitorWrap) {
            ClassWriter writer = new ClassWriter(this.flags);
            ((ClassVisitorWrap) classWriter).accept(writer);
            return writer.toByteArray();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Gets access.
     *
     * @return the access
     */
    public int getAccess() {
        return this.access;
    }

    /**
     * Gets class owner.
     *
     * @return the class owner
     */
    public String getClassOwner() {
        return this.classOwner;
    }

    /**
     * Gets class writer.
     *
     * @return the class writer
     */
    public ClassVisitor getClassWriter() {
        return this.classWriter;
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
     * Is auto compute boolean.
     *
     * @return the boolean
     */
    public boolean isAutoCompute() {
        return (this.flags & AUTO_COMPUTE) == AUTO_COMPUTE;
    }

    /**
     * Wrap asm class builder.
     *
     * @param classVisitor the class visitor
     * @return the asm class builder
     * @throws LinkerException the linker exception
     */
    public static AsmClassBuilder wrap(LClassNode classVisitor) throws LinkerException {
        AsmClassBuilder classBuilder = new AsmClassBuilder();
        classBuilder.access = classVisitor.getAccess();
        classBuilder.flags = AUTO_COMPUTE;
        classBuilder.classOwner = classVisitor.getName();
        classBuilder.className = ClassUtil.classpath2name(classVisitor.getName());
        classBuilder.superOwner = classVisitor.getSuperName();
        classBuilder.fields = new ArrayList<>();

        for (Object fieldO : classVisitor.getFields()) {
            LFieldNode field = LinkerFactory.createLinker(LFieldNode.class, fieldO);
            classBuilder.fields.add(new AsmField(field.getAccess(), classVisitor.getName(),
                    field.getName(), Type.getType(field.getDesc())));
        }

        classBuilder.classWriter = new ClassVisitorWrap(classVisitor);
        return classBuilder;
    }
}
