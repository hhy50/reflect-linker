package io.github.hhy50.linker.entity;

import io.github.hhy50.linker.generate.bytecode.vars.LookupVar;

/**
 * The type Method holder.
 */
public class MethodHolder {

    /**
     * The constant STRING_CONTAINS.
     */
    public static final MethodHolder STRING_CONTAINS = new MethodHolder("java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z");
    /**
     * The constant OBJECT_GET_CLASS.
     */
    public static final MethodHolder OBJECT_GET_CLASS = new MethodHolder("java/lang/Object", "getClass", "()Ljava/lang/Class;");
    /**
     * The constant CLASS_GET_NAME.
     */
    public static final MethodHolder CLASS_GET_NAME = new MethodHolder("java/lang/Class", "getName", "()Ljava/lang/String;");
    /**
     * The constant LOOKUP_LOOKUP_CLASS.
     */
    public static final MethodHolder LOOKUP_LOOKUP_CLASS = new MethodHolder(LookupVar.OWNER, "lookupClass", "()Ljava/lang/Class;");
    /**
     * The constant LOOKUP_FIND_GETTER_METHOD.
     */
    public static final MethodHolder LOOKUP_FIND_GETTER_METHOD = new MethodHolder(LookupVar.OWNER, "findGetter", LookupVar.FIND_XETTER_DESC);
    /**
     * The constant LOOKUP_FIND_STATIC_GETTER_METHOD.
     */
    public static final MethodHolder LOOKUP_FIND_STATIC_GETTER_METHOD = new MethodHolder(LookupVar.OWNER, "findStaticGetter", LookupVar.FIND_XETTER_DESC);
    /**
     * The constant LOOKUP_FIND_SETTER_METHOD.
     */
    public static final MethodHolder LOOKUP_FIND_SETTER_METHOD = new MethodHolder(LookupVar.OWNER, "findSetter", LookupVar.FIND_XETTER_DESC);
    /**
     * The constant LOOKUP_FIND_STATIC_SETTER_METHOD.
     */
    public static final MethodHolder LOOKUP_FIND_STATIC_SETTER_METHOD = new MethodHolder(LookupVar.OWNER, "findStaticSetter", LookupVar.FIND_XETTER_DESC);
    /**
     * The constant LOOKUP_FIND_FINDVIRTUAL.
     */
    public static final MethodHolder LOOKUP_FIND_FINDVIRTUAL = new MethodHolder(LookupVar.OWNER, "findVirtual", LookupVar.FIND_VIRTUAL);
    /**
     * The constant LOOKUP_FIND_FINDSPECIAL.
     */
    public static final MethodHolder LOOKUP_FIND_FINDSPECIAL = new MethodHolder(LookupVar.OWNER, "findSpecial", LookupVar.FIND_SPECIAL);
    /**
     * The constant METHOD_TYPE.
     */
    public static final MethodHolder METHOD_TYPE = new MethodHolder("java/lang/invoke/MethodType", "methodType", "(Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;");
    /**
     * The constant ARRAYS_ASLIST.
     */
    public static final MethodHolder ARRAYS_ASLIST = new MethodHolder("java/util/Arrays", "asList", "([Ljava/lang/Object;)Ljava/util/List;");
    /**
     * The constant DEFAULT_PROVIDER_GET_TARGET.
     */
    public static final MethodHolder DEFAULT_PROVIDER_GET_TARGET = new MethodHolder("io/github/hhy50/linker/define/provider/DefaultTargetProviderImpl", "getTarget", "()Ljava/lang/Object;");
    /**
     * The constant LINKER_FACTORY_CREATE_LINKER.
     */
    public static final MethodHolder LINKER_FACTORY_CREATE_LINKER = new MethodHolder("io/github/hhy50/linker/LinkerFactory", "createLinker", "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;");
    /**
     * The constant GET_CLASS_LOADER.
     */
    public static final MethodHolder GET_CLASS_LOADER = new MethodHolder("java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;");

    private final String owner;
    private final String methodName;
    private final String methodDesc;

    /**
     * Instantiates a new Method holder.
     *
     * @param owner      the owner
     * @param methodName the method name
     * @param methodDesc the method desc
     */
    public MethodHolder(String owner, String methodName, String methodDesc) {
        this.owner = owner;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    /**
     * Gets owner.
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Gets method name.
     *
     * @return the method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return methodDesc;
    }
}
