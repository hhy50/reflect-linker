package io.github.hhy50.linker.token;

/**
 * The interface Token.
 */
public interface Token {

    /**
     * Value string.
     *
     * @return the string
     */
    public String value();

    /**
     * Sets index.
     *
     * @param index the index
     */
    default void setIndex(IndexToken index) {

    }
}
