package io.github.hhy50.linker.define;

/**
 * The type Method holder.
 */
public class SmartMethodDescriptor extends MethodDescriptor {

    /**
     * The constant EMPTY_NAME.
     */
    public static final String EMPTY_NAME = "$$";

    /**
     *
     */
    protected String owner;

    /**
     * Instantiates a new Smart method descriptor.
     *
     * @param methodName the method name
     * @param methodDesc the method desc
     */
    public SmartMethodDescriptor(String methodName, String methodDesc) {
        super(EMPTY_NAME, methodName, methodDesc);
    }

    /**
     * Instantiates a new Smart method descriptor.
     *
     * @param descriptor the descriptor
     */
    public SmartMethodDescriptor(MethodDescriptor descriptor) {
        super(descriptor.getOwner(), descriptor.getMethodName(), descriptor.getDesc());
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
