package io.github.hhy.linker.entity;

import io.github.hhy.linker.constant.Lookup;

public class MethodHolder {

    public static final MethodHolder STRING_CONTAINS = new MethodHolder("java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z");
    public static final MethodHolder OBJECT_GET_CLASS = new MethodHolder("java/lang/Object", "getClass", "()Ljava/lang/Class;");
    public static final MethodHolder CLASS_GET_NAME = new MethodHolder("java/lang/Class", "getName", "()Ljava/lang/String;");
    public static final MethodHolder LOOKUP_LOOKUP_CLASS = new MethodHolder(Lookup.OWNER, "lookupClass", "()Ljava/lang/Class;");
    public static final MethodHolder LOOKUP_FIND_GETTER_METHOD = new MethodHolder(Lookup.OWNER, "findGetter", Lookup.FIND_GETTER_DESC);
    public static final MethodHolder LOOKUP_FIND_STATIC_GETTER_METHOD = new MethodHolder(Lookup.OWNER, "findStaticGetter", Lookup.FIND_GETTER_DESC);
    public static final MethodHolder LOOKUP_FIND_SETTER_METHOD = new MethodHolder(Lookup.OWNER, "findSetter", Lookup.FIND_GETTER_DESC);
    public static final MethodHolder LOOKUP_FIND_STATIC_SETTER_METHOD = new MethodHolder(Lookup.OWNER, "findStaticSetter", Lookup.FIND_SETTER_DESC);

    private final String owner;
    private final String methodName;
    private final String methodDesc;

    public MethodHolder(String owner, String methodName, String methodDesc) {
        this.owner = owner;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    public String getOwner() {
        return owner;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getDesc() {
        return methodDesc;
    }
}