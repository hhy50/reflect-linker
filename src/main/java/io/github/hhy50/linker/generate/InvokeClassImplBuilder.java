package io.github.hhy50.linker.generate;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.asm.AsmField;
import io.github.hhy50.linker.generate.bytecode.ClassTypeFieldVarInst;
import io.github.hhy50.linker.generate.bytecode.MethodHandleFieldVarInst;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import io.github.hhy50.linker.generate.getter.TargetFieldGetter;
import io.github.hhy50.linker.util.RandomUtil;
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
    private final Map<Object, MethodHandleFieldVarInst> mhFieldVarInsts = new HashMap<>();

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
     * Define static method handle method handle field.
     *
     * @param uniqueKey  the unique key
     * @param name       the name
     * @param lookupType the lookup type
     * @param methodType the method type
     * @return the method handle field
     */
    public MethodHandleFieldVarInst defineStaticMethodHandle(Object uniqueKey, String name, Type lookupType, Type methodType) {
        if (mhFieldVarInsts.containsKey(uniqueKey)) {
            return mhFieldVarInsts.get(uniqueKey);
        }

        String mhName = name + "_mh_" + RandomUtil.getRandomString(5);
        AsmField field = super.visitField(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL,
                mhName, METHOD_HANDLER_TYPE, null, null);
        MethodHandleFieldVarInst mhFieldVarInst = new MethodHandleFieldVarInst(field, methodType, lookupType);
        this.mhFieldVarInsts.put(uniqueKey, mhFieldVarInst);
        return mhFieldVarInst;
    }

    /**
     *
     * @param name
     * @param methodType
     * @return
     */
    public MethodHandleFieldVarInst defineMethodHandle(String name, Type methodType) {
        name = name + "_mh_" +  RandomUtil.getRandomString(5);
        return new MethodHandleFieldVarInst(super.visitField(Opcodes.ACC_PUBLIC, name, TypeUtil.METHOD_HANDLER_TYPE, null, null), methodType);
    }

    /**
     *
     * @param name name
     * @return
     */
    public ClassTypeFieldVarInst defineLookupClass(String name) {
        name = name + "_lookup_" +  RandomUtil.getRandomString(5);
        return new ClassTypeFieldVarInst(super.visitField(Opcodes.ACC_PUBLIC, name, TypeUtil.CLASS_TYPE, null, null));
    }

    /**
     * Gets target getter.
     *
     * @return the target getter
     */
    public TargetFieldGetter getTargetGetter() {
        return targetGetter;
    }
}
