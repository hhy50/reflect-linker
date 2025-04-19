package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;
import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.define.method.RuntimeMethodRef;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.Member;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import io.github.hhy50.linker.generate.bytecode.vars.ClassVar;
import io.github.hhy50.linker.generate.getter.EarlyFieldGetter;
import io.github.hhy50.linker.generate.getter.Getter;
import io.github.hhy50.linker.generate.getter.RuntimeFieldGetter;
import io.github.hhy50.linker.generate.getter.TargetFieldGetter;
import io.github.hhy50.linker.generate.invoker.EarlyMethodInvoker;
import io.github.hhy50.linker.generate.invoker.Invoker;
import io.github.hhy50.linker.generate.invoker.RuntimeMethodInvoker;
import io.github.hhy50.linker.generate.setter.EarlyFieldSetter;
import io.github.hhy50.linker.generate.setter.RuntimeFieldSetter;
import io.github.hhy50.linker.generate.setter.Setter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashMap;
import java.util.Map;

import static io.github.hhy50.linker.util.TypeUtils.METHOD_HANDLER_TYPE;

/**
 * The type Invoke class impl builder.
 */
public class InvokeClassImplBuilder extends AsmClassBuilder {
    private Class<?> defineClass;
    private final Map<String, Getter<?>> getters;

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
     * Sets define class.
     *
     * @param defineClass the define class
     * @return define class
     */
    public InvokeClassImplBuilder setDefineClass(Class<?> defineClass) {
        this.defineClass = defineClass;
        return this;
    }

    /**
     * Gets define class.
     *
     * @return the define class
     */
    public Class<?> getDefineClass() {
        return defineClass;
    }

    /**
     * Sets target.
     *
     * @param bindTarget the bind target
     * @return the target
     */
    public InvokeClassImplBuilder setTarget(Class<?> bindTarget) {
        EarlyFieldRef targetField = new EarlyFieldRef(null, null, "target", bindTarget);
        TargetFieldGetter targetFieldGetter = new TargetFieldGetter(getClassName(), targetField);

        this.getters.put(targetField.getUniqueName(), targetFieldGetter);
        return this;
    }

    /**
     * Define getter getter.
     *
     * @param fieldName the field name
     * @param fieldRef  the field ref
     * @return the getter
     */
    public Getter<?> defineGetter(String fieldName, FieldRef fieldRef) {
        return getters.computeIfAbsent(fieldName, key -> {
            if (fieldRef instanceof EarlyFieldRef) {
                return new EarlyFieldGetter(getClassName(), (EarlyFieldRef) fieldRef);
            } else {
                return new RuntimeFieldGetter(getClassName(), (RuntimeFieldRef) fieldRef);
            }
        });
    }

    /**
     * Gets getter.
     *
     * @param fieldName the field name
     * @return the getter
     */
    public Getter<?> getGetter(String fieldName) {
        return getters.get(fieldName);
    }

    /**
     * Define setter setter.
     *
     * @param fieldName the field name
     * @param fieldRef  the field ref
     * @return the setter
     */
    public Setter<?> defineSetter(String fieldName, FieldRef fieldRef) {
        return fieldRef instanceof EarlyFieldRef ? new EarlyFieldSetter(getClassName(), (EarlyFieldRef) fieldRef)
                : new RuntimeFieldSetter(getClassName(), (RuntimeFieldRef) fieldRef);
    }

    /**
     * Define invoker invoker.
     *
     * @param methodRef the method ref
     * @return the invoker
     */
    public Invoker<?> defineInvoker(MethodRef methodRef) {
        return methodRef instanceof EarlyMethodRef ? new EarlyMethodInvoker(getClassName(), (EarlyMethodRef) methodRef)
                : new RuntimeMethodInvoker(getClassName(), (RuntimeMethodRef) methodRef);
    }

    /**
     * Define static method handle method handle member.
     *
     * @param mhMemberName the mh member name
     * @param invokedType  the method invokedType
     * @param methodType   the method type
     * @return the method handle member
     */
    public MethodHandleMember defineStaticMethodHandle(String mhMemberName, Type invokedType, Type methodType) {
        if (!members.containsKey(mhMemberName)) {
            int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
            super.defineField(access, mhMemberName, METHOD_HANDLER_TYPE, null, null);
        }
        return new MethodHandleMember(members.get(mhMemberName), invokedType, methodType);
    }

    /**
     * Define method handle method handle member.
     *
     * @param mhMemberName the mh member name
     * @param methodType   the method type
     * @return the method handle member
     */
    public MethodHandleMember defineMethodHandle(String mhMemberName, Type methodType) {
        if (!members.containsKey(mhMemberName)) {
            super.defineField(Opcodes.ACC_PUBLIC, mhMemberName, METHOD_HANDLER_TYPE, null, null);
        }
        return new MethodHandleMember(members.get(mhMemberName), methodType);
    }

    /**
     * Define lookup class class type member.
     *
     * @param mName the m name
     * @return the class type member
     */
    public ClassTypeMember defineLookupClass(String mName) {
        mName = mName+"_lookup_$_class_type";
        if (!members.containsKey(mName)) {
            Member member = super.defineField(Opcodes.ACC_PUBLIC, mName, ClassVar.TYPE, null, null);
            ClassTypeMember classTypeMember = new ClassTypeMember(member);
            this.members.put(mName, classTypeMember);
            return classTypeMember;
        }
        return (ClassTypeMember) members.get(mName);
    }
}
