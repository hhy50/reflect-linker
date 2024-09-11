package io.github.hhy.linker.token;


/**
 * <p>Tokens class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class Tokens implements Iterable<Token> {

    private Token head;

    private Token tail;

    /**
     * <p>add.</p>
     *
     * @param token a {@link io.github.hhy.linker.token.Token} object.
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

    /** {@inheritDoc} */
    @Override
    public Iterator iterator() {
        return new Iterator();
    }

    class Iterator implements java.util.Iterator<Token> {

        private Token next;

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
