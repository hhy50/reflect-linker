package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.asm.MethodBuilder;
import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.ArgsDepAnalysis;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

public class MethodExprInvoker extends Invoker<MethodExprRef> {

    List<Invoker<?>> invokers = new ArrayList<>();

    ArgsDepAnalysis argsDepAnalysis = new ArgsDepAnalysis();

    public MethodExprInvoker(MethodExprRef methodExprRef) {
        super(methodExprRef, Type.getMethodType(ObjectVar.TYPE, methodExprRef.getMethodType().getArgumentTypes()));
        for (MethodRef ref : this.method.getMethods()) {
            invokers.add(ref.defineInvoker());
        }
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        MethodBuilder methodBuilder = classImplBuilder.defineMethod(Opcodes.ACC_PUBLIC, this.descriptor.getMethodName(), descriptor.getType(), null);
        MethodBody methodBody = methodBuilder.getMethodBody();
        for (Invoker<?> invoker : invokers) {
            invoker.define(classImplBuilder);
            methodBody.append(
                    ChainAction.of(invoker::invoke).andThen(Actions.areturn(ObjectVar.TYPE))
            );
        }
        methodBody.end();
    }
}
