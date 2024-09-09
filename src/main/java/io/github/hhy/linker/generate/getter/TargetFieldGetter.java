package io.github.hhy.linker.generate.getter;

import io.github.hhy.linker.define.field.EarlyFieldRef;
import io.github.hhy.linker.entity.FieldHolder;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.generate.bytecode.MethodHandleMember;
import io.github.hhy.linker.generate.bytecode.action.FieldLoadAction;
import io.github.hhy.linker.generate.bytecode.action.LoadAction;
import io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.util.AnnotationUtils;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


public class TargetFieldGetter extends Getter<EarlyFieldRef> {
    private final Class<?> defineClass;
    private final FieldHolder targetField;
    private MethodHolder getTarget;

    public TargetFieldGetter(String implClass, Class<?> defineClass, EarlyFieldRef targetFieldRef) {
        super(implClass, targetFieldRef);
        this.defineClass = defineClass;
        this.targetField = new FieldHolder(ClassUtil.className2path(implClass), field.getUniqueName(), ObjectVar.TYPE.getDescriptor());
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        Type targetType = Type.getType(field.getClassType());
        if (!AnnotationUtils.isRuntime(defineClass)) {
            MethodBody clinit = classImplBuilder.getClinit();

            this.lookupMember = classImplBuilder.defineTypedLookup(targetType.getClassName());
            this.lookupMember.setTargetLookup(true);
            this.lookupMember.staticInit(clinit, new DynamicClassLoad(targetType));
        } else {
            this.lookupMember = classImplBuilder.defineRuntimeLookup(field);
            this.lookupMember.setTargetLookup(true);

            Type mType = Type.getMethodType(ObjectVar.TYPE);
            classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, "getTarget", mType.getDescriptor(), null, null)
                    .accept(mv -> {
                        MethodBody methodBody = new MethodBody(classImplBuilder, mv, mType);
                        VarInst targetVar = methodBody.newLocalVar(mType.getReturnType(), new DynamicClassLoad(targetType));
                        checkLookup(methodBody, lookupMember, null, targetVar);
                        targetVar.returnThis(methodBody);
                    });
            this.getTarget = new MethodHolder(this.targetField.getOwner(), "getTarget", mType.getDescriptor());
        }
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        if (AnnotationUtils.isRuntime(defineClass)) {
            return methodBody.newLocalVar(ObjectVar.TYPE, "target", new MethodInvokeAction(getTarget).setInstance(LoadAction.LOAD0));
        }
        return methodBody.newLocalVar(field.getType(), field.fieldName,
                new FieldLoadAction(targetField).setInstance(LoadAction.LOAD0)
        );
    }

    @Override
    protected void checkLookup(MethodBody methodBody, LookupMember lookupMember, MethodHandleMember _1_, VarInst varInst) {
        varInst.ifNull(methodBody,
                (__) -> lookupMember.reinit(methodBody, getClassLoadAction(Type.getType(field.getClassType()))),
                (__) -> lookupMember.reinit(methodBody, varInst.getThisClass()));
    }
}
