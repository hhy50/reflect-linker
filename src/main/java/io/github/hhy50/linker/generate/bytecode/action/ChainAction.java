package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * The type Chain action.
 *
 * @param <T> the type parameter
 */
public class ChainAction<T> extends AbstractChain<MethodBody, T> {

    /**
     * Instantiates a new Chain action.
     *
     * @param func the func
     */
    public ChainAction(Function<MethodBody, T> func) {
        super(func);
    }

    @Override
    public void apply(MethodBody body) {
        T t = doChain(body, body);
        if (t instanceof Action) {
            ((Action) t).apply(body);
        }
    }

    /**
     * Of chain action.
     *
     * @param <T>    the type parameter
     * @param bifunc the bifunc
     * @return the chain action
     */
    public static <T> ChainAction<T> of(Function<MethodBody, T> bifunc) {
        return new ChainAction<>(bifunc);
    }

    /**
     * Of chain action.
     *
     * @param <T>    the type parameter
     * @param provider the provider
     * @return the chain action
     */
    public static <T> ChainAction<T> of(Supplier<T> provider) {
        return new ChainAction<>((__) -> provider.get());
    }

    /**
     * Then chain action.
     *
     * @param function the function
     * @return the chain action
     */
    public final ChainAction<T> then(Function<T, Action> function) {
        addConsumer((body, val) -> {
            Action then = function.apply(val);
            if (then != null) then.apply(body);
        });
        return this;
    }

    /**
     * Then chain action.
     *
     * @param func the func
     * @return the chain action
     */
    public ChainAction<T> then(BiConsumer<MethodBody, T> func) {
        addConsumer(func);
        return this;
    }

    /**
     * Map chain action.
     *
     * @param <Out> the type parameter
     * @param func  the func
     * @return the chain action
     */
    public <Out> ChainAction<Out> map(Function<T, Out> func) {
        return new Next<>(this, func);
    }

    /**
     * Then chain action.
     *
     * @param <Out> the type parameter
     * @param func  the func
     * @return the chain action
     */
    public <Out> ChainAction<Out> map(BiFunction<MethodBody, T, Out> func) {
        return new Next<>(this, func);
    }

    /**
     * Map chain action.
     *
     * @param func  the func
     * @return the chain action
     */
    public ChainAction<VarInst> mapVar(Function<T, TypedAction> func) {
        return new Next<>(this, varinst -> {
            TypedAction apply = func.apply(varinst);
            return Actions.newLocalVar(apply.getType(), apply);
        });
    }


    /**
     * The type Next.
     *
     * @param <In>  the type parameter
     * @param <Out> the type parameter
     */
    static class Next<In, Out> extends ChainAction<Out> {
        /**
         * Instantiates a new Next.
         *
         * @param prevAction the prev action
         * @param func       the func
         */
        public Next(ChainAction<In> prevAction, BiFunction<MethodBody, In, Out> func) {
            super((body) -> {
                In in = prevAction.doChain(body, body);
                return func.apply(body, in);
            });
        }

        /**
         * Instantiates a new Next.
         *
         * @param prevAction the prev action
         * @param func       the func
         */
        public Next(ChainAction<In> prevAction, Function<In, Out> func) {
            super((body) -> {
                In in = prevAction.doChain(body, body);
                return func.apply(in);
            });
        }
    }
}
