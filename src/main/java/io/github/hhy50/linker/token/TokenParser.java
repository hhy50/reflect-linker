package io.github.hhy50.linker.token;


import io.github.hhy50.linker.exceptions.ParseException;

import java.util.ArrayList;
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
     * @return  the tokens
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
    interface TokenItem {
        /**
         * Next token.
         *
         * @return  the token
         */
        Token getToken();
    }

    /**
     * The type Parser.
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

        private String tokenStr;
        private char[] tokenSymbols;

        private int pos;

        /**
         * Instantiates a new Parser.
         *
         * @param tokenStr the token str
         */
        public ParserIter(String tokenStr) {
            this.tokenStr = tokenStr;
            this.tokenSymbols = tokenStr.toCharArray();
        }

        /**
         * Has next boolean.
         *
         * @return  the boolean
         */
        public boolean hasNext() {
            return pos < tokenSymbols.length;
        }

        /**
         * Is identifier character boolean.
         *
         * @param i the
         * @return  the boolean
         */
        public boolean isIdentifierCharacter(int i) {
            return Character.isJavaIdentifierPart(tokenSymbols[i]);
        }

        /**
         * Check owner.
         *
         * @param owner the owner
         * @param start the start
         * @param len the len
         */
        public void checkOwner(char[] owner, int start, int len) {
            if (len == 0) {
                throwParseException("Unknown symbol "+tokenSymbols[start], start);
            }
            // 如果owner的第一个字符不是a-z, A-Z, _
            if (!Character.isJavaIdentifierStart(tokenSymbols[start])) {
                throwParseException("Unknown symbol "+tokenSymbols[start], start);
            }
        }

        /**
         * Throw parse exception.
         *
         * @param message the message
         * @param pos the pos
         */
        public void throwParseException(String message, int pos) {
            throw new ParseException(String.format("At position %d %s", pos+1, message));
        }

        /**
         * Next token.
         *
         * @return  the token
         */
        public Token next() {
            TokenItem current = null;
            for (int i = pos; i <= tokenSymbols.length; i++) {
                if (i == tokenSymbols.length) {
                    checkOwner(tokenSymbols, pos, i-pos);
                    String owner = tokenStr.substring(pos, i);
                    pos = i+1;
                    return new FieldToken(owner);
                } else if (isIdentifierCharacter(i)) {
                    continue;
                }

                if (tokenSymbols[i] == SPLIT) {
                    if (i == pos) {
                        if (i == 0 || i == tokenSymbols.length-1) {  // 如果第一个或者最后一个字符为  .
                            throwParseException("Unknown symbol "+tokenSymbols[i], i);
                        } else if (tokenSymbols[i-1] == SPLIT) { // 如果出现连续两个字符为  .
                            throwParseException("Unknown symbol "+tokenSymbols[i], i);
                        } else {
                            pos = i+1;
                            continue;
                        }
                    }
                    checkOwner(tokenSymbols, pos, i-pos);
                    String owner = tokenStr.substring(pos, i);
                    pos = i+1;
                    return new FieldToken(owner);
                } else if (tokenSymbols[i] == METHOD_START_SYMBOL) {
                    checkOwner(tokenSymbols, pos, i-pos);
                    String owner = tokenStr.substring(pos, i);
                    pos = i;
                    current = new MethodTokenParser(owner);
                } else if (tokenSymbols[i] == INDEX_ACCESS_START_SYMBOL) {
                    checkOwner(tokenSymbols, pos, i-pos);
                    String owner = tokenStr.substring(pos, i);
                    pos = i;
                    current = new IndexFieldTokenParser(owner);
                    break;
                } else {
                    throwParseException("Unknown symbol "+tokenSymbols[i], i);
                }
            }
            if (current != null) {
                return current.getToken();
            }
            return null;

        }

        /**
         * The type Index field token parser.
         */
        class IndexFieldTokenParser implements TokenItem {
            private List<String> index = new ArrayList<>();
            private final String owner;
            private boolean in;

            /**
             * Instantiates a new Field token.
             *
             * @param owner the owner
             */
            public IndexFieldTokenParser(String owner) {
                this.owner = owner;
            }

            @Override
            public Token getToken() {
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
                if (in) {
                    throwParseException("Parse index symbol error", pos);
                }
                if (index.size() == 0) {
                    throwParseException("Parse index symbol error", pos);
                }
                return new FieldIndexToken(owner, index);
            }
        }

        /**
         * The type Method token parser.
         */
        class MethodTokenParser implements TokenItem {

            private List<String> args = new ArrayList<>();
            private String methodName;
            private boolean in;

            /**
             * Instantiates a new Method token parser.
             *
             * @param methodName the method name
             */
            public MethodTokenParser(String methodName) {
                this.methodName = methodName;
            }

            @Override
            public Token getToken() {
                for (int i = pos; i < tokenSymbols.length; i++) {
                    if (isIdentifierCharacter(i)) continue;

                    if (tokenSymbols[i] == METHOD_START_SYMBOL) {
                        if (in) {
                            throwParseException("Unknown symbol "+tokenSymbols[i], i);
                        }
                        in = true;
                        pos = i+1;
                    } else if (tokenSymbols[i] == METHOD_END_SYMBOL) {
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
                if (in) {
                    throwParseException("Parse index symbol error", pos);
                }
                if (index.size() == 0) {
                    throwParseException("Parse index symbol error", pos);
                }
                return new FieldIndexToken(owner, index);
            }
        }
    }
}
