package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * @param <T>
 */
public class ChainAction<T> extends AbstractChain<MethodBody, T> {

    public ChainAction(Function<MethodBody, T> func) {
        super(func);
    }

    @Override
    public void apply(MethodBody body) {
        T t = doChain(body);
        if (t instanceof Action) {
            ((Action) t).apply(body);
        }
    }

    public final ChainAction<T> peek(Consumer<T> consumer) {
        addConsumer(consumer);
        return this;
    }

    public static <T> ChainAction<T> of(Function<MethodBody, T> bifunc) {
        return new ChainAction<>(bifunc);
    }

    public <Out> ChainAction<Out> map(Function<T, Out> func) {
        return new Next<>(this, func);
    }

    public <Out> ChainAction<Out> then(BiFunction<T, MethodBody, Out> func) {
        return new Next<>(this, func);
    }

    public <Out> ChainAction<Out> then(Function<T, Out> func) {
        return new Next<>(this, func);
    }

    static class Next<In, Out> extends ChainAction<Out> {
        public Next(ChainAction<In> prevAction, BiFunction<In, MethodBody, Out> func) {
            super((body) -> {
                In in = prevAction.doChain(body);
                return func.apply(in, body);
            });
        }

        public Next(ChainAction<In> prevAction, Function<In, Out> func) {
            super((body) -> {
                In in = prevAction.doChain(body);
                return func.apply(in);
            });
        }
    }
}
