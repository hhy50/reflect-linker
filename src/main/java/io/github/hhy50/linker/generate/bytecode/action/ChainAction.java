package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.block.CodeBlock;

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
    public void apply(CodeBlock block) {
        T t = doChain(block, block);
        if (t instanceof Action) {
            ((Action) t).apply(block);
        }
    }

    /**
     * Peek chain action.
     *
     * @param consumer the consumer
     * @return the chain action
     */
    public final ChainAction<T> peek(Consumer<T> consumer) {
        addConsumer((body, val) -> {
            consumer.accept(val);
        });
        return this;
    }

    /**
     * Peek chain action.
     *
     * @param consumer the consumer
     * @return the chain action
     */
    public final ChainAction<T> peek(BiConsumer<MethodBody, T> consumer) {
        addConsumer(consumer);
        return this;
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
    public <Out> ChainAction<Out> then(BiFunction<T, MethodBody, Out> func) {
        return new Next<>(this, func);
    }

    /**
     * Then chain action.
     *
     * @param <Out> the type parameter
     * @param func  the func
     * @return the chain action
     */
    public <Out> ChainAction<Out> then(Function<T, Out> func) {
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
        public Next(ChainAction<In> prevAction, BiFunction<In, MethodBody, Out> func) {
            super((body) -> {
                In in = prevAction.doChain(body, body);
                return func.apply(in, body);
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
