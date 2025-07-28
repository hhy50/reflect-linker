package io.github.hhy50.linker.define.field;

import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

public class FieldIndexRef extends FieldRef {
    private final List<Object> index;

    /**
     * Instantiates a new Field ref.
     *
     */
    public FieldIndexRef(FieldRef owner, List<Object> index) {
        super(owner, "index$_"+index.stream()
                .map(Object::toString)
                .collect(Collectors.joining("_")));
        this.index = index;
    }

    public List<Object> getIndex() {
        return index;
    }

    @Override
    public Class<?> getActualType() {
        return super.getActualType();
    }

    @Override
    public Type getType() {
        Class<?> actualType = prev.getActualType();
        if (actualType != Object.class && index.size() == 1) {
            if (actualType.isArray() && index.get(0) instanceof Integer) {
                return Type.getType(actualType.getComponentType());
            }
        }
        return ObjectVar.TYPE;
    }
}
