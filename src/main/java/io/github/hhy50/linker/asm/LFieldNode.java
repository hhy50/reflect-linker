package io.github.hhy50.linker.asm;

import io.github.hhy50.linker.annotations.Field;

public interface LFieldNode {

    @Field.Getter("access")
    int getAccess();

    @Field.Getter("name")
    String getName();

    @Field.Getter("desc")
    String getDesc();
}
