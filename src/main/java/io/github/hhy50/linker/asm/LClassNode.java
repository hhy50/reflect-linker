package io.github.hhy50.linker.asm;

import io.github.hhy50.linker.annotations.Field;

import java.util.List;

/**
 * The interface L class node.
 */
public interface LClassNode extends LClassVisitor {

    /**
     * Gets fields.
     *
     * @return the fields
     */
    @Field.Getter("fields")
    List<Object> getFields();

    /**
     * Gets methods.
     *
     * @return the methods
     */
    @Field.Getter("methods")
    List<Object> getMethods();
}
