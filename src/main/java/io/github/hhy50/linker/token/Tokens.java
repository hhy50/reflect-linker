package io.github.hhy50.linker.token;


import java.util.ArrayList;
import java.util.List;

/**
 * The type Tokens.
 */
public class Tokens implements Iterable<Token>, Token {

    private Node head;

    private Node tail;

    private int size;

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
            tail.next = new Node(token, null);
            tail = tail.next;
        }
        size++;
    }

    /**
     * Size int.
     *
     * @return the int
     */
    public int size() {
        return size;
    }

    /**
     * Split with MethodToken.
     *
     * @return the java . util . iterator
     */
    public java.util.Iterator<SplitToken> splitWithMethod() {
        return new IteratorSplitMethod(this);
    }

    /**
     * The type Node.
     */
    static class Node {
        /**
         * The Token.
         */
        Token token;
        /**
         * The Next.
         */
        Node next;

        /**
         * Instantiates a new Node.
         *
         * @param token the token
         * @param node  the node
         */
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

    /**
     * The type Iterator split method.
     */
    static class IteratorSplitMethod implements java.util.Iterator<SplitToken> {
        private Node next;

        /**
         * Instantiates a new Iterator split method.
         *
         * @param head the head
         */
        public IteratorSplitMethod(Tokens head) {
            this.next = head.head;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public SplitToken next() {
            Tokens tokens = new Tokens();
            Token method = null;
            while (next != null) {
                Token token = next.token;
                next = next.next;

                if (token instanceof MethodToken) {
                    method = token;
                    break;
                } else {
                    tokens.add(token);
                }
            }
            return new SplitToken(tokens, method);
        }
    }
}
