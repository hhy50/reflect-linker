package io.github.hhy50.linker.generate.invoker;

import io.github.hhy50.linker.asm.AsmUtil;
import io.github.hhy50.linker.asm.MethodBuilder;
import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.define.method.MethodRef;
import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.SmartMethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.getter.TargetFieldGetter;
import io.github.hhy50.linker.runtime.RuntimeUtil;
import io.github.hhy50.linker.token.ArgsToken;
import io.github.hhy50.linker.token.ConstToken;
import io.github.hhy50.linker.token.PlaceholderToken;
import io.github.hhy50.linker.token.Token;
import io.github.hhy50.linker.util.RandomUtil;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * The type Method expr invoker.
 */
public class MethodExprInvoker extends Invoker<MethodExprRef> {
    private final Type methodType;
    private final String methodName;
    private final List<MethodRef> stepMethods;

    /**
     * Instantiates a new Invoker.
     *
     * @param mr the method
     */
    public MethodExprInvoker(MethodExprRef mr) {
        super(mr.getName(), null);
        this.stepMethods = mr.getStepMethods();
        this.methodType = mr.getMethodType();
        this.methodName = "invoke_"+mr.getName()+"_expr_" + RandomUtil.getRandomString(5);
    }

    @Override
    protected void define0(InvokeClassImplBuilder classImplBuilder) {
        TargetFieldGetter target = classImplBuilder.getTargetGetter();
        MethodBuilder builder = classImplBuilder
                .defineMethod(Opcodes.ACC_PUBLIC, methodName, methodType, null);
        BiFunction<ChainAction<VarInst>, ChainAction<VarInst[]>, Action> invoker = (varInstChain, argsChainAction) -> {
            for (MethodRef methodRef : stepMethods) {
                MethodHandle mh = methodRef.defineInvoker();
                mh.define(classImplBuilder);

                ChainAction<VarInst[]> stepArgsChain = loadStepArgs(mh, methodRef, argsChainAction)
                        .map(varInsts -> {
                            if (varInsts != null) {
                                Type mType = methodRef.getGenericType();
                                Type[] argsType = mType.getArgumentTypes();
                                for (int i = 0; i < varInsts.length; i++) {
                                    varInsts[i] = typeCast(varInsts[i], argsType[i]);
                                }
                            }
                            return varInsts;
                        });

                varInstChain = mh.invoke(ChainAction.join(varInstChain, stepArgsChain)).mapBody((body, varInst) -> {
                    Type t = varInst.getType();
                    boolean nullable = methodRef.isNullable();
                    List<Object> indexs = methodRef.getIndexs();

                    // 数组访问
                    if (indexs != null && !indexs.isEmpty()) {
                        if (TypeUtil.getDimensions(t) >= indexs.size()) {
                            varInst = new ArrayIndexAction(varInst, indexs);
                        } else {
                            varInst = Methods.invoke(RuntimeUtil.INDEX_VALUE)
                                    .setArgs(varInst, Actions.asList(indexs.stream().map(LdcLoadAction::of)
                                            .map(BoxAction::new)
                                            .toArray(Action[]::new)));
                        }
                        t = varInst.getType();
                    }
                    if (nullable && t.getSort() != Type.VOID && !TypeUtil.isPrimitiveType(t)) {
                        varInst = Actions.newLocalVar(varInst);
                        body.append(new ConditionJumpAction(
                                Condition.isNull(varInst),
                                defaultReturnAction(),
                                null
                        ));
                    }
                    return varInst;
                });
            }
            return varInstChain.areturn();
        };
        builder.intercept(invoker.apply(target.invoke(ChainAction.empty()), ChainAction.of(MethodBody::getArgs)));
    }

    public ChainAction<VarInst> invoke(ChainAction<VarInst[]> argsAction) {
        return ChainAction.of(() -> new SmartMethodInvokeAction(new SmartMethodDescriptor(methodName, methodType))
                .setInstance(LoadAction.LOAD0)
                .setArgs(argsAction));
    }

    private ChainAction<VarInst[]> loadStepArgs(MethodHandle mh, MethodRef methodRef, ChainAction<VarInst[]> argsChainAction) {
        if (mh instanceof Getter) {
            return ChainAction.empty();
        }

        ArgsToken argsToken = methodRef.getArgsToken();
        if (argsToken == null || argsToken.isPlaceholderAll()) {
            return ChainAction.of(MethodBody::getArgs);
        }

        return argsChainAction.map(varInsts -> {
            List<VarInst> args = new ArrayList<>();
            for (Token token : argsToken) {
                switch (token.kind()) {
                    // TODO
                    case Field:
                    case Method:
                        break;
                    case StrConst:
                    case IntConst:
                        args.add(LdcLoadAction.of(((ConstToken) token).getValue()));
                        break;
                    case Placeholder:
                        args.add(varInsts[((PlaceholderToken) token).index]);
                        break;
                }
            }
            return args.toArray(new VarInst[0]);
        });

    }

    /**
     * Gets method type.
     *
     * @return the method type
     */
    public Type getMethodType() {
        return methodType;
    }

    private Action defaultReturnAction() {
        Type returnType = methodType.getReturnType();
        return Actions.withVisitor(mv -> {
            switch (returnType.getSort()) {
                case Type.VOID:
                    mv.visitInsn(Opcodes.RETURN);
                    return;
                case Type.BOOLEAN:
                case Type.BYTE:
                case Type.CHAR:
                case Type.SHORT:
                case Type.INT:
                    mv.visitInsn(Opcodes.ICONST_0);
                    break;
                case Type.LONG:
                    mv.visitInsn(Opcodes.LCONST_0);
                    break;
                case Type.FLOAT:
                    mv.visitInsn(Opcodes.FCONST_0);
                    break;
                case Type.DOUBLE:
                    mv.visitInsn(Opcodes.DCONST_0);
                    break;
                default:
                    mv.visitInsn(Opcodes.ACONST_NULL);
                    break;
            }
            AsmUtil.areturn(mv, returnType);
        });
    }
}
