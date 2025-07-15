package io.github.hhy50.linker.token;


import io.github.hhy50.linker.exceptions.ParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        ParserIter parser = new ParserIter(tokensStr);
        while (parser.hasNext()) {
            Token next = parser.next();
            tokens.add(next);
        }
        return tokens;
    }

    /**
     * The interface Parser.
     */
    interface Parser {
        /**
         * Next token.
         *
         * @return the token
         */
        Token next();

        /**
         * Has next boolean.
         *
         * @return the boolean
         */
        boolean hasNext();
    }

    /**
     * The type Parser.
     */
    static class ParserIter implements Parser {
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

        private Parser current;

        /**
         * Instantiates a new Parser.
         *
         * @param tokenStr the token str
         */
        public ParserIter(String tokenStr) {
            this.tokenStr = tokenStr;
            this.tokenSymbols = tokenStr.toCharArray();
        }

        @Override
        public boolean hasNext() {
            return pos < tokenSymbols.length;
        }

        /**
         * Is identifier character boolean.
         *
         * @param i the
         * @return the boolean
         */
        public boolean isIdentifierCharacter(int i) {
            if ((tokenSymbols[i] >= 'a' && tokenSymbols[i] <= 'z')
                    || (tokenSymbols[i] >= 'A' && tokenSymbols[i] <= 'Z')
                    || (tokenSymbols[i] >= '0' && tokenSymbols[i] <= '9')
                    || tokenSymbols[i] == '_' || tokenSymbols[i] == '$') {
                return true;
            }
            return false;
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

        @Override
        public Token next() {
            if (current != null && current.hasNext()) {
                return current.next();
            }
            for (int i = pos; i < tokenSymbols.length; i++) {
                if (isIdentifierCharacter(i)) {
                    continue;
                }

                if (tokenSymbols[i] == INDEX_ACCESS_START_SYMBOL) {
                    String owner = tokenStr.substring(pos, i);
                    pos = i;
                    current = new IndexFieldTokenParser(owner);
                    break;
                } else if (tokenSymbols[i] == SPLIT) {
                    if (i == pos) {
                        if (i == tokenSymbols.length-1)
                            throwParseException("Unknown symbol "+tokenSymbols[i], i);
                        else {
                            pos = i+1;
                            continue;
                        }
                    }
                    String owner = tokenStr.substring(pos, i);
                    pos = i+1;
                    return new FieldToken(owner);
                } else {
                    throwParseException("Unknown symbol "+tokenSymbols[i], i);
                }
            }
            if (current != null && current.hasNext()) {
                return current.next();
            }
            return null;

        }

        /**
         * The type Index field token parser.
         */
        class IndexFieldTokenParser implements Parser {
            private List<String> index = new ArrayList<>();
            private final String owner;
            private int start;
            private boolean in;

            /**
             * Instantiates a new Field token.
             *
             * @param owner the owner
             */
            public IndexFieldTokenParser(String owner) {
                this.owner = owner;
                this.start = pos;
            }

            @Override
            public Token next() {
                for (int i = pos; i < tokenSymbols.length; i++) {
                    if (isIdentifierCharacter(i)) continue;

                    if (tokenSymbols[i] == INDEX_ACCESS_START_SYMBOL) {
                        if (in) {
                            throwParseException("Unknown symbol "+tokenSymbols[i], i);
                        }
                        in = true;
                        pos = i+1;
                    } else if (tokenSymbols[i] == INDEX_ACCESS_END_SYMBOL) {
                        if (!in) {
                            throwParseException("Unknown symbol "+tokenSymbols[i], i);
                        }
                        in = false;
                        index.add(tokenStr.substring(pos, i));
                        pos = i+1;
                    } else if (tokenSymbols[i] == '\'') {
                        continue;
                    } else if (tokenSymbols[i] == SPLIT) {
                        break;
                    } else {
                        throw new ParseException("Unknown symbol "+tokenSymbols[i]+", at position "+i);
                    }
                }
                if (index.size() == 0) {
                    throwParseException("Parse index symbol error", pos);
                }
                return new FieldIndexToken(owner, index);
            }

            @Override
            public boolean hasNext() {
                return this.start == pos;
            }
        }
    }
}
