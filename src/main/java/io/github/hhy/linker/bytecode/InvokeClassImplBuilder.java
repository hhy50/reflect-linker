package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmClassBuilder;
import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.asm.MethodBuilder;
import io.github.hhy.linker.define.DefaultTargetProviderImpl;
import io.github.hhy.linker.exceptions.ImplClassBuilderException;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class InvokeClassImplBuilder extends AsmClassBuilder {

    public String bindTarget;
    private MethodVisitor initMethodWriter;
    /**
     *
     */
    private Map<String /* lookup_class */, Lookup /* lookup_var */> lookups = new HashMap<>();

    public InvokeClassImplBuilder(int access, String className, String superName, String[] interfaces, String signature) {
        super(access, className, superName, interfaces, signature);
    }

    @Override
    public MethodBuilder defineConstruct(int access, String[] argsType, String[] exceptions, String sign) {
        if (this.initMethodWriter != null) {
            MethodBuilder methodBuilder = super.defineConstruct(access, argsType, exceptions, sign);
            this.initMethodWriter = methodBuilder.getMethodVisitor();
            return methodBuilder;
        }
        throw new ImplClassBuilderException("Define multiple constructors");
    }

    @Override
    public AsmClassBuilder end() {
        if (this.initMethodWriter != null) {
            this.initMethodWriter.visitInsn(Opcodes.RETURN);
            this.initMethodWriter.visitMaxs(0, 0);
        }
        return super.end();
    }

    public void appendInit(Consumer<MethodVisitor> interceptor) {
        if (initMethodWriter != null) {
            interceptor.accept(initMethodWriter);
        }
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
        defineLookup(bindTarget);
        return this;
    }

    public Lookup defineLookup(String lookupClass) {
        Lookup lookup = findLookup(lookupClass);
        if (lookup == null) {
            String lookupVar = lookupClass.replace('.', '_') + "_lookup";
            this.defineField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, lookupVar,
                            "Ljava/lang/invoke/MethodHandles$Lookup;", null, null)
                    .appendClinit(writer -> {
                        // lookup = Runtime.lookup(className);
                        writer.visitLdcInsn(Type.getType(AsmUtil.toTypeDesc(lookupClass)));
                        writer.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/hhy/linker/runtime/Runtime", "lookup", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodHandles$Lookup;", false);
                        writer.visitFieldInsn(Opcodes.PUTSTATIC, ClassUtil.className2path(this.getClassName()), lookupVar, "Ljava/lang/invoke/MethodHandles$Lookup;");
                    });
            lookup = new Lookup(lookupVar, lookupClass);
            lookups.put(lookupClass, lookup);
        }
        return lookup;
    }

    public Lookup findLookup(String lookupName) {
        return lookups.get(lookupName);
    }
}
