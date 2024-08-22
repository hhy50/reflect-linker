package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.bytecode.vars.ObjectVar;
import io.github.hhy.linker.bytecode.vars.VarInst;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

public class MethodBody {
    private final MethodVisitor writer;
    private Type methodType;
    public int lvbIndex;
    private VarInst[] args;

    // ============================ Labels ===================================
    private Label checkLookupLabel;
    private Label lookupReassignLabel;
    private Label checkMhLabel;

    public MethodBody(MethodVisitor mv, Type methodType) {
        Type[] argumentTypes = methodType.getArgumentTypes();
        this.writer = mv;
        this.methodType = methodType;
        this.lvbIndex = AsmUtil.calculateLvbOffset(false, argumentTypes);
        this.args = new VarInst[argumentTypes.length];

        initArgsTable(argumentTypes);
    }

    private void initArgsTable(Type[] argumentTypes) {
        int index = 1;
        for (int i = 0; i < argumentTypes.length; i++) {
            args[i] = new ObjectVar(index++, argumentTypes[i]);
            if (argumentTypes[i].getSort() == Type.DOUBLE || argumentTypes[i].getSort() == Type.LONG) {
                index++;
            }
        }
    }

    public void append(Consumer<MethodVisitor> interceptor) {
        interceptor.accept(this.writer);
    }

    /**
     * 根据参数生命的顺序， 获取第几个参数
     *
     * @param i
     * @return
     */
    public VarInst getArg(int i) {
        return args[i];
    }

    public void loadArgs() {
        for (int i = 0; i < methodType.getArgumentTypes().length; i++) {
            getArg(i).load(this);
        }
    }

    public Label getCheckLookupLabel() {
        if (checkLookupLabel == null) {
            checkLookupLabel = new Label();
        }
        return checkLookupLabel;
    }

    public Label getLookupAssignLabel() {
        if (lookupReassignLabel == null) {
            lookupReassignLabel = new Label();
        }
        return lookupReassignLabel;
    }

    public Label getCheckMhLabel() {
        if (checkMhLabel == null) {
            checkMhLabel = new Label();
        }
        return checkMhLabel;
    }

    public void setArg(int i, VarInst arg) {
        this.args[i] = arg;
    }
}
