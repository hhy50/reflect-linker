package io.github.hhy.linker.token;

import lombok.Getter;

public class Tokens {

    @Getter
    private Token head;

    @Getter
    private Token tail;

    public void add(Token token) {
        if (head == null) {
            head = token;
            tail = head;
        } else {
            tail.next = token;
            tail = tail.next;
        }
    }
}
