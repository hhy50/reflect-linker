package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;


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
     * Peek chain action.
     *
     * @param consumer the consumer
     * @return the chain action
     */
    public final ChainAction<T> then(Consumer<T> consumer) {
        addConsumer((body, val) -> {
            consumer.accept(val);
        });
        return this;
    }

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
