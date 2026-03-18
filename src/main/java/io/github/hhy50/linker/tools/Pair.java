package io.github.hhy50.linker.tools;

/**
 * The type Pair.
 *
 * @param <T> the type parameter
 * @param <E> the type parameter
 */
public class Pair<T, E> {
    /**
     * The First.
     */
    public T first;
    /**
     * The Second.
     */
    public E second;

    /**
     * Instantiates a new Pair.
     *
     * @param first  the first
     * @param second the second
     */
    public Pair(T first, E second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Of pair.
     *
     * @param <T> the type parameter
     * @param <E> the type parameter
     * @param f   the f
     * @param s   the s
     * @return the pair
     */
    public  static <T, E> Pair<T, E> of(T f, E s) {
        return new Pair<>(f, s);
    }

    /**
     * Gets first.
     *
     * @return the first
     */
    public T getFirst() {
        return first;
    }

    /**
     * Sets first.
     *
     * @param first the first
     */
    public void setFirst(T first) {
        this.first = first;
    }

    /**
     * Gets second.
     *
     * @return the second
     */
    public E getSecond() {
        return second;
    }

    /**
     * Sets second.
     *
     * @param second the second
     */
    public void setSecond(E second) {
        this.second = second;
    }
}
