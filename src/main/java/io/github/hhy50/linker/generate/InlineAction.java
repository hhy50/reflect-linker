package io.github.hhy50.linker.generate;


import io.github.hhy50.linker.generate.bytecode.action.ChainAction;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;

/**
 * The interface Inline action.
 */
public interface InlineAction {

    /**
     * Invoke chain action.
     *
     * @param varInstChain    the var inst chain
     * @param argsChainAction the args chain action
     * @return the chain action
     */
    ChainAction<VarInst> invoke(ChainAction<VarInst> varInstChain, ChainAction<VarInst[]> argsChainAction);
}
