package io.github.hhy50.linker.generate.bytecode.action;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The type Abstract chain.
 *
 * @param <In>  the type parameter
 * @param <Out> the type parameter
 */
public abstract class AbstractChain<In, Out> implements Action {

    /**
     * The Func.
     */
    protected Function<In, Out> func;

    /**
     * The Consumers.
     */
    protected List<Consumer<Out>> consumers;

    /**
     * Instantiates a new Abstract chain.
     *
     * @param func the func
     */
    protected AbstractChain(Function<In, Out> func) {
        this.func = func;
    }

    /**
     * Do chain out.
     *
     * @param in the in
     * @return the out
     */
    public Out doChain(In in) {
        Out o = func.apply(in);
        if (consumers != null) {
            for (Consumer<Out> consumer : consumers) {
                consumer.accept(o);
            }
        }
        return o;
    }

    /**
     * Add consumer.
     *
     * @param consumer the consumer
     */
    public void addConsumer(Consumer<Out> consumer) {
        if (consumers == null) {
            consumers = new ArrayList<>();
        }
        consumers.add(consumer);
    }
}
