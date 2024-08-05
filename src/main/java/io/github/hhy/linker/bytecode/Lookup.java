package io.github.hhy.linker.bytecode;


public class Lookup {
    private String varName;
    private String lookupClass;

    public Lookup(String varName, String lookupClass) {
        this.varName = varName;
        this.lookupClass = lookupClass;
    }

    public String getVarName() {
        return varName;
    }

    public String getLookupClass() {
        return lookupClass;
    }

    public static Lookup target(String targetClass) {
        return new Lookup("target_lookup", targetClass);
    }
}
