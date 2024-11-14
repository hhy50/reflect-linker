package io.github.hhy50.linker.define;


import io.github.hhy50.linker.util.ReflectUtil;

/**
 * The type Method holder.
 */
public class SmartMethodDescriptor extends MethodDescriptor {

    public static final String EMPTY_NAME = "$$";

    public SmartMethodDescriptor(String methodName, String methodDesc) {
        super(EMPTY_NAME, methodName, methodDesc);
    }

    public SmartMethodDescriptor(MethodDescriptor descriptor) {
        super(descriptor.getOwner(), descriptor.getMethodName(), descriptor.getDesc());
    }

    public void setOwner(String owner) {
        try {
            ReflectUtil.setFieldValue(this, "owner", owner);
        } catch (IllegalAccessException e) {
            //
        }
    }
}
