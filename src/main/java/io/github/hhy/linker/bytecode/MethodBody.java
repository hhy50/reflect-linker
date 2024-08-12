package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.function.Consumer;

public class MethodBody {

    public final MethodVisitor writer;

    private Type methodType;

    public int lvbIndex;

    public MethodBody(MethodVisitor mv, Type methodType) {
        this.writer = mv;
        this.methodType = methodType;
        this.lvbIndex = AsmUtil.calculateLvbOffset(false, methodType.getArgumentTypes());
    }

    public void append(Consumer<MethodVisitor> interceptor) {
        interceptor.accept(this.writer);
    }

    public void end() {
        this.writer.visitMaxs(0, 0);
    }

    /**
     * 根据参数生命的顺序， 获取第几个参数
     *
     * @param i
     * @return
     */
    public ObjectVar getArg(int i) {
        Type type = methodType.getArgumentTypes()[i];
        int index = AsmUtil.calculateLvbOffset(false, Arrays.copyOfRange(methodType.getArgumentTypes(), 0, i));
        return new ObjectVar(index, type.getDescriptor());
    }

    public void loadArgs() {
        for (int i = 0; i < methodType.getArgumentTypes().length; i++) {
            getArg(i).load(this);
        }
    }
}
