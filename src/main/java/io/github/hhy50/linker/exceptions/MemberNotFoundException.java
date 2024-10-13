package io.github.hhy50.linker.exceptions;

/**
 * The type Member not found exception.
 */
public class MemberNotFoundException extends RuntimeException {

    /**
     * Instantiates a new Member not found exception.
     *
     * @param clazz      the clazz
     * @param memberName the member name
     */
    public MemberNotFoundException(String clazz, String memberName) {
        super(String.format("Member %s not found in class %s", memberName, clazz));
    }
}
