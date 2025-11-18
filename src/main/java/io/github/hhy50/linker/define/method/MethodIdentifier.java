package io.github.hhy50.linker.define.method;


import org.objectweb.asm.Type;

public class MethodIdentifier {
    private Type owner;
    private String name;
    private Type type;

    public MethodIdentifier(Type owner, String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {


        return false;
    }
}
