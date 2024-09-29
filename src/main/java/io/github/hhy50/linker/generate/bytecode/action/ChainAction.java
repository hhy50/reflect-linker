//package io.github.hhy50.linker.generate.bytecode.action;
//
//import io.github.hhy50.linker.generate.MethodBody;
//
//import java.util.List;
//import java.util.function.BiFunction;
//import java.util.function.Consumer;
//import java.util.function.Function;
//
//
///**
// * @param <T>
// */
//public class ChainAction<T, U> implements Action {
//
//    private BiFunction<?, MethodBody, T> prev;
//    private Function<MethodBody, U> func;
////    private Node head;
////    private Node tail;
//    private List<Consumer<T>> consumers;
////    private List<>
//    public ChainAction(Function<MethodBody, T> prev, Function<MethodBody, T> func) {
//        this.func = func;
//    }
//
//    public static <T, U> ChainAction<T, U> of(BiFunction<T, MethodBody, U> func) {
//        return new ChainAction<>(func);
//    }
//
//    @Override
//    public void apply(MethodBody body) {
//        T r = func.apply(body);
//        if (consumers != null) {
//            consumers.forEach(c -> c.accept(r));
//        }
//    }
//
//    public ChainAction<T, U> peek(Consumer<T> consumer) {
//        consumers.add(consumer);
//        return this;
//    }
//
//    public ChainAction<T, U> then(BiFunction<U, MethodBody, T> thenAction) {
//        return new ChainAction<>(this, thenAction);
//    }
//
//    static class Node {
//        private Node next;
//        private Object data;
//    }
//}
