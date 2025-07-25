package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;

public class ExprMethodInvoker2 extends MethodHandle {

    public ExprMethodInvoker2(MethodExprRef methodRef) {
    }

    @Override
    public VarInst invoke(MethodBody methodBody) {
        return null;
    }

//    @Override
//    protected void define0(InvokeClassImplBuilder classImplBuilder) {
//        for (MethodRef methodMethod : method.getMethods()) {
//            FieldRef owner = methodMethod.getOwner();
//            if (owner != null) {
//                Getter getter = classImplBuilder.defineGetter(owner.getUniqueName(), owner);
////                getter.defi
//            }
//
//            Invoker<?> invoker = classImplBuilder.defineInvoker(methodMethod);
//            invoker.define(classImplBuilder);
//        }
//    }
//
//
//    @Override
//    public VarInst invoke(MethodBody methodBody) {
//        return super.invoke(methodBody);
//    }


}
