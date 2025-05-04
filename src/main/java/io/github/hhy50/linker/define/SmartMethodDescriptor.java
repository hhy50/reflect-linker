package io.github.hhy50.linker.define;

import org.objectweb.asm.Type;

/**
 * The type Method holder.
 */
public class SmartMethodDescriptor extends MethodDescriptor {

    /**
     * The constant EMPTY_NAME.
     */
    public static final String EMPTY_NAME = "$$";

    /**
     * The Owner.
     */
    protected String owner;

    /**
     * Instantiates a new Smart method descriptor.
     *
     * @param name the method name
     * @param type the type
     */
    public SmartMethodDescriptor(String name, Type type) {
        super(EMPTY_NAME, name, type);
    }

    /**
     * Instantiates a new Smart method descriptor.
     *
     * @param descriptor the descriptor
     */
    public SmartMethodDescriptor(MethodDescriptor descriptor) {
        super(descriptor.getOwner(), descriptor.getMethodName(), descriptor.getType());
    }

    /**
     * Sets owner.
     *
     * @param owner the owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     *
     * @return
     */
    public String getOwner() {
        if (this.owner != null) {
            return this.owner;
        }
        return super.getOwner();
    }
}
