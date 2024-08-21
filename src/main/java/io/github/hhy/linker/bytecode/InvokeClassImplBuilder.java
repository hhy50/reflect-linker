package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmClassBuilder;
import io.github.hhy.linker.bytecode.getter.EarlyFieldGetter;
import io.github.hhy.linker.bytecode.getter.Getter;
import io.github.hhy.linker.bytecode.getter.RuntimeFieldGetter;
import io.github.hhy.linker.bytecode.getter.TargetFieldGetter;
import io.github.hhy.linker.bytecode.setter.Setter;
import io.github.hhy.linker.bytecode.vars.*;
import io.github.hhy.linker.define.field2.EarlyFieldRef;
import io.github.hhy.linker.define.field2.FieldRef;
import io.github.hhy.linker.define.field2.RuntimeFieldRef;
import io.github.hhy.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

public class InvokeClassImplBuilder extends AsmClassBuilder {
    public Class<?> bindTarget;
    private final String implClassDesc;
    private MethodBody clinit;
    private final Map<String, Getter> getters;
    private final Map<String, Setter> setters;
    private final Map<String, Member> members;

    public InvokeClassImplBuilder(int access, String className, String superName, String[] interfaces, String signature) {
        super(access, className, superName, interfaces, signature);
        this.implClassDesc = ClassUtil.className2path(this.getClassName());
        this.getters = new HashMap<>();
        this.setters = new HashMap<>();
        this.members = new HashMap<>();
    }

    private void init() {
        EarlyFieldRef target = new EarlyFieldRef(null, "target", Type.getType(bindTarget));
        TargetFieldGetter targetFieldGetter = new TargetFieldGetter(getClassName(), target);

        LookupMember lookupMember = this.defineLookup(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, target.getLookupName());
        lookupMember.isTarget(true);
        lookupMember.staticInit(this.getClinit(), target.getType());

        this.members.put(lookupMember.memberName, lookupMember);
        this.getters.put(targetFieldGetter.field.getGetterName(), targetFieldGetter);
    }

    public InvokeClassImplBuilder setTarget(Class<?> bindTarget) {
        this.bindTarget = bindTarget;
        this.defineConstruct(Opcodes.ACC_PUBLIC, new String[]{bindTarget.getName()}, null, "")
                .accept(mv -> {
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.ALOAD, 1);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, ClassUtil.className2path(DefaultTargetProviderImpl.class.getName()), "<init>", "(Ljava/lang/Object;)V", false);
                    mv.visitInsn(Opcodes.RETURN);
                });
        init();
        return this;
    }

    /**
     * 定义Getter
     * @param field
     * @param methodType
     * @return
     */
    public Getter defineGetter(String fieldName, FieldRef fieldRef) {
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
     * @param fieldName
     * @return
     */
    public Getter getGetter(String fieldName) {
        return getters.get(fieldName);
    }

    public Setter defineSetter(String fieldName, FieldRef field) {
        return setters.computeIfAbsent(fieldName, key -> new Setter(getClassName(), field));
    }

    /**
     * 定义运行时的lookup
     * @param fieldRef
     * @return
     */
    public LookupMember defineLookup(FieldRef fieldRef) {
        String lookupMemberName = fieldRef.getFullName()+"_lookup";
        if (!members.containsKey(lookupMemberName)) {
            int access = Opcodes.ACC_PUBLIC;
            if (fieldRef instanceof EarlyFieldRef) {
                access |= Opcodes.ACC_STATIC;
            }
            super.defineField(access, lookupMemberName, LookupVar.DESCRIPTOR, null, null);
            this.members.put(lookupMemberName, new LookupMember(access, implClassDesc, lookupMemberName));
        }
        return (LookupMember) members.get(lookupMemberName);
    }

    /**
     * 定义指定类型的lookup字段
     * @param access
     * @param type
     * @return
     */
    public LookupMember defineLookup(int access, Type type) {
        String memberName = type.getClassName().replace('.', '_') + "_lookup";
        if (!members.containsKey(memberName)) {
            super.defineField(access, memberName, LookupVar.DESCRIPTOR, null, null);
            this.members.put(memberName, new LookupMember(access, implClassDesc, memberName, type));
        }
        return (LookupMember) members.get(memberName);
    }

    /**
     * 定义静态的methodHandle
     * @param mhMemberName
     * @param methodType
     * @return
     */
    public MethodHandleMember defineStaticMethodHandle(String mhMemberName, Type methodType) {
        if (!members.containsKey(mhMemberName)) {
            int access = Opcodes.ACC_PUBLIC|Opcodes.ACC_STATIC;
            super.defineField(access, mhMemberName, MethodHandleVar.DESCRIPTOR, null, null);
            this.members.put(mhMemberName, new MethodHandleMember(access, implClassDesc, mhMemberName, methodType));
        }
        return (MethodHandleMember) members.get(mhMemberName);
    }

    public MethodHandleMember defineMethodHandle(String mhMemberName, Type methodType) {
        if (!members.containsKey(mhMemberName)) {
            super.defineField(Opcodes.ACC_PUBLIC, mhMemberName, MethodHandleVar.DESCRIPTOR, null, null);
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
