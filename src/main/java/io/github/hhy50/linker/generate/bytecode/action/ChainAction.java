package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * @param <T>
 */
public class ChainAction<T> implements Action {

    protected Function<MethodBody, T> func;
    protected List<Consumer<T>> consumers;
    protected Next<T, ?> next;

    public ChainAction(Function<MethodBody, T> func) {
        this.func = func;
    }

    @Override
    public final void apply(MethodBody body) {
        T r = func.apply(body);
        if (consumers != null) {
            for (Consumer<T> consumer : consumers) {
                consumer.accept(r);
            }
        }
    }

    public ChainAction<T> peek(Consumer<T> consumer) {
        if (consumers == null) {
            consumers = new ArrayList<>();
        }
        consumers.add(consumer);
        return this;
    }

    public static <T> ChainAction<T> of(Function<MethodBody, T> bifunc) {
        return new ChainAction<>(bifunc);
    }

    public <R> ChainAction<R> then(BiFunction<T, MethodBody, R> func) {
//        return new ChainAction<>(body -> func.apply(r, body));
    }

    static class Next<T, R> {

        private BiFunction<T, MethodBody, R> bifunc;

        private Next(BiFunction<T, MethodBody, R> bifunc) {
            this.bifunc = bifunc;
        }
    }

    public static void main(String[] args) {
        class User {

        }

        class User2 {

        }

        ChainAction<User> chainAction = ChainAction.of(body -> new User());
        chainAction.peek(System.out::println);
        chainAction.peek(System.out::println);
        chainAction.peek(System.out::println);
        chainAction.peek(System.out::println);

        ChainAction<User2> newChain = chainAction.then((user, body) -> new User2());
        newChain = chainAction.then((user, body) -> {
            System.out.println(1234);
            return new User2();
        });
        newChain = chainAction.then((user, body) -> {
            System.out.println(4567);
            return new User2();
        });
        newChain = chainAction.then((user, body) -> {
            System.out.println(90);
            return new User2();
        });
        newChain = chainAction.then((user, body) -> {
            System.out.println(1);
            return new User2();
        });

        newChain.apply(null);
    }

}
