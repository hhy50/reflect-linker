package io.github.hhy50.linker.define.field;

import io.github.hhy50.linker.token.ConstToken;

import java.util.List;
import java.util.stream.Collectors;

public class FieldIndexRef extends FieldRef {
    FieldRef owner;
    private final List<ConstToken> index;

    /**
     * Instantiates a new Field ref.
     *
     */
    public FieldIndexRef(FieldRef owner, List<ConstToken> index) {
        super(null, owner.fieldName+"_$index$_"+index.stream().map(ConstToken::getValue)
                .collect(Collectors.joining("_")));
        this.owner = owner;
        this.index = index;
    }
}
