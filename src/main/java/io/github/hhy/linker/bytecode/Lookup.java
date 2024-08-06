package io.github.hhy.linker.bytecode;


public class Lookup {
    private boolean isStatic;
    private String varName;

    /**
     *
     */
    private String lookupClass;

    /**
     * 局部变量表索引
     */
    private int lvbIndex;

    public Lookup(String varName, String lookupClass, boolean isStatic) {
        this.varName = varName;
        this.lookupClass = lookupClass;
        this.isStatic = isStatic;
    }

    public String getVarName() {
        return varName;
    }

    public String getLookupClass() {
        return lookupClass;
    }

    public static Lookup target(String targetClass) {
        return new Lookup("target_lookup", targetClass, true);
    }
}
