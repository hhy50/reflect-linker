package io.github.hhy.linker.generate.getter;

import io.github.hhy.linker.define.field.EarlyFieldRef;
import io.github.hhy.linker.entity.FieldHolder;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.action.FieldLoadAction;
import io.github.hhy.linker.generate.bytecode.action.LoadAction;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.util.AnnotationUtils;
import io.github.hhy.linker.util.ClassUtil;


public class TargetFieldGetter extends Getter<EarlyFieldRef> {

    private final boolean isRuntime;
    private final FieldHolder targetField;

    public TargetFieldGetter(String implClass, Class<?> defineClass, EarlyFieldRef targetFieldRef) {
        super(implClass, targetFieldRef);
        this.targetField = new FieldHolder(ClassUtil.className2path(implClass), field.getUniqueName(), ObjectVar.TYPE.getDescriptor());
        this.isRuntime = AnnotationUtils.isRuntime(defineClass);
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        if (!this.isRuntime) {
//            this.lookupMember = classImplBuilder.defineTypedLookup(field.getType());
//            this.lookupMember.setTargetLookup(true);
//            this.lookupMember.staticInit(classImplBuilder.getClinit());
        } else {
//            this.lookupMember = classImplBuilder.defineRuntimeLookup(field);
//            classImplBuilder
        }
        this.lookupMember = classImplBuilder.defineTypedLookup(field.getType());
        this.lookupMember.setTargetLookup(true);
        this.lookupMember.staticInit(classImplBuilder.getClinit());
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        return methodBody.newLocalVar(field.getType(), field.fieldName,
                new FieldLoadAction(targetField).setInstance(LoadAction.LOAD0)
        );
    }
}
