package io.github.hhy50.linker.token;


/**
 * The type Split token.
 */
public class SplitToken {
    /**
     * 前段
     */
    public Token prefix;

    /**
     * 后段
     */
    public Token suffix;

    /**
     * Instantiates a new Split token.
     *
     * @param prefix the prefix
     * @param suffix the suffix
     */
    public SplitToken(Token prefix, Token suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        if (prefix == null) {
            return suffix == null ? "" : suffix.toString();
        }
        if (suffix == null) {
            return prefix.toString();
        }
        return prefix + "." + suffix;
    }
}
