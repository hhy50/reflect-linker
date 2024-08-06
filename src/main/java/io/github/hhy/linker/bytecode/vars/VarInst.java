package io.github.hhy.linker.bytecode.vars;

public class VarInst {
    private String type;
    /**
     * 局部变量表的索引
     */
    private int index;

    public VarInst(String type, int index) {
        this.type = type;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
