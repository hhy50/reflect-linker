package io.github.hhy50.linker.token;


import java.util.ArrayList;
import java.util.List;

/**
 * The type Tokens.
 */
public class Tokens implements Iterable<Token> {

    private Token head;

    private Token tail;

    /**
     * Add.
     *
     * @param token the token
     */
    public void add(Token token) {
        if (head == null) {
            head = token;
            tail = head;
        } else {
            tail.next = token;
            tail = tail.next;
        }
    }

    /**
     * Tail token.
     *
     * @return token token
     */
    public Token tail() {
        return tail;
    }

    @Override
    public Iterator iterator() {
        return new Iterator();
    }

    @Override
    public String toString() {
        Iterator it = this.iterator();
        List<String> builder = new ArrayList<>();
        while (it.hasNext()) {
            builder.add(it.next().toString());
        }
        return String.join(".", builder);
    }

    /**
     * The type Iterator.
     */
    class Iterator implements java.util.Iterator<Token> {

        private Token next;

        /**
         * Instantiates a new Iterator.
         */
        Iterator() {
            this.next = head;
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public Token next() {
            Token n = this.next;
            this.next = n.next;
            return n;
        }
    }
}
