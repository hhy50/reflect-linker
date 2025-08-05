package io.github.hhy50.linker.asm;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ASM9;

/**
 * The type Asm field.
 */
public class AsmField extends FieldVisitor {
    /**
     * The Access.
     */
    public final int access;

    /**
     * The Owner.
     */
    public final String owner;

    /**
     * The Member name.
     */
    public final String name;

    /**
     * The Type.
     */
    public final Type type;

    /**
     * Instantiates a new AsmField.
     *
     * @param access the access
     * @param owner  the owner
     * @param name   the name
     * @param type   the type
     */
    public AsmField(int access, String owner, String name, Type type) {
        super(ASM9, null);
        this.access = access;
        this.owner = owner;
        this.name = name;
        this.type = type;
    }

    /**
     * Instantiates a new AsmField.
     *
     * @param access       the access
     * @param owner        the owner
     * @param name         the name
     * @param type         the type
     * @param fieldVisitor the field visitor
     */
    public AsmField(int access, String owner, String name, Type type, FieldVisitor fieldVisitor) {
        super(ASM9, fieldVisitor);
        this.access = access;
        this.owner = owner;
        this.name = name;
        this.type = type;
    }
}
