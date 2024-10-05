package io.github.hhy50.linker.generate.bytecode.action;


import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.Member;
import org.objectweb.asm.Type;


/**
 * The type Field action.
 */
public class FieldAction implements LoadAction {

    private final String name;
    private final Member member;

    /**
     * Instantiates a new Field action.
     *
     * @param member the member
     */
    public FieldAction(Member member) {
        this.member = member;
        this.name = null;
    }

    /**
     * Instantiates a new Field action.
     *
     * @param name the name
     */
    public FieldAction(String name) {
        this.member = null;
        this.name = name;
    }

    @Override
    public void load(MethodBody body) {

    }

    /**
     * Of getter field action.
     *
     * @param filedName the filed name
     * @return the field action
     */
    public static FieldAction ofGetter(String filedName) {
    return null;
    }

    @Override
    public Type getType() {
        return null;
    }
}
