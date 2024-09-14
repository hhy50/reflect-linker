package io.github.hhy50.linker.entity;

import io.github.hhy50.linker.generate.bytecode.vars.LookupVar;

/**
 * <p>MethodHolder class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class MethodHolder {

    /** Constant <code>STRING_CONTAINS</code> */
    public static final MethodHolder STRING_CONTAINS = new MethodHolder("java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z");
    /** Constant <code>OBJECT_GET_CLASS</code> */
    public static final MethodHolder OBJECT_GET_CLASS = new MethodHolder("java/lang/Object", "getClass", "()Ljava/lang/Class;");
    /** Constant <code>CLASS_GET_NAME</code> */
    public static final MethodHolder CLASS_GET_NAME = new MethodHolder("java/lang/Class", "getName", "()Ljava/lang/String;");
    /** Constant <code>LOOKUP_LOOKUP_CLASS</code> */
    public static final MethodHolder LOOKUP_LOOKUP_CLASS = new MethodHolder(LookupVar.OWNER, "lookupClass", "()Ljava/lang/Class;");
    /** Constant <code>LOOKUP_FIND_GETTER_METHOD</code> */
    public static final MethodHolder LOOKUP_FIND_GETTER_METHOD = new MethodHolder(LookupVar.OWNER, "findGetter", LookupVar.FIND_XETTER_DESC);
    /** Constant <code>LOOKUP_FIND_STATIC_GETTER_METHOD</code> */
    public static final MethodHolder LOOKUP_FIND_STATIC_GETTER_METHOD = new MethodHolder(LookupVar.OWNER, "findStaticGetter", LookupVar.FIND_XETTER_DESC);
    /** Constant <code>LOOKUP_FIND_SETTER_METHOD</code> */
    public static final MethodHolder LOOKUP_FIND_SETTER_METHOD = new MethodHolder(LookupVar.OWNER, "findSetter", LookupVar.FIND_XETTER_DESC);
    /** Constant <code>LOOKUP_FIND_STATIC_SETTER_METHOD</code> */
    public static final MethodHolder LOOKUP_FIND_STATIC_SETTER_METHOD = new MethodHolder(LookupVar.OWNER, "findStaticSetter", LookupVar.FIND_XETTER_DESC);
    /** Constant <code>LOOKUP_FIND_FINDVIRTUAL</code> */
    public static final MethodHolder LOOKUP_FIND_FINDVIRTUAL = new MethodHolder(LookupVar.OWNER, "findVirtual", LookupVar.FIND_VIRTUAL);
    /** Constant <code>LOOKUP_FIND_FINDSPECIAL</code> */
    public static final MethodHolder LOOKUP_FIND_FINDSPECIAL = new MethodHolder(LookupVar.OWNER, "findSpecial", LookupVar.FIND_SPECIAL);
    /** Constant <code>METHOD_TYPE</code> */
    public static final MethodHolder METHOD_TYPE = new MethodHolder("java/lang/invoke/MethodType", "methodType", "(Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;");
    /** Constant <code>ARRAYS_ASLIST</code> */
    public static final MethodHolder ARRAYS_ASLIST = new MethodHolder("java/util/Arrays", "asList", "([Ljava/lang/Object;)Ljava/util/List;");
    /** Constant <code>DEFAULT_PROVIDER_GET_TARGET</code> */
    public static final MethodHolder DEFAULT_PROVIDER_GET_TARGET = new MethodHolder("io/github/hhy50/linker/define/provider/DefaultTargetProviderImpl", "getTarget", "()Ljava/lang/Object;");
    /** Constant <code>LINKER_FACTORY_CREATE_LINKER</code> */
    public static final MethodHolder LINKER_FACTORY_CREATE_LINKER = new MethodHolder("io/github/hhy50/linker/LinkerFactory", "createLinker", "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;");
    /** Constant <code>GET_CLASS_LOADER</code> */
    public static final MethodHolder GET_CLASS_LOADER = new MethodHolder("java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;");

    private final String owner;
    private final String methodName;
    private final String methodDesc;

    /**
     * <p>Constructor for MethodHolder.</p>
     *
     * @param owner a {@link java.lang.String} object.
     * @param methodName a {@link java.lang.String} object.
     * @param methodDesc a {@link java.lang.String} object.
     */
    public MethodHolder(String owner, String methodName, String methodDesc) {
        this.owner = owner;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    /**
     * <p>Getter for the field <code>owner</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * <p>Getter for the field <code>methodName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * <p>getDesc.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDesc() {
        return methodDesc;
    }
}
