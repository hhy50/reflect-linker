package io.github.hhy50.linker.define.field;

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
}
