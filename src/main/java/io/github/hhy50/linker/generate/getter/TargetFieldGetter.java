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
public class TargetFieldGetter extends Getter<EarlyFieldRef> {
    private final Member targetObj;

    private ClassTypeMember targetClass;

    /**
     * Instantiates a new Target field getter.
     *
     * @param implClass      the impl class
     * @param targetFieldRef the target field ref
     */
    public TargetFieldGetter(String implClass, EarlyFieldRef targetFieldRef) {
        super(implClass, targetFieldRef);
        this.targetObj = Members.of(targetFieldRef.fieldName, ObjectVar.TYPE);
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
        super.define0(classImplBuilder);
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        return methodBody.newLocalVar(ObjectVar.TYPE, field.fieldName, this.targetObj);
    }

    /**
     * Gets target class.
     *
     * @return the target class
     */
    public ClassTypeMember getTargetClass() {
        return targetClass;
    }

    public Type getTargetType() {
        return this.field.getType();
    }
}
