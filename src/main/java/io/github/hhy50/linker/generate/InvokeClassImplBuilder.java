package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.asm.AsmField;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import io.github.hhy50.linker.generate.getter.Getter;
import io.github.hhy50.linker.generate.getter.TargetFieldGetter;
import io.github.hhy50.linker.generate.setter.Setter;
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
    private Class<?> defineClass;
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
        EarlyFieldRef targetField = new EarlyFieldRef(null, "target", bindTarget);
        TargetFieldGetter targetFieldGetter = new TargetFieldGetter(targetField);

        this.getters.put(targetField.getUniqueName(), targetFieldGetter);
        return this;
    }

    /**
     * Define getter getter.
     *
     * @param fieldRef the field ref
     * @return the getter
     */
    public Getter defineGetter(FieldRef fieldRef) {
        return getters.computeIfAbsent(fieldRef.getUniqueName(), key -> {
            return new Getter(fieldRef);
        });
    }

    /**
     * Gets getter.
     *
     * @param fieldRef the field ref
     * @return the getter
     */
    public Getter getGetter(FieldRef fieldRef) {
        String uniqueName = fieldRef.getUniqueName();
        return getters.get(uniqueName);
    }

    /**
     * Define setter setter.
     *
     * @param fieldName the field name
     * @param fieldRef  the field ref
     * @return the setter
     */
    public Setter defineSetter(String fieldName, FieldRef fieldRef) {
        return setters.computeIfAbsent(fieldName, key -> {
            return new Setter(getClassName(), fieldRef);
        });
    }

    /**
     * Define static method handle method handle member.
     *
     * @param fieldName  the mh member name
     * @param lookupType the method invokedType
     * @param methodType the method type
     * @return the method handle member
     */
    public MethodHandleMember defineStaticMethodHandle(String fieldName, Type lookupType, Type methodType) {
        AsmField field = getField(fieldName);
        if (field == null) {
            int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
            field = super.visitField(access, fieldName, METHOD_HANDLER_TYPE, null, null);
        }
        return new MethodHandleMember(field, lookupType, methodType);
    }

    /**
     * Define method handle method handle member.
     *
     * @param fieldName  the mh member name
     * @param methodType the method type
     * @return the method handle member
     */
    public MethodHandleMember defineMethodHandle(String fieldName, Type methodType) {
        AsmField field = getField(fieldName);
        if (field == null) {
            field = super.visitField(Opcodes.ACC_PUBLIC, fieldName, METHOD_HANDLER_TYPE, null, null);
        }
        return new MethodHandleMember(field, methodType);
    }

    /**
     * Define lookup class class type member.
     *
     * @param mName the m name
     * @return the class type member
     */
    public ClassTypeMember defineLookupClass(String mName) {
        mName = mName+"_lookup_$_class_type";
        AsmField field = getField(mName);
        if (field == null) {
            field = super.visitField(Opcodes.ACC_PUBLIC, mName, TypeUtil.CLASS_TYPE, null, null);
        }
        return new ClassTypeMember(field);
    }
}
