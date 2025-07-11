package io.github.hhy50.linker.token;


import io.github.hhy50.linker.exceptions.ParseException;

import java.util.Iterator;

/**
 * The type Token parser.
 */
public class TokenParser {

    /**
     * The Empty.
     */
    static Tokens EMPTY = new Tokens();

    /**
     * Parse tokens.
     *
     * @param tokensStr the tokens str
     * @return the tokens
     */
    public Tokens parse(String tokensStr) {
        if (tokensStr == null || (tokensStr = tokensStr.trim()).length() == 0) {
            return EMPTY;
        }

        Tokens tokens = new Tokens();
        Parser parser = new Parser(tokensStr);
        while (parser.hasNext()) {
            Token next = parser.next();
            tokens.add(next);
        }
        return tokens;
    }

    /**
     * The type Parser.
     */
    static class Parser implements Iterator<Token> {
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

        private String tokenStr;
        private char[] tokenSymbols;

        private int pos;

        /**
         * Instantiates a new Parser.
         *
         * @param tokenStr the token str
         */
        public Parser(String tokenStr) {
            this.tokenStr = tokenStr;
            this.tokenSymbols = tokenStr.toCharArray();
        }

        @Override
        public boolean hasNext() {
            return pos < tokenSymbols.length;
        }

        @Override
        public Token next() {
            String owner = null;
            String index = null;
            boolean mapIndex = false;
            for (int i = pos; i < tokenSymbols.length; i++) {
                if ((tokenSymbols[i] >= 'a' && tokenSymbols[i] <= 'z')
                        || (tokenSymbols[i] >= 'A' && tokenSymbols[i] <= 'Z')
                        || (tokenSymbols[i] >= '0' && tokenSymbols[i] <= '9')
                        || tokenSymbols[i] == '_' || tokenSymbols[i] == '$') {
                    continue;
                } else if (tokenSymbols[i] == INDEX_ACCESS_START_SYMBOL) {
                    owner = tokenStr.substring(pos, i);
                    pos = i;
                } else if (tokenSymbols[i] == INDEX_ACCESS_END_SYMBOL) {
                    int end = i;
                    if (mapIndex) {
                        pos++;
                        end--;
                    }
                    index = tokenStr.substring(pos+1, end);
                    pos = i+1;
                } else if (tokenSymbols[i] == SPLIT) {
                    if (owner == null) {
                        owner = tokenStr.substring(pos, i);
                    }
                    pos = i+1;
                    break;
                } else if (tokenSymbols[i] == '\'') {
                    mapIndex = true;
                } else {
                    throw new ParseException("Unknown symbol "+tokenSymbols[i]);
                }
            }
            if (owner == null) {
                owner = tokenStr.substring(pos);
                pos = tokenSymbols.length;
            }
            if (mapIndex && index == null) {
                throw new ParseException("Unknown symbol '");
            }
            if (index != null) {
                return mapIndex ? new MapKeyToken(owner, index) : new ArrayIndexFieldToken(owner, index);
            }
            return new FieldToken(owner);
        }
    }
}
