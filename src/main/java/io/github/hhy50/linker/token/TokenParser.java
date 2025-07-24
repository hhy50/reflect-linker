package io.github.hhy50.linker.token;


import io.github.hhy50.linker.exceptions.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The type Token parser.
 */
public class TokenParser {

    /**
     * The Empty.
     */
    public static Tokens EMPTY = new Tokens();

    /**
     * Parse tokens.
     *
     * @param tokensStr the tokens str
     * @return the tokens
     * @throws ParseException the parse exception
     */
    public Tokens parse(String tokensStr) throws ParseException {
        if (tokensStr == null || tokensStr.length() == 0) {
            return EMPTY;
        }

        Tokens tokens = new Tokens();
        ParserIter parser = new ParserIter(tokensStr);
        while (parser.hasNext()) {
            Token next = parser.parseNextToken();
            tokens.add(next);
        }
        return tokens;
    }

    /**
     * The interface Token item.
     */
    interface TokenItem {
        /**
         * Gets token.
         *
         * @return the token
         */
        Token getToken();
    }

    /**
     * The type Parser iter.
     */
    static class ParserIter {
        /**
         * The constant SPLIT.
         */
        public static final char SPLIT = '.';

        /**
         * The constant INDEX_ACCESS_START_SYMBOL.
         */
        public static final char INDEX_ACCESS_START_SYMBOL = '[';
        /**
         * The constant INDEX_ACCESS_END_SYMBOL.
         */
        public static final char INDEX_ACCESS_END_SYMBOL = ']';

        /**
         * The constant METHOD_START_SYMBOL.
         */
        public static final char METHOD_START_SYMBOL = '(';
        /**
         * The constant METHOD_END_SYMBOL.
         */
        public static final char METHOD_END_SYMBOL = ')';

        private final char[] tokenSymbols;

        private int pos;

        /**
         * Instantiates a new Parser iter.
         *
         * @param tokenStr the token str
         */
        public ParserIter(String tokenStr) {
            this.tokenSymbols = tokenStr.toCharArray();
        }

        /**
         * Has next boolean.
         *
         * @return the boolean
         */
        public boolean hasNext() {
            return pos < tokenSymbols.length;
        }

        /**
         * Parse next token token.
         *
         * @return the token
         */
        public Token parseNextToken() {
            if (pos >= tokenSymbols.length) {
                return null;
            }
            if (tokenSymbols[pos] == SPLIT) {
                pos++;
            }
            String identifier = nextIdentifier();
            // 如果owner的第一个字符不是a-z, A-Z, _
            if (!Character.isJavaIdentifierStart(identifier.charAt(0))) {
                throwParseException("Illegal identifier "+tokenSymbols[pos-identifier.length()], pos-identifier.length());
            }

            Token owner = null;
            if (pos < tokenSymbols.length && tokenSymbols[pos] == METHOD_START_SYMBOL) {
                owner = parseMethodToken(identifier);
            } else {
                owner = new FieldToken(identifier);
            }
            if (pos < tokenSymbols.length && tokenSymbols[pos] == INDEX_ACCESS_START_SYMBOL) {
                owner.setIndex(parseIndexToken());
            }
            return owner;
        }

        private String nextIdentifier() {
            String identifier = null;
            skipWhitespace();
            if (pos >= tokenSymbols.length) {
                throwParseException("Identifier expected end", pos);
            }
            if (!isIdentifierCharacter(pos)) {
                throwParseException("Identifier expected", pos);
            }
            int i;
            for (i = pos; i < tokenSymbols.length && isIdentifierCharacter(i); i++) {
            }
            identifier = new String(Arrays.copyOfRange(tokenSymbols, pos, i));
            pos = i;
            skipWhitespace();
            return identifier;
        }

        /**
         * Skip whitespace.
         */
        void skipWhitespace() {
            while (pos < tokenSymbols.length && Character.isWhitespace(tokenSymbols[pos])) {
                pos++;
            }
        }

        /**
         * Is identifier character boolean.
         *
         * @param i the
         * @return the boolean
         */
        public boolean isIdentifierCharacter(int i) {
            return Character.isJavaIdentifierPart(tokenSymbols[i]);
        }

        /**
         * Parse string const token.
         *
         * @return the const token
         */
        public ConstToken parseString() {
            if (tokenSymbols[pos] != '\'') {
                throwParseException("String expected", pos);
            }
            pos++; // 跳过开头的单引号
            String val = nextIdentifier();
            if (tokenSymbols[pos] != '\'') {
                throwParseException("String expected end", pos);
            }
            pos++; // 跳过结尾的单引号
            return ConstToken.ofStr(val);
        }

        /**
         * Parse int const token.
         *
         * @return the const token
         */
        public ConstToken parseInt() {
            String val = nextIdentifier();
            Integer.parseInt(val); // check
            return ConstToken.ofInt(val);
        }

        /**
         * Parse index token index token.
         *
         * @return the index token
         */
        public IndexToken parseIndexToken() {
            if (tokenSymbols[pos] != INDEX_ACCESS_START_SYMBOL) {
                return null;
            }

            List<ConstToken> index = new ArrayList<>();
            while (pos < tokenSymbols.length && tokenSymbols[pos] == INDEX_ACCESS_START_SYMBOL) {
                pos++; // 跳过 [
                skipWhitespace();
                if (tokenSymbols[pos] == '\'') {
                    index.add(parseString());
                } else {
                    index.add(parseInt());
                }
                skipWhitespace();
                if (pos >= tokenSymbols.length || tokenSymbols[pos] != INDEX_ACCESS_END_SYMBOL) {
                    throwParseException("Unclosed index", pos);
                }
                pos++; // 跳过 ]
            }
            assert index.size() > 0;
            return new IndexToken(index);
        }

        /**
         * Parse method token method token.
         *
         * @param methodName the method name
         * @return the method token
         */
        public MethodToken parseMethodToken(String methodName) {
            if (tokenSymbols[pos] != METHOD_START_SYMBOL) {
                throwParseException("Unknown symbol "+tokenSymbols[pos], pos);
            }
            pos++; // 跳过 (
            skipWhitespace();

            ArgsToken args = new ArgsToken();
            while (pos < tokenSymbols.length && tokenSymbols[pos] != METHOD_END_SYMBOL) {
                if (tokenSymbols[pos] == '\'') {
                    args.add(parseString());
                } else if (tokenSymbols[pos] > '0' && tokenSymbols[pos] <= '9') {
                    args.add(parseInt());
                } else if (tokenSymbols[pos] == '$') {
                    pos++;
                    String identifier = nextIdentifier();
                    args.add(new PlaceholderToken(Integer.parseInt(identifier)));
                } else {
                    Tokens chain = new Tokens();
                    chain.add(parseNextToken());
                    while (pos < tokenSymbols.length && tokenSymbols[pos] == SPLIT) {
                        pos++;
                        chain.add(parseNextToken());
                    }
                    args.add(chain);
                }
                if (pos >= tokenSymbols.length) {
                    throwParseException("Unclosed method", pos);
                }
                if (tokenSymbols[pos] == ',') {
                    pos++;
                } else if (tokenSymbols[pos] != METHOD_END_SYMBOL) {
                    throwParseException("Unknown symbol '"+tokenSymbols[pos]+"'", pos);
                }
                skipWhitespace();
            }
            pos++;
            return new MethodToken(methodName, args);
        }

        /**
         * Throw parse exception.
         *
         * @param message the message
         * @param pos     the pos
         */
        public void throwParseException(String message, int pos) {
            throw new ParseException(String.format("At position %d %s", pos+1, message));
        }
    }
}
