package io.github.hhy50.linker.token;

/**
 * The type Placeholder token.
 */
public class PlaceholderToken implements Token {

    /**
     * The Index.
     */
    public int index;

    /**
     * Instantiates a new Placeholder token.
     *
     * @param index the index
     */
    public PlaceholderToken(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public Kind kind() {
        return Kind.Placeholder;
    }

    @Override
    public String toString() {
        return "$"+index;
    }
}
