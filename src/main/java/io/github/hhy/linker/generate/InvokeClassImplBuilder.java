package io.github.hhy.linker.generate;

import io.github.hhy.linker.asm.AsmClassBuilder;
import io.github.hhy.linker.constant.Lookup;
import io.github.hhy.linker.constant.MethodHandle;
import io.github.hhy.linker.define.field.EarlyFieldRef;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.define.field.RuntimeFieldRef;
import io.github.hhy.linker.define.method.EarlyMethodRef;
import io.github.hhy.linker.define.method.MethodRef;
import io.github.hhy.linker.define.method.RuntimeMethodRef;
import io.github.hhy.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.Member;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.getter.EarlyFieldGetter;
import io.github.hhy.linker.generate.getter.Getter;
import io.github.hhy.linker.generate.getter.RuntimeFieldGetter;
import io.github.hhy.linker.generate.getter.TargetFieldGetter;
import io.github.hhy.linker.generate.invoker.EarlyMethodInvoker;
import io.github.hhy.linker.generate.invoker.Invoker;
import io.github.hhy.linker.generate.invoker.RuntimeMethodInvoker;
import io.github.hhy.linker.generate.setter.EarlyFieldSetter;
import io.github.hhy.linker.generate.setter.RuntimeFieldSetter;
import io.github.hhy.linker.generate.setter.Setter;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class InvokeClassImplBuilder extends AsmClassBuilder {
    private Class<?> bindTarget;
    private final String implClassDesc;
    private MethodBody clinit;
    private final Map<String, Getter<?>> getters;
    private final Map<String, Setter<?>> setters;
    private final Map<String, Invoker<?>> invokers;
    private final Map<String, Member> members;

    public InvokeClassImplBuilder(int access, String className, String superName, String[] interfaces, String signature) {
        super(access, className, superName, interfaces, signature);
        this.implClassDesc = ClassUtil.className2path(this.getClassName());
        this.getters = new HashMap<>();
        this.setters = new HashMap<>();
        this.invokers = new HashMap<>();
        this.members = new HashMap<>();
    }

    public InvokeClassImplBuilder init() {
        EarlyFieldRef target = new EarlyFieldRef(null, null, "target", bindTarget);
        TargetFieldGetter targetFieldGetter = new TargetFieldGetter(getClassName(), target);

        this.getters.put(target.getUniqueName(), targetFieldGetter);
        return this;
    }

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
     * @param fieldName
     * @param fieldRef
     * @return
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
     * @param fieldName
     * @return
     */
    public Getter<?> getGetter(String fieldName) {
        return getters.get(fieldName);
    }

    public Setter<?> defineSetter(String fieldName, FieldRef fieldRef) {
        return setters.computeIfAbsent(fieldName, key -> {
            if (fieldRef instanceof EarlyFieldRef) {
                return new EarlyFieldSetter(getClassName(), (EarlyFieldRef) fieldRef);
            } else {
                return new RuntimeFieldSetter(getClassName(), (RuntimeFieldRef) fieldRef);
            }
        });
    }

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
     * 定义运行时的lookup
     *
     * @param fieldRef
     * @return
     */
    public LookupMember defineRuntimeLookup(FieldRef fieldRef) {
        String lookupMemberName = fieldRef.getUniqueName()+"_runtime_lookup";
        if (!members.containsKey(lookupMemberName)) {
            int access = Opcodes.ACC_PUBLIC;
            super.defineField(access, lookupMemberName, Lookup.DESCRIPTOR, null, null);
            this.members.put(lookupMemberName, new LookupMember(access, implClassDesc, lookupMemberName));
        }
        return (LookupMember) members.get(lookupMemberName);
    }

    /**
     * 定义指定类型的lookup字段
     *
     * @param staticType
     * @return
     */
    public LookupMember defineTypedLookup(Type staticType) {
        String memberName = staticType.getClassName().replace('.', '_')+"_lookup";
        if (!members.containsKey(memberName)) {
            int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
            super.defineField(access, memberName, Lookup.DESCRIPTOR, null, null);
            this.members.put(memberName, new LookupMember(access, implClassDesc, memberName, staticType));
        }
        return (LookupMember) members.get(memberName);
    }

    /**
     * 定义静态的methodHandle
     *
     * @param mhMemberName
     * @param methodType
     * @return
     */
    public MethodHandleMember defineStaticMethodHandle(String mhMemberName, Type methodType) {
        if (!members.containsKey(mhMemberName)) {
            int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
            super.defineField(access, mhMemberName, MethodHandle.DESCRIPTOR, null, null);
            this.members.put(mhMemberName, new MethodHandleMember(access, implClassDesc, mhMemberName, methodType));
        }
        return (MethodHandleMember) members.get(mhMemberName);
    }

    public MethodHandleMember defineMethodHandle(String mhMemberName, Type methodType) {
        if (!members.containsKey(mhMemberName)) {
            super.defineField(Opcodes.ACC_PUBLIC, mhMemberName, MethodHandle.DESCRIPTOR, null, null);
            this.members.put(mhMemberName, new MethodHandleMember(Opcodes.ACC_PUBLIC, implClassDesc, mhMemberName, methodType));
        }
        return (MethodHandleMember) members.get(mhMemberName);
    }

    public MethodBody getClinit() {
        if (clinitMethodWriter == null) {
            clinitMethodWriter = this.defineMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null)
                    .getMethodVisitor();
        }
        if (clinit == null) {
            clinit = new MethodBody(clinitMethodWriter, Type.getMethodType(Type.VOID_TYPE));
        }
        return clinit;
    }
}
