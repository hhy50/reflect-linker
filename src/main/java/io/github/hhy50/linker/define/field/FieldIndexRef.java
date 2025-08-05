package io.github.hhy50.linker.define.field;

import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Field index ref.
 */
public class FieldIndexRef extends FieldRef {
    private final List<Object> index;

    /**
     * Instantiates a new Field ref.
     *
     * @param owner the owner
     * @param index the index
     */
    public FieldIndexRef(FieldRef owner, List<Object> index) {
        super(owner, "index$_"+index.stream()
                .map(Object::toString)
                .collect(Collectors.joining("_")));
        this.index = index;
    }

    /**
     * Gets index.
     *
     * @return the index
     */
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
        int dim = TypeUtil.getArrayDimension(actualType);
        if (dim >= index.size()) {
            int i = dim - index.size();
            Type type = Type.getType(actualType).getElementType();
            char[] c = new char[i];
            Arrays.fill(c, '[');
            return Type.getType(new String(c)+type.getDescriptor());
        }
        return ObjectVar.TYPE;
    }
}
