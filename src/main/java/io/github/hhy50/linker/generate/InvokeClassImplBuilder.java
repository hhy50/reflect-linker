package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.asm.AsmField;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import io.github.hhy50.linker.generate.getter.TargetFieldGetter;
import io.github.hhy50.linker.generate.invoker.Getter;
import io.github.hhy50.linker.generate.invoker.Setter;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

import static io.github.hhy50.linker.util.TypeUtil.METHOD_HANDLER_TYPE;

/**
 * The type Invoke class impl builder.
 */
public class InvokeClassImplBuilder extends AsmClassBuilder {
    private TargetFieldGetter targetGetter;
    private final Map<String, Getter> getters;
    private final Map<String, Setter> setters;

    /**
     * Instantiates a new Invoke class impl builder.
     *
     * @param access     the access
     * @param className  the class name
     * @param superName  the super name
     * @param interfaces the interfaces
     * @param signature  the signature
     */
    public InvokeClassImplBuilder(int access, String className, String superName, String[] interfaces, String signature) {
        super(access, className, superName, interfaces, signature);
        this.getters = new HashMap<>();
        this.setters = new HashMap<>();

        this.defineConstruct(Opcodes.ACC_PUBLIC, Object.class)
                .intercept(Methods.invokeSuper().thenReturn());
    }

    /**
     * builder a new InvokeClassImplBuilder
     *
     * @param access     the access
     * @param className  the class name
     * @param superName  the super name
     * @param interfaces the interfaces
     * @param sign       the sign
     * @return invoke class impl builder
     */
    public static InvokeClassImplBuilder builder(int access, String className, String superName, String[] interfaces, String sign) {
        return new InvokeClassImplBuilder(access, className, superName, interfaces, sign);
    }

    /**
     * Sets target.
     *
     * @param targetGetter the target
     * @return the target
     */
    public InvokeClassImplBuilder setTarget(TargetFieldGetter targetGetter) {
        this.targetGetter = targetGetter;
        this.targetGetter.define(this);
        return this;
    }

    /**
     * Define static method handle method handle member.
     *
     * @param fullName   the fullName
     * @param lookupType the method invokedType
     * @param methodType the method type
     * @return the method handle member
     */
    public MethodHandleMember defineStaticMethodHandle(String fullName, Type lookupType, Type methodType) {
        fullName = fullName.replace('.', '_') + "_mh";
        AsmField field = getField(fullName);
        if (field == null) {
            int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
            field = super.visitField(access, fullName, METHOD_HANDLER_TYPE, null, null);
        }
        return new MethodHandleMember(field, lookupType, methodType);
    }

    /**
     * Define method handle method handle member.
     *
     * @param fullName
     * @param methodType
     * @return
     */
    public MethodHandleMember defineMethodHandle(String fullName, Type methodType) {
        fullName = fullName.replace('.', '_');
        AsmField field = getField(fullName);
        if (field == null) {
            field = super.visitField(Opcodes.ACC_PUBLIC, fullName, METHOD_HANDLER_TYPE, null, null);
        }
        return new MethodHandleMember(field, methodType);
    }

    /**
     * Define lookup class class type member.
     *
     * @param fullName the fullName
     * @return the class type member
     */
    public ClassTypeMember defineLookupClass(String fullName) {
        fullName = fullName.replace('.', '_') + "_lookup_$_class_type";
        AsmField field = getField(fullName);
        if (field == null) {
            field = super.visitField(Opcodes.ACC_PUBLIC, fullName, TypeUtil.CLASS_TYPE, null, null);
        }
        return new ClassTypeMember(field);
    }

    public TargetFieldGetter getTargetGetter() {
        return targetGetter;
    }


    Map<String, MethodHandle> cache = new HashMap<>();
    public MethodHandle defineInvoker(MethodRef methodRef) {
        String k = "method:"+methodRef.getFullName();
        if (cache.containsKey(k)) {
            return cache.get(k);
        }
        MethodHandle mh = methodRef.defineInvoker();
        mh.define(this);
        cache.put(k, mh);
        return mh;
    }
}
