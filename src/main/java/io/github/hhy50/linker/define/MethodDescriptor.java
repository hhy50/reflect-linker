package io.github.hhy50.linker.define;

import io.github.hhy50.linker.generate.bytecode.vars.LookupVar;

/**
 * The type Method holder.
 */
public class MethodDescriptor {

    /**
     * The constant OBJECT_GET_CLASS.
     */
    public static final MethodDescriptor OBJECT_GET_CLASS = MethodDescriptor.of("java/lang/Object", "getClass", "()Ljava/lang/Class;");
    /**
     * The constant LOOKUP_FIND_GETTER_METHOD.
     */
    public static final MethodDescriptor LOOKUP_FIND_GETTER_METHOD = MethodDescriptor.of(LookupVar.OWNER, "findGetter", LookupVar.FIND_XETTER_DESC);
    /**
     * The constant LOOKUP_FIND_STATIC_GETTER_METHOD.
     */
    public static final MethodDescriptor LOOKUP_FIND_STATIC_GETTER_METHOD = MethodDescriptor.of(LookupVar.OWNER, "findStaticGetter", LookupVar.FIND_XETTER_DESC);
    /**
     * The constant LOOKUP_FIND_SETTER_METHOD.
     */
    public static final MethodDescriptor LOOKUP_FIND_SETTER_METHOD = MethodDescriptor.of(LookupVar.OWNER, "findSetter", LookupVar.FIND_XETTER_DESC);
    /**
     * The constant LOOKUP_FIND_STATIC_SETTER_METHOD.
     */
    public static final MethodDescriptor LOOKUP_FIND_STATIC_SETTER_METHOD = MethodDescriptor.of(LookupVar.OWNER, "findStaticSetter", LookupVar.FIND_XETTER_DESC);
    /**
     * The constant LOOKUP_FIND_FINDVIRTUAL.
     */
    public static final MethodDescriptor LOOKUP_FIND_FINDVIRTUAL = MethodDescriptor.of(LookupVar.OWNER, "findVirtual", LookupVar.FIND_VIRTUAL);
    /**
     * The constant LOOKUP_FIND_FINDSPECIAL.
     */
    public static final MethodDescriptor LOOKUP_FIND_FINDSPECIAL = MethodDescriptor.of(LookupVar.OWNER, "findSpecial", LookupVar.FIND_SPECIAL);
    /**
     * The constant METHOD_TYPE.
     */
    public static final MethodDescriptor METHOD_TYPE = MethodDescriptor.of("java/lang/invoke/MethodType", "methodType", "(Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;");
    /**
     * The constant ARRAYS_ASLIST.
     */
    public static final MethodDescriptor ARRAYS_ASLIST = MethodDescriptor.of("java/util/Arrays", "asList", "([Ljava/lang/Object;)Ljava/util/List;");
    /**
     * The constant DEFAULT_PROVIDER_GET_TARGET.
     */
    public static final MethodDescriptor DEFAULT_PROVIDER_GET_TARGET = MethodDescriptor.of("io/github/hhy50/linker/define/provider/DefaultTargetProviderImpl", "getTarget", "()Ljava/lang/Object;");
    /**
     * The constant LINKER_FACTORY_CREATE_LINKER.
     */
    public static final MethodDescriptor LINKER_FACTORY_CREATE_LINKER = MethodDescriptor.of("io/github/hhy50/linker/LinkerFactory", "createLinker", "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;");
    /**
     * The constant GET_CLASS_LOADER.
     */
    public static final MethodDescriptor GET_CLASS_LOADER = MethodDescriptor.of("java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;");

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
    public MethodDescriptor(String owner, String methodName, String methodDesc) {
        this.owner = owner;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    /**
     * Of method descriptor.
     *
     * @param owner      the owner
     * @param methodName the method name
     * @param methodDesc the method desc
     * @return the method descriptor
     */
    public static MethodDescriptor of(String owner, String methodName, String methodDesc) {
        return new MethodDescriptor(owner, methodName, methodDesc);
    }

    /**
     * Of method descriptor.
     * @param methodName
     * @param methodDesc
     * @return
     */
    public static MethodDescriptor of(String methodName, String methodDesc) {
        return new MethodDescriptor(null, methodName, methodDesc);
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
