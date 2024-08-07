package io.github.hhy.linker.bytecode.vars;




public class LookupMember extends Member {


    public LookupMember(String owner, String lookupName) {
        super(owner, lookupName, LookupVar.DESCRIPTOR);
    }

}
