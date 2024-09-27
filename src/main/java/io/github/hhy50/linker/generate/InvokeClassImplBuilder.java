package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.constant.MethodHandle;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.define.method.RuntimeMethodRef;
import io.github.hhy50.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.Member;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.vars.ClassVar;
import io.github.hhy50.linker.generate.getter.EarlyFieldGetter;
import io.github.hhy50.linker.generate.getter.Getter;
import io.github.hhy50.linker.generate.getter.RuntimeFieldGetter;
import io.github.hhy50.linker.generate.getter.TargetFieldGetter;
import io.github.hhy50.linker.generate.invoker.EarlyMethodInvoker;
import io.github.hhy50.linker.generate.invoker.Invoker;
import io.github.hhy50.linker.generate.invoker.RuntimeMethodInvoker;
import io.github.hhy50.linker.generate.setter.EarlyFieldSetter;
import io.github.hhy50.linker.generate.setter.RuntimeFieldSetter;
import io.github.hhy50.linker.generate.setter.Setter;
import io.github.hhy50.linker.util.ClassUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Invoke class impl builder.
 */
public class InvokeClassImplBuilder extends AsmClassBuilder {
    private Class<?> defineClass;
    private Class<?> bindTarget;
    private MethodBody clinit;
    private final Map<String, Getter<?>> getters;
    private final Map<String, Setter<?>> setters;
    private final Map<String, Invoker<?>> invokers;
    private final Map<String, Member> members;

    /**
     * Instantiates a new Invoke class impl builder.
     *
     * @param access     the access
     * @param className  the class name
     * @param superName  the super name
     * @param interfaces the interfaces
     * @param signature  the signature
     */
    public InvokeClassImplBuilder(int access, String className, String superName, String[] interfaces, String signature) {
        super(access, className, superName, interfaces, signature);
        this.getters = new HashMap<>();
        this.setters = new HashMap<>();
        this.invokers = new HashMap<>();
        this.members = new HashMap<>();
    }

    /**
     * Init invoke class impl builder.
     *
     * @return the invoke class impl builder
     */
    public InvokeClassImplBuilder init() {
        EarlyFieldRef target = new EarlyFieldRef(null, null, "target", bindTarget);
        TargetFieldGetter targetFieldGetter = new TargetFieldGetter(getClassName(), target);

        this.getters.put(target.getUniqueName(), targetFieldGetter);
        return this;
    }

    /**
     * Sets define.
     *
     * @param define the define
     * @return the define
     */
    public InvokeClassImplBuilder setDefine(Class<?> define) {
        this.defineClass = define;
        return this;
    }

