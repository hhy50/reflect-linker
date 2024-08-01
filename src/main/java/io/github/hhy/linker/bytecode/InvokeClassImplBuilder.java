package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmClassBuilder;
import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.define.DefaultTargetProviderImpl;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

public class InvokeClassImplBuilder extends AsmClassBuilder {

    private Class<?> bindTarget;

    /**
     *
     */
    private Map<String /* lookup_class */, String /* lookup_var */> lookups = new HashMap<>();

    public InvokeClassImplBuilder(int access, String className, String superName, String[] interfaces, String signature) {
        super(access, className, superName, interfaces, signature);
    }

    public InvokeClassImplBuilder setTarget(Class<?> bindTarget) {
        this.bindTarget = bindTarget;
        this.defineConstruct(Opcodes.ACC_PUBLIC, new String[]{bindTarget.getName()}, null, "")
                .accept(writer -> {
                    writer.visitVarInsn(Opcodes.ALOAD, 0);
                    writer.visitVarInsn(Opcodes.ALOAD, 1);
                    writer.visitMethodInsn(Opcodes.INVOKESPECIAL, ClassUtil.className2path(DefaultTargetProviderImpl.class.getName()), "<init>", "(Ljava/lang/Object;)V", false);
                    writer.visitInsn(Opcodes.RETURN);
                });
        defineLookup(bindTarget.getName());
        return this;
    }

    public String defineLookup(String className) {
        if (!lookups.containsKey(className)) {
            String lookupVar = className.replace('.', '_') + "_lookup";
            this.defineField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, lookupVar,
                            "Ljava/lang/invoke/MethodHandles$Lookup;", null, null)
                    .writeClint(writer -> {
                        // lookup = Runtime.lookup(className);
                        writer.visitLdcInsn(Type.getType(AsmUtil.toTypeDesc(className)));
                        writer.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/hhy/linker/runtime/Runtime", "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
                        writer.visitFieldInsn(Opcodes.PUTSTATIC, ClassUtil.className2path(this.getClassName()), lookupVar, "Ljava/lang/invoke/MethodHandles$Lookup;");
                    });
            lookups.put(className, lookupVar);
            return lookupVar;
        }
        return lookups.get(className);
    }
}
