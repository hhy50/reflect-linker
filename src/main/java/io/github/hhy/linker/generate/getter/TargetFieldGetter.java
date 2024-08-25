package io.github.hhy.linker.generate.getter;

import io.github.hhy.linker.define.field.EarlyFieldRef;
import io.github.hhy.linker.entity.FieldHolder;
import io.github.hhy.linker.generate.InvokeClassImplBuilder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.action.FieldLoadAction;
import io.github.hhy.linker.generate.bytecode.action.LoadAction;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.util.ClassUtil;


public class TargetFieldGetter extends Getter<EarlyFieldRef> {

    private final FieldHolder targetField;

    public TargetFieldGetter(String implClass, EarlyFieldRef targetFieldRef) {
        super(implClass, targetFieldRef);
        this.targetField = new FieldHolder(ClassUtil.className2path(implClass), field.getFullName(), ObjectVar.TYPE.getDescriptor());
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        return methodBody.newLocalVar(field.getType(), field.fieldName,
                new FieldLoadAction(targetField).setInstance(LoadAction.LOAD0)
        );
    }
}
