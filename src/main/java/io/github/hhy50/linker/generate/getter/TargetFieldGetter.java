package io.github.hhy50.linker.generate.getter;

import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.entity.FieldHolder;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.ClassTypeMember;
import io.github.hhy50.linker.generate.bytecode.action.ConditionJumpAction;
import io.github.hhy50.linker.generate.bytecode.action.FieldLoadAction;
import io.github.hhy50.linker.generate.bytecode.action.LoadAction;
import io.github.hhy50.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.util.AnnotationUtils;
import io.github.hhy50.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static io.github.hhy50.linker.generate.bytecode.action.Condition.isNull;
import static io.github.hhy50.linker.generate.bytecode.action.Condition.notNull;


/**
 * <p>TargetFieldGetter class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class TargetFieldGetter extends Getter<EarlyFieldRef> {
    private final Class<?> defineClass;
    private final FieldHolder targetField;
    private MethodHolder getTarget;

    /**
     * <p>Constructor for TargetFieldGetter.</p>
     *
     * @param implClass      a {@link java.lang.String} object.
     * @param defineClass    a {@link java.lang.Class} object.
     * @param targetFieldRef a {@link io.github.hhy50.linker.define.field.EarlyFieldRef} object.
     */
    public TargetFieldGetter(String implClass, Class<?> defineClass, EarlyFieldRef targetFieldRef) {
        super(implClass, targetFieldRef);
        this.defineClass = defineClass;
        this.targetField = new FieldHolder(ClassUtil.className2path(implClass), field.getUniqueName(), ObjectVar.TYPE.getDescriptor());
    }

    /** {@inheritDoc} */
    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        if (AnnotationUtils.isRuntime(defineClass)) {
            String mName = "getTarget";
            Type mType = Type.getMethodType(ObjectVar.TYPE);
            this.getTarget = new MethodHolder(this.targetField.getOwner(), mName, mType.getDescriptor());

            this.lookupClass = classImplBuilder.defineClassTypeMember(Opcodes.ACC_PUBLIC, field.getUniqueName()+"_lookup");
            classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, mName, mType.getDescriptor(), null, null)
                    .accept(mv -> {
                        MethodBody body = new MethodBody(classImplBuilder, mv, mType);
                        VarInst targetVar = body.newLocalVar(ObjectVar.TYPE, "target", new FieldLoadAction(targetField).setInstance(LoadAction.LOAD0));

                        checkLookClass(body, lookupClass, targetVar);
                        targetVar.returnThis(body);
                    });
        } else {
            this.lookupClass = classImplBuilder.defineClassTypeMember(field.getUniqueName()+"_lookup");
            this.lookupClass.store(classImplBuilder.getClinit(), getClassLoadAction(field.getType()));
        }
    }

    /** {@inheritDoc} */
    @Override
    public VarInst invoke(MethodBody methodBody) {
        if (this.getTarget != null) {
            return methodBody.newLocalVar(ObjectVar.TYPE, "target", new MethodInvokeAction(getTarget).setInstance(LoadAction.LOAD0));
        }
        return methodBody.newLocalVar(field.getType(), field.fieldName,
                new FieldLoadAction(targetField).setInstance(LoadAction.LOAD0)
        );
    }

    /** {@inheritDoc} */
    @Override
    protected void checkLookClass(MethodBody body, ClassTypeMember lookupClass, VarInst varInst) {
        body.append(() -> new ConditionJumpAction(
                isNull(lookupClass),
                new ConditionJumpAction(notNull(varInst),
                        (__) -> lookupClass.store(__, varInst.getThisClass()),
                        (__) -> lookupClass.store(__, getClassLoadAction(field.getType()))
                ),
                null
        ));
    }
}
