package io.github.hhy50.linker.generate.bytecode.action;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @param <In>
 * @param <Out>
 */
public abstract class AbstractChain<In, Out> implements Action {

    protected Function<In, Out> func;

    protected List<Consumer<Out>> consumers;

    protected AbstractChain(Function<In, Out> func) {
        this.func = func;
    }

    public Out doChain(In in) {
        Out o = func.apply(in);
        if (consumers != null) {
            for (Consumer<Out> consumer : consumers) {
                consumer.accept(o);
            }
        }
        return o;
    }

    public void addConsumer(Consumer<Out> consumer) {
        if (consumers == null) {
            consumers = new ArrayList<>();
        }
        consumers.add(consumer);
    }
}
