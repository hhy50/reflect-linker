package io.github.hhy50.linker.asm.tree;

import io.github.hhy50.linker.annotations.Field;

/**
 * The interface L field node.
 */
public interface LFieldNode {

    /**
     * Gets name.
     *
     * @return the name
     */
    @Field.Getter("name")
    String getName();

    /**
     * Gets access.
     *
     * @return the access
     */
    @Field.Getter("access")
    int getAccess();

    /**
     * Gets desc.
     *
     * @return the desc
     */
    @Field.Getter("desc")
    String getDesc();
}
