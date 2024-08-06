package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.bytecode.vars.VarInst;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ImplClassConstruct {
    private Map<String, VarInst> lvb = new HashMap<>();
    private MethodVisitor methodWriter;

    public ImplClassConstruct(MethodVisitor methodWriter) {
        this.methodWriter = methodWriter;

        // lvb 默认有个target
        this.lvb.put("target", new VarInst("Ljava/lang/Object;", 1));
    }

    public void end() {
        this.methodWriter.visitInsn(Opcodes.RETURN);
        this.methodWriter.visitMaxs(0, 0);
    }

    public void append(Consumer<MethodVisitor> interceptor) {
        if (methodWriter != null) {
            interceptor.accept(methodWriter);
        }
    }

    public VarInst getLocalVar(String prevVar) {
        return lvb.get(prevVar);
    }
}
