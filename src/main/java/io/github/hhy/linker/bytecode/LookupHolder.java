package io.github.hhy.linker.bytecode;


public class LookupHolder {
    private String varName;
    private String lookupClass;

    public LookupHolder(String varName, String lookupClass) {
        this.varName = varName;
        this.lookupClass = lookupClass;
    }

    public String getVarName() {
        return varName;
    }

    public String getLookupClass() {
        return lookupClass;
    }
}
