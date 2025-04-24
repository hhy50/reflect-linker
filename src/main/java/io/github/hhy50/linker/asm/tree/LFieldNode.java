package io.github.hhy50.linker.asm.tree;

import io.github.hhy50.linker.annotations.Field;

public interface LFieldNode {

    @Field.Getter("name")
    String getName();

    @Field.Getter("access")
    int getAccess();

    @Field.Getter("desc")
    String getDesc();
}
