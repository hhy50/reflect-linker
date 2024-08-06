package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmClassBuilder;
import io.github.hhy.linker.asm.MethodBuilder;
import io.github.hhy.linker.define.DefaultTargetProviderImpl;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

public class InvokeClassImplBuilder extends AsmClassBuilder {
    public String bindTarget;
    private ImplClassConstruct implClassConstruct;
    private Map<String /* lookup_class */, Lookup /* lookup_var */> lookups = new HashMap<>();

    public InvokeClassImplBuilder(int access, String className, String superName, String[] interfaces, String signature) {
        super(access, className, superName, interfaces, signature);
    }

    public ImplClassConstruct getConstruct() {
        if (this.implClassConstruct == null) {
            MethodBuilder methodBuilder = super.defineConstruct(Opcodes.ACC_PUBLIC, new String[]{bindTarget}, null, null);
            this.implClassConstruct = new ImplClassConstruct(methodBuilder.getMethodVisitor());
        }
        return this.implClassConstruct;
    }

    @Override
    public AsmClassBuilder end() {
        if (this.implClassConstruct != null) {
            this.implClassConstruct.end();
        }
        return super.end();
    }

    public InvokeClassImplBuilder setTarget(String bindTarget) {
        this.bindTarget = bindTarget;
        this.getConstruct()
                .append(writer -> {
                    writer.visitVarInsn(Opcodes.ALOAD, 0);
                    writer.visitVarInsn(Opcodes.ALOAD, 1);
                    writer.visitMethodInsn(Opcodes.INVOKESPECIAL, ClassUtil.className2path(DefaultTargetProviderImpl.class.getName()), "<init>", "(Ljava/lang/Object;)V", false);
                });
        return this;
    }

//    public Lookup defineLookup(String lookupName) {
//        Lookup lookup = findLookup(lookupClass);
//        if (lookup == null) {
//            String lookupVar = lookupClass.replace('.', '_')+"_lookup";
//            this.defineField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, lookupVar,
//                            "Ljava/lang/invoke/MethodHandles$Lookup;", null, null)
//                    .appendClinit(writer -> {
//                        // lookup = Runtime.lookup(className);
//                        writer.visitLdcInsn(Type.getType(AsmUtil.toTypeDesc(lookupClass)));
//                        writer.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/hhy/linker/runtime/Runtime", "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
//                        writer.visitFieldInsn(Opcodes.PUTSTATIC, ClassUtil.className2path(this.getClassName()), lookupVar, "Ljava/lang/invoke/MethodHandles$Lookup;");
//                    });
//            lookup = new Lookup(lookupVar, lookupClass);
//            lookups.put(lookupClass, lookup);
//        }
//        return lookup;
//    }

    public Lookup findLookup(String lookupName) {
        return lookups.get(lookupName);
    }
}
