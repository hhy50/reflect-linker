package io.github.hhy50.linker.generate.bytecode.utils;

import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.LazyTypedAction;
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
            VarInst[] args = body.getArgs();
            int[] indices = argIndices;
            if (argIndices.length == 0) {
                indices = IntStream.range(0, args.length).toArray();
            }
            for (int i = 0; i < indices.length; i++) {
                body.append(args[indices[i]]);
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
                    VarInst arg = body.getArgs()[index];
                    body.append(arg);
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
     * The interface Load args action.
     */
    public static abstract class DynamicArgLoadAction extends VarInst implements LazyTypedAction {

    }
}
