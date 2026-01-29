package io.github.hhy50.linker.token;

/**
 * The type Placeholder token.
 */
public class AllPlaceholderToken implements Token {

    @Override
    public Kind kind() {
        return Kind.Placeholder;
    }

    @Override
    public String toString() {
        return "..";
    }
}
