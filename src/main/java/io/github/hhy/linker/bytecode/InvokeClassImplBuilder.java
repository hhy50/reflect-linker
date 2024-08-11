package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmClassBuilder;
import io.github.hhy.linker.bytecode.getter.Getter;
import io.github.hhy.linker.bytecode.getter.RuntimeFieldGetter;
import io.github.hhy.linker.bytecode.getter.TargetFieldGetter;
import io.github.hhy.linker.bytecode.setter.Setter;
import io.github.hhy.linker.bytecode.vars.Member;
import io.github.hhy.linker.define.RuntimeField;
import io.github.hhy.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InvokeClassImplBuilder extends AsmClassBuilder {
    public String bindTarget;
    private final Map<String, Getter> getters;
    private final Map<String, Setter> setters;
    private final Set<String> members;

    public InvokeClassImplBuilder(int access, String className, String superName, String[] interfaces, String signature) {
        super(access, className, superName, interfaces, signature);
        this.getters = new HashMap<>();
        this.setters = new HashMap<>();
        this.members = new HashSet<>();
        this.getters.put(RuntimeField.TARGET.getGetterMhVarName(), new TargetFieldGetter(getClassName()));
    }

    public InvokeClassImplBuilder setTarget(String bindTarget) {
        this.bindTarget = bindTarget;
        this.defineConstruct(Opcodes.ACC_PUBLIC, new String[]{bindTarget}, null, "")
                .accept(writer -> {
                    writer.visitVarInsn(Opcodes.ALOAD, 0);
                    writer.visitVarInsn(Opcodes.ALOAD, 1);
                    writer.visitMethodInsn(Opcodes.INVOKESPECIAL, ClassUtil.className2path(DefaultTargetProviderImpl.class.getName()), "<init>", "(Ljava/lang/Object;)V", false);
                    writer.visitInsn(Opcodes.RETURN);
                });
        return this;
    }

    /**
     * @param member
     */
    public void defineField(Member member) {
        if (!members.contains(member.memberName)) {
            super.defineField(Opcodes.ACC_PUBLIC, member.memberName, member.type, null, null);
            this.members.add(member.memberName);
        }
    }

    public Getter defineGetter(RuntimeField field, Type methodType) {
        String getterMhVarName = field.getGetterMhVarName();
        return getters.computeIfAbsent(getterMhVarName, key -> new RuntimeFieldGetter(getClassName(), field, methodType));
    }

    public Setter defineSetter(RuntimeField field, Type methodType) {
        String getterMhVarName = field.getGetterMhVarName();
        return setters.computeIfAbsent(getterMhVarName, key -> new Setter(getClassName(), field, methodType));
    }
}
