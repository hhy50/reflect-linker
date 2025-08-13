package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.Member;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.utils.Members;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.util.AnnotationUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


/**
 * The type Target field getter.
 */
public class TargetFieldGetter extends Getter {
    private final Member targetObj;

    private ClassTypeMember targetClass;

    /**
     *
     */
    private final Type targetType;

    /**
     * Instantiates a new Target field getter.
     *
     * @param targetFieldRef the target field ref
     */
    public TargetFieldGetter(EarlyFieldRef targetFieldRef) {
        super(targetFieldRef);
        this.targetObj = Members.of(targetFieldRef.fieldName, ObjectVar.TYPE);
        this.targetType = targetFieldRef.getType();
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        if (AnnotationUtils.isRuntime(classImplBuilder.getDefineClass())) {
            this.targetClass = classImplBuilder.defineLookupClass("target");
            classImplBuilder.defineConstruct(Opcodes.ACC_PUBLIC, Object.class, Class.class)
                    .intercept(Methods.invokeSuper(MethodDescriptor.ofConstructor(Object.class)).setArgs(Args.of(0))
                            .andThen(this.targetClass.store(Args.of(1)))
                            .andThen(Actions.vreturn()));
        } else {

        }
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        return this.targetObj;
    }

    /**
     * Gets target class.
     *
     * @return the target class
     */
    public ClassTypeMember getTargetClass() {
        return targetClass;
    }

    /**
     * Gets target type.
     *
     * @return the target type
     */
    public Type getTargetType() {
        return this.targetType;
    }
}
