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
         *
         */
        public void skipWhitespace() {
            while (pos < tokenSymbols.length && Character.isWhitespace(tokenSymbols[pos])) {
                pos++;
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
            private List<Token> args = new ArrayList<>();
            private String methodName;
            private boolean in;

            public MethodTokenParser(String methodName) {
                this.methodName = methodName;
            }

            @Override
            public Token getToken() {
                if (tokenSymbols[pos] != METHOD_START_SYMBOL) {
                    throwParseException("Expected '('", pos);
                }
                pos++; // 跳过 '('
                skipWhitespace();

                while (pos < tokenSymbols.length) {
                    if (tokenSymbols[pos] == METHOD_END_SYMBOL) {
                        pos++; // 跳过 ')'
                        break;
                    }

                    Token arg = parseArg();
                    args.add(arg);
                    skipWhitespace();

                    if (pos < tokenSymbols.length && tokenSymbols[pos] == ',') {
                        pos++; // 跳过 ','
                        skipWhitespace();
                    } else if (tokenSymbols[pos] != METHOD_END_SYMBOL) {
                        throwParseException("Expected ',' or ')'", pos);
                    }
                }
                return new MethodToken(methodName, args);
            }

            private Token parseArg() {
                char c = tokenSymbols[pos];
                if (c == '\'') {
                    return parseString();
                } else if (Character.isDigit(c)) {
                    return parseInteger();
                } else if (c == '$') {
                    return parsePlaceholder();
                } else if (Character.isJavaIdentifierStart(c)) {
                    return parseProperty();
                } else {
                    throwParseException("Unexpected character: " + c, pos);
                    return null;
                }
            }

            private Token parseString() {
                pos++; // 跳过开头的单引号
                int start = pos;
                while (pos < tokenSymbols.length) {
                    if (tokenSymbols[pos] == '\'') {
                        String value = tokenStr.substring(start, pos);
                        pos++; // 跳过结尾的单引号
                        return new ConstToken.Str(value);
                    }
                    pos++;
                }
                throwParseException("Unclosed string", start - 1);
                return null;
            }

            private Token parseInteger() {
                int start = pos;
                while (pos < tokenSymbols.length && Character.isDigit(tokenSymbols[pos])) {
                    pos++;
                }
                String num = tokenStr.substring(start, pos);
                try {
                    return new ConstToken.Int(num);
                } catch (NumberFormatException e) {
                    throwParseException("Invalid integer: " + num, start);
                    return null;
                }
            }

            private Token parsePlaceholder() {
                pos++; // 跳过 '$'
                int start = pos;
                while (pos < tokenSymbols.length && Character.isDigit(tokenSymbols[pos])) {
                    pos++;
                }
                if (pos == start) {
                    throwParseException("Expected digit after '$'", start - 1);
                }
                String indexStr = tokenStr.substring(start, pos);
                try {
                    return new PlaceholderToken(Integer.parseInt(indexStr));
                } catch (NumberFormatException e) {
                    throwParseException("Invalid placeholder index: " + indexStr, start);
                    return null;
                }
            }

            private Token parseProperty() {
                int start = pos;
                int depth = 0;
                boolean inString = false;

                while (pos < tokenSymbols.length) {
                    char c = tokenSymbols[pos];
                    if (inString) {
                        if (c == '\'') inString = false;
                    } else {
                        if (c == '\'') inString = true;
                        else if (c == '[') depth++;
                        else if (c == ']') depth--;
                        else if (depth == 0 && (c == ',' || c == ')' || Character.isWhitespace(c))) break;
                    }
                    pos++;
                }

                if (depth != 0) {
                    throwParseException("Unmatched brackets", start);
                }

                String property = tokenStr.substring(start, pos);
                return new FieldToken(property);
            }
        }
    }
}
