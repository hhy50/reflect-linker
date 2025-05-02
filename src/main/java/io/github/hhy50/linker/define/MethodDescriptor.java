package io.github.hhy50.linker.define;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.define.provider.TargetProvider;
import io.github.hhy50.linker.generate.bytecode.vars.LookupVar;
import io.github.hhy50.linker.util.StringUtil;
import io.github.hhy50.linker.util.TypeUtils;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * The type Method holder.
 */
public class MethodDescriptor {

    /**
     * The constant OBJECT_GET_CLASS.
     */
    public static final MethodDescriptor GET_CLASS = MethodDescriptor.of("java/lang/Object", "getClass", Type.getMethodType(TypeUtils.CLASS_TYPE));
    /**
     * The constant LOOKUP_FIND_GETTER_METHOD.
     */
    public static final MethodDescriptor LOOKUP_FINDGETTER = MethodDescriptor.of(LookupVar.OWNER, "findGetter", LookupVar.FIND_XETTER_DESC);
    /**
     * The constant LOOKUP_FIND_STATIC_GETTER_METHOD.
     */
    public static final MethodDescriptor LOOKUP_FINDSTATICGETTER = MethodDescriptor.of(LookupVar.OWNER, "findStaticGetter", LookupVar.FIND_XETTER_DESC);
    /**
     * The constant LOOKUP_FIND_SETTER_METHOD.
     */
    public static final MethodDescriptor LOOKUP_FINDSETTER = MethodDescriptor.of(LookupVar.OWNER, "findSetter", LookupVar.FIND_XETTER_DESC);
    /**
     * The constant LOOKUP_FIND_STATIC_SETTER_METHOD.
     */
    public static final MethodDescriptor LOOKUP_FINDSTATICSETTER = MethodDescriptor.of(LookupVar.OWNER, "findStaticSetter", LookupVar.FIND_XETTER_DESC);
    /**
     * The constant LOOKUP_FIND_FINDVIRTUAL.
     */
    public static final MethodDescriptor LOOKUP_FINDVIRTUAL = MethodDescriptor.of(LookupVar.OWNER, "findVirtual", LookupVar.FIND_XXXXX);

    /**
     * The constant LOOKUP_FIND_FINDSTATIC.
     */
    public static final MethodDescriptor LOOKUP_FINDSTATIC = MethodDescriptor.of(LookupVar.OWNER, "findStatic", LookupVar.FIND_XXXXX);
    /**
     * The constant LOOKUP_FIND_FINDSPECIAL.
     */
    public static final MethodDescriptor LOOKUP_FINDSPECIAL = MethodDescriptor.of(LookupVar.OWNER, "findSpecial", LookupVar.FIND_SPECIAL);
    /**
     * The constant LOOKUP_FIND_CONSTRUCTOR.
     */
    public static final MethodDescriptor LOOKUP_FINDCONSTRUCTOR = MethodDescriptor.of(LookupVar.OWNER, "findConstructor", LookupVar.FIND_CONSTRUCTOR);
    /**
     * The constant METHOD_TYPE.
     */
    public static final MethodDescriptor METHOD_TYPE = MethodDescriptor.of(MethodType.class, "methodType",
            MethodType.class, Class.class, Class[].class);
    /**
     * The constant ARRAYS_ASLIST.
     */
    public static final MethodDescriptor ARRAYS_ASLIST = MethodDescriptor.of(Arrays.class, "asList",
            List.class, Object[].class);
    /**
     * The constant DEFAULT_PROVIDER_GET_TARGET.
     */
    public static final MethodDescriptor DEFAULT_PROVIDER_GET_TARGET = MethodDescriptor.of(TargetProvider.class, "getTarget",
            Object.class);
    /**
     * The constant LINKER_FACTORY_CREATE_LINKER.
     */
    public static final MethodDescriptor LINKER_FACTORY_CREATE_LINKER = MethodDescriptor.of(LinkerFactory.class, "createLinker",
            Object.class, Class.class, Object.class);

    /**
     * The constant LINKER_FACTORY_CREATE_LINKER_COLLECT.
     */
    public static final MethodDescriptor LINKER_FACTORY_CREATE_LINKER_COLLECT = MethodDescriptor.of(LinkerFactory.class, "createLinkerCollect",
            Collection.class, Class.class, Collection.class);

    /**
     * The constant LINKER_FACTORY_CREATE_STATIC_LINKER.
     */
    public static final MethodDescriptor LINKER_FACTORY_CREATE_STATIC_LINKER = MethodDescriptor.of(LinkerFactory.class, "createStaticLinker",
            Object.class, Class.class, Class.class);
    /**
     * The constant LINKER_FACTORY_CREATE_STATIC_LINKER_CLASSLOADER.
     */
    public static final MethodDescriptor LINKER_FACTORY_CREATE_STATIC_LINKER_CLASSLOADER = MethodDescriptor.of(LinkerFactory.class, "createStaticLinker",
            Object.class, Class.class, ClassLoader.class);
    /**
     * The constant GET_CLASS_LOADER.
     */
    public static final MethodDescriptor GET_CLASS_LOADER = MethodDescriptor.of(Class.class, "getClassLoader",
            ClassLoader.class);

    private final String owner;
    private final String name;
    private final Type type;

    /**
     * Instantiates a new Method holder.
     *
     * @param owner the owner
     * @param name  the method name
     * @param type  the method desc
     */
    public MethodDescriptor(String owner, String name, Type type) {
        if (StringUtil.isEmpty(owner) || StringUtil.isEmpty(name) || Objects.isNull(type)) {
            throw new IllegalArgumentException("owner or name or type can not be null");
        }
        this.owner = owner;
        this.name = name;
        this.type = type;
    }

    /**
     * of method descriptor.
     *
     * @param method the method
     * @return method descriptor
     */
    public static MethodDescriptor of(Method method) {
        return of(method.getDeclaringClass(), method.getName(), method.getReturnType(), method.getParameterTypes());
    }

    /**
     * of method descriptor.
     *
     * @param clazzName the clazz name
     * @param name      the method name
     * @param rType     the r type
     * @param argsType  the args type
     * @return method descriptor
     */
    public static MethodDescriptor of(Class<?> clazzName, String name, Class<?> rType, Class<?>... argsType) {
        return of(AsmUtil.toOwner(clazzName.getName()), name, TypeUtils.getMethodType(rType, argsType));
    }

    /**
     * of method descriptor.
     *
     * @param argsType the args type
     * @return method descriptor
     */
    public static MethodDescriptor ofConstructor(Class<?>... argsType) {
        return new SmartMethodDescriptor("<init>", TypeUtils.getMethodType(void.class, argsType));
    }

    /**
     * Of method descriptor.
     *
     * @param owner the owner
     * @param name  the method name
     * @param mType the method desc
     * @return the method descriptor
     */
    public static MethodDescriptor of(String owner, String name, Type mType) {
        return new MethodDescriptor(owner, name, mType);
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
        return name;
    }

    /**
     * Gets type.
     *
     * @return type
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return type.getDescriptor();
    }


    /**
     * Gets return type.
     *
     * @return return type
     */
    public Type getReturnType() {
        return this.type.getReturnType();
    }
}
