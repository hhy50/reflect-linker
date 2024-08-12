package io.github.hhy.linker.bytecode.vars;


public class LookupMember extends Member {

    public LookupMember(int access, String owner, String lookupName) {
        super(access, owner, lookupName, LookupVar.DESCRIPTOR);
    }

    public LookupMember(String owner, String lookupName) {
        super(owner, lookupName, LookupVar.DESCRIPTOR);
    }
}
