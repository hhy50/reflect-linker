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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>InvokeClassImplBuilder class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class InvokeClassImplBuilder extends AsmClassBuilder {
    private Class<?> defineClass;
    private Class<?> bindTarget;
    private final String implClassDesc;
    private MethodBody clinit;
    private final Map<String, Getter<?>> getters;
    private final Map<String, Setter<?>> setters;
    private final Map<String, Invoker<?>> invokers;
    private final Map<String, Member> members;

    /**
     * <p>Constructor for InvokeClassImplBuilder.</p>
     *
     * @param access     a int.
     * @param className  a {@link java.lang.String} object.
     * @param superName  a {@link java.lang.String} object.
     * @param interfaces an array of {@link java.lang.String} objects.
     * @param signature  a {@link java.lang.String} object.
     */
    public InvokeClassImplBuilder(int access, String className, String superName, String[] interfaces, String signature) {
        super(access, className, superName, interfaces, signature);
        this.implClassDesc = ClassUtil.className2path(this.getClassName());
        this.getters = new HashMap<>();
        this.setters = new HashMap<>();
        this.invokers = new HashMap<>();
        this.members = new HashMap<>();
    }

    /**
     * <p>init.</p>
     *
     * @return a {@link io.github.hhy50.linker.generate.InvokeClassImplBuilder} object.
     */
    public InvokeClassImplBuilder init() {
        EarlyFieldRef target = new EarlyFieldRef(null, null, "target", bindTarget);
        TargetFieldGetter targetFieldGetter = new TargetFieldGetter(getClassName(), defineClass, target);

        this.getters.put(target.getUniqueName(), targetFieldGetter);
        return this;
    }

    /**
     * <p>setDefine.</p>
     *
     * @param define a {@link java.lang.Class} object.
     * @return a {@link io.github.hhy50.linker.generate.InvokeClassImplBuilder} object.
     */
    public InvokeClassImplBuilder setDefine(Class<?> define) {
        this.defineClass = define;
        return this;
    }

    /**
     * <p>setTarget.</p>
     *
     * @param bindTarget a {@link java.lang.Class} object.
     * @return a {@link io.github.hhy50.linker.generate.InvokeClassImplBuilder} object.
     */
    public InvokeClassImplBuilder setTarget(Class<?> bindTarget) {
        this.bindTarget = bindTarget;
        String argsType = Modifier.isPublic(bindTarget.getModifiers()) ? bindTarget.getName() : "java.lang.Object";
        this.defineConstruct(Opcodes.ACC_PUBLIC, new String[]{argsType}, null, "")
                .accept(mv -> {
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.ALOAD, 1);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, ClassUtil.className2path(DefaultTargetProviderImpl.class.getName()), "<init>", "(Ljava/lang/Object;)V", false);
                    mv.visitInsn(Opcodes.RETURN);
                });
        return this;
    }

    /**
     * 定义Getter
     *
     * @param fieldName a {@link java.lang.String} object.
     * @param fieldRef  a {@link io.github.hhy50.linker.define.field.FieldRef} object.
     * @return a {@link io.github.hhy50.linker.generate.getter.Getter} object.
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
     * 获取getter
     *
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link io.github.hhy50.linker.generate.getter.Getter} object.
     */
    public Getter<?> getGetter(String fieldName) {
        return getters.get(fieldName);
    }

    /**
     * <p>defineSetter.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @param fieldRef  a {@link io.github.hhy50.linker.define.field.FieldRef} object.
     * @return a {@link io.github.hhy50.linker.generate.setter.Setter} object.
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
     * <p>defineInvoker.</p>
     *
     * @param methodRef a {@link io.github.hhy50.linker.define.method.MethodRef} object.
     * @return a {@link io.github.hhy50.linker.generate.invoker.Invoker} object.
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
     * 定义静态的methodHandle
     *
     * @param mhMemberName a {@link java.lang.String} object.
     * @param methodType   a {@link org.objectweb.asm.Type} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.MethodHandleMember} object.
     */
    public MethodHandleMember defineStaticMethodHandle(String mhMemberName, Type methodType) {
        if (!members.containsKey(mhMemberName)) {
            int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
            super.defineField(access, mhMemberName, MethodHandle.DESCRIPTOR, null, null);
            this.members.put(mhMemberName, new MethodHandleMember(access, implClassDesc, mhMemberName, methodType));
        }
        return (MethodHandleMember) members.get(mhMemberName);
    }

    /**
     * <p>defineMethodHandle.</p>
     *
     * @param mhMemberName a {@link java.lang.String} object.
     * @param methodType   a {@link org.objectweb.asm.Type} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.MethodHandleMember} object.
     */
    public MethodHandleMember defineMethodHandle(String mhMemberName, Type methodType) {
        if (!members.containsKey(mhMemberName)) {
            super.defineField(Opcodes.ACC_PUBLIC, mhMemberName, MethodHandle.DESCRIPTOR, null, null);
            this.members.put(mhMemberName, new MethodHandleMember(Opcodes.ACC_PUBLIC, implClassDesc, mhMemberName, methodType));
        }
        return (MethodHandleMember) members.get(mhMemberName);
    }

    /**
     * <p>defineClassTypeMember.</p>
     *
     * @return a {@link io.github.hhy50.linker.generate.bytecode.ClassTypeMember} object.
     * @param mName a {@link java.lang.String} object.
     */
    public ClassTypeMember defineLookupClass(String mName) {
        return defineLookupClass(Opcodes.ACC_PUBLIC|Opcodes.ACC_STATIC|Opcodes.ACC_FINAL, mName);
    }

    /**
     * <p>defineClassTypeMember.</p>
     *
     * @param access a int.
     * @param mName a {@link java.lang.String} object.
     * @return a {@link io.github.hhy50.linker.generate.bytecode.ClassTypeMember} object.
     */
    public ClassTypeMember defineLookupClass(int access, String mName) {
        mName = mName+"_lookup_$_class_type";
        if (!members.containsKey(mName)) {
            super.defineField(access, mName, ClassVar.TYPE.getDescriptor(), null, null);

            ClassTypeMember classTypeMember = new ClassTypeMember(access, implClassDesc, mName);
            this.members.put(mName, classTypeMember);
            return classTypeMember;
        }
        return (ClassTypeMember) members.get(mName);
    }

    /**
     * <p>Getter for the field <code>clinit</code>.</p>
     *
     * @return a {@link io.github.hhy50.linker.generate.MethodBody} object.
     */
    public MethodBody getClinit() {
        if (clinitMethodWriter == null) {
            clinitMethodWriter = this.defineMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null)
                    .getMethodVisitor();
        }
        if (clinit == null) {
            clinit = new MethodBody(this, clinitMethodWriter, Type.getMethodType(Type.VOID_TYPE), true);
        }
        return clinit;
    }
}
