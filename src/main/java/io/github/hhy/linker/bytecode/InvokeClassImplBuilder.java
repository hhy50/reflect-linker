package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmClassBuilder;
import io.github.hhy.linker.define.RuntimeField;
import io.github.hhy.linker.define.provider.DefaultTargetProviderImpl;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

public class InvokeClassImplBuilder extends AsmClassBuilder {
    public String bindTarget;
    private Map<String, Getter> getters;

    public InvokeClassImplBuilder(int access, String className, String superName, String[] interfaces, String signature) {
        super(access, className, superName, interfaces, signature);
        this.getters = new HashMap<>();
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

    public Getter defineGetter(RuntimeField field) {
        String getterMhVarName = field.getGetterMhVarName();
        return getters.computeIfAbsent(getterMhVarName, key -> new RuntimeFieldGetter(getClassName(), field));
    }
}
