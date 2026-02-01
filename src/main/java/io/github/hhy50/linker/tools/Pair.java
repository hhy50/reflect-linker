package io.github.hhy50.linker.tools;

public class Pair<T, E> {
    public T first;
    public E second;

    public Pair(T first, E second) {
        this.first = first;
        this.second = second;
    }

    public  static <T, E> Pair<T, E> of(T f, E s) {
        return new Pair<>(f, s);
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public E getSecond() {
        return second;
    }

    public void setSecond(E second) {
        this.second = second;
    }
}
