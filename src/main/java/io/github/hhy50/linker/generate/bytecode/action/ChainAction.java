package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Array;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;


/**
 * The type Chain action.
 *
 * @param <T> the type parameter
 */
public class ChainAction<T> extends AbstractChain<T> {

    static final ChainAction<?> EMPTY = new ChainAction<>(__ -> null);

    /**
     * Instantiates a new Chain action.
     *
     * @param func the func
     */
    public ChainAction(Function<MethodBody, T> func) {
        super(func);
    }


    @SuppressWarnings("unchecked")
    public static <T> ChainAction<T> empty() {
        return (ChainAction<T>) EMPTY;
    }

    /**
     * Join two chain actions into a VarInst array.
     * Optimized implementation that avoids unnecessary array copies.
     *
     * @param chain1 the first chain action
     * @param chain2 the second chain action
     * @return the joined chain action
     */
    public static ChainAction<VarInst[]> join(ChainAction<VarInst> chain1, ChainAction<VarInst[]> chain2) {
        return ChainAction.of(body -> {
            VarInst first = chain1.doChain(body);
            VarInst[] rest = chain2.doChain(body);

            if (rest == null || rest.length == 0) {
                return new VarInst[]{first};
            }

            VarInst[] result = new VarInst[rest.length + 1];
            result[0] = first;
            System.arraycopy(rest, 0, result, 1, rest.length);
            return result;
        });
    }

    /**
     * Join multiple VarInst chain actions into a VarInst array.
     *
     * @param chains the chain actions to join
     * @return the joined chain action
     */
    @SafeVarargs
    public static ChainAction<VarInst[]> joinAll(ChainAction<VarInst>... chains) {
        if (chains == null || chains.length == 0) {
            return ChainAction.of(() -> new VarInst[0]);
        }

        return ChainAction.of(body -> {
            VarInst[] result = new VarInst[chains.length];
            for (int i = 0; i < chains.length; i++) {
                result[i] = chains[i].doChain(body);
            }
            return result;
        });
    }

    /**
     * Join a VarInst array chain with another VarInst chain.
     *
     * @param chain1 the first chain action (array)
     * @param chain2 the second chain action (single)
     * @return the joined chain action
     */
    public static ChainAction<VarInst[]> append(ChainAction<VarInst[]> chain1, ChainAction<VarInst> chain2) {
        return ChainAction.of(body -> {
            VarInst[] first = chain1.doChain(body);
            VarInst last = chain2.doChain(body);

            if (first == null || first.length == 0) {
                return new VarInst[]{last};
            }

            VarInst[] result = Arrays.copyOf(first, first.length + 1);
            result[first.length] = last;
            return result;
        });
    }

    /**
     * Concatenate two VarInst array chains.
     *
     * @param chain1 the first chain action
     * @param chain2 the second chain action
     * @return the concatenated chain action
     */
    public static ChainAction<VarInst[]> concat(ChainAction<VarInst[]> chain1, ChainAction<VarInst[]> chain2) {
        return ChainAction.of(body -> {
            VarInst[] first = chain1.doChain(body);
            VarInst[] second = chain2.doChain(body);

            if (first == null || first.length == 0) {
                return second;
            }
            if (second == null || second.length == 0) {
                return first;
            }

            VarInst[] result = new VarInst[first.length + second.length];
            System.arraycopy(first, 0, result, 0, first.length);
            System.arraycopy(second, 0, result, first.length, second.length);
            return result;
        });
    }

