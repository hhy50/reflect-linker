package io.github.hhy50.linker.token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Index token.
 */
public class IndexToken implements Token {

    /**
     * The Index.
     */
    public List<ConstToken> index;

    /**
     * Instantiates a new Index token.
     *
     * @param index the index
     */
    public IndexToken(List<ConstToken> index) {
        this.index = index;
    }

    @Override
    public String toString() {
        String collect = index.stream().map(Object::toString).collect(Collectors.joining("]["));
        return "["+collect+"]";
    }

    @Override
    public String value() {
        return "";
    }
}
