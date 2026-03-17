package io.github.hhy50.linker.define.parameter;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import io.github.hhy50.linker.generate.invoker.Getter;
import io.github.hhy50.linker.token.ArgsToken;

public interface ParameterLoader {

    /**
     *
     * @param mh
     * @param argsChainAction
     * @return
     */
    public ChainAction<VarInst[]> loadStepArgs(MethodHandle mh, ChainAction<VarInst[]> argsChainAction);

    /**
     *
     * @return
     */
    public ArgsToken getArgsToken();


    public static final ParameterLoader DEFAULT = new  ParameterLoader() {
        @Override
        public ChainAction<VarInst[]> loadStepArgs(MethodHandle mh, ChainAction<VarInst[]> argsChainAction) {
            if (mh instanceof Getter) {
                return ChainAction.empty();
            }
            return ChainAction.of(MethodBody::getArgs);
        }

        @Override
        public ArgsToken getArgsToken() {
            return ArgsToken.ALL;
        }
    };
}
