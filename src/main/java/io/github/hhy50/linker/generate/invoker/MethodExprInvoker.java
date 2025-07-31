package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.asm.MethodBuilder;
import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;

public class MethodExprInvoker extends Invoker<MethodExprRef> {

    public MethodExprInvoker(MethodExprRef methodExprRef) {
        super(methodExprRef, methodExprRef.getMethodType());
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        MethodBuilder methodBuilder = classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, this.descriptor.getMethodName(), descriptor.getType(), null);
        MethodBody methodBody = methodBuilder.getMethodBody();

        List<MethodRef> methods = method.getStatement();

        Invoker<?> first = methods.get(0).defineInvoker();
        Type curType = first.descriptor.getReturnType();
        first.define(classImplBuilder);

        ChainAction<VarInst> chain = ChainAction.of(first::invoke);
        for (int i = 1; i < methods.size(); i++) {
            ChainInvoker chainInvoker = new ChainInvoker(curType, methods.get(i));
            chainInvoker.define(classImplBuilder);
            chain = chain
                    .map(chainInvoker::invokeNext);
        }
        methodBody.append(chain.then((body, varInst) -> {
            if (varInst != null) {
                varInst.returnThis();
            } else {
                body.append(Actions.vreturn());
            }
        }));
        methodBody.end();
    }
}
