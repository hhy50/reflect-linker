package io.github.hhy50.linker.token;


import java.util.ArrayList;
import java.util.List;

/**
 * The type Tokens.
 */
public class Tokens implements Iterable<Token> {

    private Node head;

    private Node tail;

    /**
     * Add.
     *
     * @param token the token
     */
    public void add(Token token) {
        if (head == null) {
            head = new Node(token, null);
            tail = head;
        } else {
            tail.next = head = new Node(token, null);;
            tail = tail.next;
        }
    }

    class Node {
        Token token;
        Node next;
        public Node(Token token, Node node) {
            this.token = token;
            this.next = node;
        }
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

        private Node next;

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
            Node n = this.next;
            this.next = n.next;
            return n.token;
        }
    }
}
