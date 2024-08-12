package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmClassBuilder;
import io.github.hhy.linker.bytecode.getter.Getter;
import io.github.hhy.linker.bytecode.getter.RuntimeFieldGetter;
import io.github.hhy.linker.bytecode.getter.TargetFieldGetter;
import io.github.hhy.linker.bytecode.setter.Setter;
import io.github.hhy.linker.bytecode.vars.*;
import io.github.hhy.linker.define.RuntimeField;
import io.github.hhy.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

public class InvokeClassImplBuilder extends AsmClassBuilder {
    public String bindTarget;
    private final String implClassDesc;
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
        String targetLookup = RuntimeField.TARGET.getLookupName();
        this.defineField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, targetLookup, LookupVar.DESCRIPTOR, null, null);
        this.appendClinit(mv -> {
            // lookup = Runtime.lookup(className);
            mv.visitLdcInsn(Type.getType("L"+ClassUtil.className2path(bindTarget)+";"));
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/hhy/linker/runtime/Runtime", "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
            mv.visitFieldInsn(Opcodes.PUTSTATIC, ClassUtil.className2path(this.getClassName()), targetLookup, "Ljava/lang/invoke/MethodHandles$Lookup;");
        });
        this.members.put(targetLookup, new LookupMember(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                implClassDesc, targetLookup));
        this.getters.put(RuntimeField.TARGET.getGetterMhVarName(), new TargetFieldGetter(getClassName()));
    }

    public InvokeClassImplBuilder setTarget(String bindTarget) {
        this.bindTarget = bindTarget;
        this.defineConstruct(Opcodes.ACC_PUBLIC, new String[]{bindTarget}, null, "")
                .accept(mv -> {
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.ALOAD, 1);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, ClassUtil.className2path(DefaultTargetProviderImpl.class.getName()), "<init>", "(Ljava/lang/Object;)V", false);
                    mv.visitInsn(Opcodes.RETURN);
                });
        init();
        return this;
    }

    public Getter defineGetter(RuntimeField field, Type methodType) {
        String getterMhVarName = field.getGetterMhVarName();
        return getters.computeIfAbsent(getterMhVarName, key -> new RuntimeFieldGetter(getClassName(), field, methodType));
    }

    public Setter defineSetter(RuntimeField field, Type methodType) {
        String getterMhVarName = field.getGetterMhVarName();
        return setters.computeIfAbsent(getterMhVarName, key -> new Setter(getClassName(), field, methodType));
    }

    public LookupMember defineLookup(String lookupMemberName) {
        if (!members.containsKey(lookupMemberName)) {
            super.defineField(Opcodes.ACC_PUBLIC, lookupMemberName, LookupVar.DESCRIPTOR, null, null);
            this.members.put(lookupMemberName, new LookupMember(implClassDesc, lookupMemberName));
        }
        return (LookupMember) members.get(lookupMemberName);
    }

    public MethodHandleMember defineMethodHandle(String mhMemberName, Type methodType) {
        if (!members.containsKey(mhMemberName)) {
            super.defineField(Opcodes.ACC_PUBLIC, mhMemberName, MethodHandleVar.DESCRIPTOR, null, null);
            this.members.put(mhMemberName, new MethodHandleMember(implClassDesc, mhMemberName, methodType));
        }
        return (MethodHandleMember) members.get(mhMemberName);
    }
}
