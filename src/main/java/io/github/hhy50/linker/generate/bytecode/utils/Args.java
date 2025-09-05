package io.github.hhy50.linker.generate.bytecode.utils;

import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LazyTypedAction;
import io.github.hhy50.linker.generate.bytecode.vars.LocalVarInst;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

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
            LocalVarInst[] args = body.getArgs();
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
    public static VarInst of(int index) {
        return new DynamicArgLoadAction() {
            private Type type;

            @Override
            public Action load() {
                return body -> {
                    LocalVarInst arg = body.getArgs()[index];
                    arg.loadToStack();
                    this.type = arg.getType();
                };
            }

            @Override
            public Type getType() {
                return type;
            }
        };
    }

    /**
     * Load args ignore this action.
     *
     * @return the action
     */
    public static Action loadArgsIgnore0() {
        return body -> {
            LocalVarInst[] args = body.getArgs();
            for (int i = 1; i < args.length; i++) {
                args[i].loadToStack();
            }
        };
    }

    /**
     * The interface Load args action.
     */
    public static abstract class DynamicArgLoadAction extends VarInst implements LazyTypedAction {

    }
}