    @Override
    public void apply(MethodBody body) {
        T t = doChain(body);

        if (t != null) {
            if (t instanceof Action) {
                ((Action) t).apply(body);
            }
           if (t.getClass().isArray()) {
               for (int i = 0; i < Array.getLength(t); i++) {
                   Object o = Array.get(t, i);
                   if (o instanceof Action) {
                       ((Action) o).apply(body);
                   }
               }
           }
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
     * @param <T>      the type parameter
     * @param provider the provider
     * @return the chain action
     */
    public static <T> ChainAction<T> of(Supplier<T> provider) {
        return new ChainAction<>((__) -> provider.get());
    }

    /**
     * Create a chain action from a constant value.
     *
     * @param <T>   the type parameter
     * @param value the constant value
     * @return the chain action
     */
    public static <T> ChainAction<T> constant(T value) {
        return new ChainAction<>((__) -> value);
    }

    public static ChainAction<VarInst> mapOwnerAndArgs(ChainAction<VarInst[]> chainAction, BiFunction<VarInst, VarInst[], VarInst> func) {
        return chainAction.map(args -> {
            if (args == null || args.length == 0) {
                return func.apply(null, new VarInst[0]);
            }

            VarInst owner = args[0];
            VarInst[] realArgs = new VarInst[args.length - 1];
            System.arraycopy(args, 1, realArgs, 0, realArgs.length);
            return func.apply(owner, realArgs);
        });
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
     * Peek at the value without transforming it.
     *
     * @param consumer the consumer
     * @return the chain action
     */
    public ChainAction<T> peek(Consumer<T> consumer) {
        addConsumer((body, val) -> consumer.accept(val));
        return this;
    }

    /**
     * Peek at the value with access to MethodBody.
     *
     * @param consumer the consumer
     * @return the chain action
     */
    public ChainAction<T> peekBody(BiFunction<MethodBody, T, Void> consumer) {
        addConsumer(consumer::apply);
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
     * FlatMap chain action.
     *
     * @param <Out> the type parameter
     * @param func  the func
     * @return the chain action
     */
    public <Out> ChainAction<Out> flatMap(Function<T, ChainAction<Out>> func) {
        return new ChainAction<>(body -> {
            T value = this.doChain(body);
            ChainAction<Out> nextChain = func.apply(value);
            return nextChain.doChain(body);
        });
    }

    /**
     * Then chain action.
     *
     * @param <Out> the type parameter
     * @param func  the func
     * @return the chain action
     */
    public <Out> ChainAction<Out> mapBody(BiFunction<MethodBody, T, Out> func) {
        return new Next<>(this, func);
    }

    /**
     * Map chain action.
     *
     * @param func the func
     * @return the chain action
     */
    public ChainAction<VarInst> mapVar(Function<T, TypedAction> func) {
        return new Next<>(this, varinst -> {
            TypedAction apply = func.apply(varinst);
            return Actions.newLocalVar(apply.getType(), apply);
        });
    }

    /**
     * Filter the value based on a predicate.
     *
     * @param predicate the predicate
     * @return the chain action
     */
    public ChainAction<T> filter(Predicate<T> predicate) {
        return new ChainAction<>(body -> {
            T value = this.doChain(body);
            return predicate.test(value) ? value : null;
        });
    }

    /**
     * Provide a default value if the result is null.
     *
     * @param defaultValue the default value
     * @return the chain action
     */
    public ChainAction<T> orElse(T defaultValue) {
        return new ChainAction<>(body -> {
            T value = this.doChain(body);
            return value != null ? value : defaultValue;
        });
    }

    /**
     * Provide a default value supplier if the result is null.
     *
     * @param supplier the supplier
     * @return the chain action
     */
    public ChainAction<T> orElseGet(Supplier<T> supplier) {
        return new ChainAction<>(body -> {
            T value = this.doChain(body);
            return value != null ? value : supplier.get();
        });
    }

    /**
     * Combine this chain with another chain using a combiner function.
     *
     * @param <U>      the type parameter
     * @param <R>      the result type parameter
     * @param other    the other chain action
     * @param combiner the combiner function
     * @return the chain action
     */
    public <U, R> ChainAction<R> combine(ChainAction<U> other, BiFunction<T, U, R> combiner) {
        return new ChainAction<>(body -> {
            T thisValue = this.doChain(body);
            U otherValue = other.doChain(body);
            return combiner.apply(thisValue, otherValue);
        });
    }

    /**
     * Zip this chain with another chain into a pair.
     *
     * @param <U>   the type parameter
     * @param other the other chain action
     * @return the chain action
     */
    public <U> ChainAction<Pair<T, U>> zip(ChainAction<U> other) {
        return combine(other, Pair::new);
    }

    /**
     * Execute an action and return this chain.
     *
     * @param action the action
     * @return the chain action
     */
    public ChainAction<T> andThen(Action action) {
        addConsumer((body, val) -> action.apply(body));
        return this;
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
                In in = prevAction.doChain(body);
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
                In in = prevAction.doChain(body);
                return func.apply(in);
            });
        }
    }

    /**
     * Simple pair class for zip operation.
     *
     * @param <L> the left type parameter
     * @param <R> the right type parameter
     */
    public static class Pair<L, R> {
        public final L left;
        public final R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public L getLeft() {
            return left;
        }

        public R getRight() {
            return right;
        }
    }
}
