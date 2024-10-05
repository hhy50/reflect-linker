package io.github.hhy50.linker.generate.bytecode.utils;

import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;

import java.util.stream.IntStream;

/**
 * The type Args.
 */
public class Args {
    /**
     * Load args action.
     *
     * @param argIndices the arg indices
     * @return the action
     */
    public static Action loadArgs(int... argIndices) {
        return body -> {
            VarInst[] args = body.getArgs();
            int[] indices = argIndices;
            if (argIndices.length == 0) {
                indices = IntStream.range(0, args.length).toArray();
            }
            for (int i = 0; i < indices.length; i++) {
                args[indices[i]].loadToStack();
            }
        };
    }

    /**
     * Of action.
     *
     * @param index the index
     * @return the action
     */
    public static Action of(int index) {
        return body -> {
            VarInst arg = body.getArgs()[index];
            arg.loadToStack();
        };
    }
}
