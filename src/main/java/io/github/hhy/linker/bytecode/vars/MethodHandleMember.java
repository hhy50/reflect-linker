package io.github.hhy.linker.bytecode.vars;



public class MethodHandleMember extends Member {

    public MethodHandleMember(String owner, String mhVarName) {
        super(owner, mhVarName, MethodHandleVar.DESCRIPTOR);
    }

    public void invoke(ObjectVar... args) {

    }
}
