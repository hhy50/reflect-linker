package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;

public class ExprMethodInvoker extends Invoker<MethodExprRef> {

    public ExprMethodInvoker(String className, MethodExprRef methodRef) {
        super(className, methodRef, methodRef.getMethodType());
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        for (MethodRef methodMethod : method.getMethods()) {
            Invoker<?> invoker = classImplBuilder.defineInvoker(methodMethod);
            FieldRef owner = methodMethod.getOwner();
            if (owner != null) {
                classImplBuilder.defineGetter(owner.getUniqueName(), owner);
            }
            invoker.define(classImplBuilder);
        }
        super.define0(classImplBuilder);
    }
}
