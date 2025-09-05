package io.github.hhy50.linker.generate.invoker;

//public class MethodExprInvoker extends Invoker<MethodExprRef> {
//
//    public MethodExprInvoker(MethodExprRef methodExprRef) {
//        super(methodExprRef, methodExprRef.getMethodType());
//    }
//
//    @Override
//    protected void define0(InvokeClassImplBuilder classImplBuilder) {
//        MethodBuilder methodBuilder = classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, this.descriptor.getMethodName(), descriptor.getType(), null);
//        MethodBody methodBody = methodBuilder.getMethodBody();
//
//        List<MethodRef> methods = method.getStatement();
//
//        Invoker<?> first = methods.get(0).defineInvoker();
//        Type curType = first.descriptor.getReturnType();
//        first.define(classImplBuilder);
//
//        ChainAction<VarInst> chain = ChainAction.of(methodBody1 -> first.invoke(methodBody1));
//        for (int i = 1; i < methods.size(); i++) {
//            ChainInvoker chainInvoker = new ChainInvoker(curType, methods.get(i));
//            chainInvoker.define(classImplBuilder);
//            chain = chain
//                    .map(chainInvoker::invokeNext);
//        }
//        methodBody.append(chain.then((varInst) -> {
//            if (varInst != null) {
//                return varInst.thenReturn();
//            } else {
//                return Actions.vreturn();
//            }
//        }));
//        methodBody.end();
//    }
//}