    /**
     * Sets target.
     *
     * @param bindTarget the bind target
     * @return the target
     */
    public InvokeClassImplBuilder setTarget(Class<?> bindTarget) {
        this.bindTarget = bindTarget;
        String argsType = Modifier.isPublic(bindTarget.getModifiers()) ? bindTarget.getName() : "java.lang.Object";
        this.defineConstruct(Opcodes.ACC_PUBLIC, new String[]{argsType}, null, "")
                .accept(body -> {
                    MethodVisitor mv = body.getWriter();
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.ALOAD, 1);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, ClassUtil.className2path(DefaultTargetProviderImpl.class.getName()), "<init>", "(Ljava/lang/Object;)V", false);
                    mv.visitInsn(Opcodes.RETURN);
                });
        return this;
    }

    /**
     * Define getter getter.
     *
     * @param fieldName the field name
     * @param fieldRef  the field ref
     * @return the getter
     */
    public Getter<?> defineGetter(String fieldName, FieldRef fieldRef) {
        return getters.computeIfAbsent(fieldName, key -> {
            if (fieldRef instanceof EarlyFieldRef) {
                return new EarlyFieldGetter(getClassName(), (EarlyFieldRef) fieldRef);
            } else {
                return new RuntimeFieldGetter(getClassName(), (RuntimeFieldRef) fieldRef);
            }
        });
    }

    /**
     * Gets getter.
     *
     * @param fieldName the field name
     * @return the getter
     */
    public Getter<?> getGetter(String fieldName) {
        return getters.get(fieldName);
    }

    /**
     * Define setter setter.
     *
     * @param fieldName the field name
     * @param fieldRef  the field ref
     * @return the setter
     */
    public Setter<?> defineSetter(String fieldName, FieldRef fieldRef) {
        return setters.computeIfAbsent(fieldName, key -> {
            if (fieldRef instanceof EarlyFieldRef) {
                return new EarlyFieldSetter(getClassName(), (EarlyFieldRef) fieldRef);
            } else {
                return new RuntimeFieldSetter(getClassName(), (RuntimeFieldRef) fieldRef);
            }
        });
    }

    /**
     * Define invoker invoker.
     *
     * @param methodRef the method ref
     * @return the invoker
     */
    public Invoker<?> defineInvoker(MethodRef methodRef) {
        Invoker<?> invoker = invokers.get(methodRef.getFullName());
        if (invoker == null) {
            invoker = methodRef instanceof EarlyMethodRef ? new EarlyMethodInvoker(getClassName(), (EarlyMethodRef) methodRef)
                    : new RuntimeMethodInvoker(getClassName(), (RuntimeMethodRef) methodRef);
            invokers.put(methodRef.getFullName(), invoker);
        }
        return invoker;
    }

    /**
     * Define static method handle method handle member.
     *
     * @param mhMemberName the mh member name
     * @param methodType   the method type
     * @return the method handle member
     */
    public MethodHandleMember defineStaticMethodHandle(String mhMemberName, Type methodType) {
        if (!members.containsKey(mhMemberName)) {
            int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
            Member member = super.defineField(access, mhMemberName, MethodHandle.TYPE, null, null);
            this.members.put(mhMemberName, new MethodHandleMember(member, methodType));
        }
        return (MethodHandleMember) members.get(mhMemberName);
    }

    /**
     * Define method handle method handle member.
     *
     * @param mhMemberName the mh member name
     * @param methodType   the method type
     * @return the method handle member
     */
    public MethodHandleMember defineMethodHandle(String mhMemberName, Type methodType) {
        if (!members.containsKey(mhMemberName)) {
            Member member = super.defineField(Opcodes.ACC_PUBLIC, mhMemberName, MethodHandle.TYPE, null, null);
            this.members.put(mhMemberName, new MethodHandleMember(member, methodType));
        }
        return (MethodHandleMember) members.get(mhMemberName);
    }

    /**
     * Define lookup class class type member.
     *
     * @param mName the m name
     * @return the class type member
     */
    public ClassTypeMember defineLookupClass(String mName) {
        return defineLookupClass(Opcodes.ACC_PUBLIC|Opcodes.ACC_STATIC|Opcodes.ACC_FINAL, mName);
    }

    /**
     * Define lookup class class type member.
     *
     * @param access the access
     * @param mName  the m name
     * @return the class type member
     */
    public ClassTypeMember defineLookupClass(int access, String mName) {
        mName = mName+"_lookup_$_class_type";
        if (!members.containsKey(mName)) {
            Member member = super.defineField(access, mName, ClassVar.TYPE, null, null);
            ClassTypeMember classTypeMember = new ClassTypeMember(member);
            this.members.put(mName, classTypeMember);
            return classTypeMember;
        }
        return (ClassTypeMember) members.get(mName);
    }

    /**
     * Gets clinit.
     *
     * @return the clinit
     */
    public MethodBody getClinit() {
        if (clinitMethodWriter == null) {
            clinitMethodWriter = this.defineMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null)
                    .getMethodVisitor();
        }
        if (clinit == null) {
            clinit = new MethodBody(this, clinitMethodWriter, Type.getMethodType(Type.VOID_TYPE), true);
        }
        return clinit;
    }
}
