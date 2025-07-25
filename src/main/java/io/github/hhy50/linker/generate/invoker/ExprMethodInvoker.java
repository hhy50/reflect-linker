package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.Getter;

public class ExprMethodInvoker extends Invoker<MethodExprRef> {

    public ExprMethodInvoker(MethodExprRef methodRef) {
        super(methodRef, methodRef.getMethodType());
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        for (MethodRef methodMethod : method.getMethods()) {
            FieldRef owner = methodMethod.getOwner();
            if (owner != null) {
                Getter getter = classImplBuilder.defineGetter(owner.getUniqueName(), owner);
//                getter.defi
            }

            Invoker<?> invoker = classImplBuilder.defineInvoker(methodMethod);
            invoker.define(classImplBuilder);
        }
    }


    @Override
    public VarInst invoke(MethodBody methodBody) {
        return super.invoke(methodBody);
    }
}
