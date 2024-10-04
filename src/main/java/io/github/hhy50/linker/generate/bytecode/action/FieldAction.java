package io.github.hhy50.linker.generate.bytecode.action;


import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.Member;
import org.objectweb.asm.Type;


public class FieldAction implements LoadAction {

    private final String name;
    private final Member member;

    public FieldAction(Member member) {
        this.member = member;
        this.name = null;
    }

    public FieldAction(String name) {
        this.member = null;
        this.name = name;
    }

    @Override
    public void load(MethodBody body) {

    }

    public static FieldAction ofGetter(String filedName) {
    return null;
    }

    @Override
    public Type getType() {
        return null;
    }
}
