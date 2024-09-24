package io.github.hhy50.linker.asm;

import io.github.hhy50.linker.generate.InvokeClassImplBuilder;
import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.function.Consumer;

/**
 * <p>MethodBuilder class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class MethodBuilder {

    private AsmClassBuilder classBuilder;

    private MethodVisitor methodVisitor;

    private final String methodDesc;

    /**
     * <p>Constructor for MethodBuilder.</p>
     *
     * @param classBuilder  a {@link AsmClassBuilder} object.
     * @param methodVisitor a {@link MethodVisitor} object.
     * @param methodDesc
     */
    public MethodBuilder(AsmClassBuilder classBuilder, MethodVisitor methodVisitor, String methodDesc) {
        this.classBuilder = classBuilder;
        this.methodVisitor = methodVisitor;
        this.methodDesc = methodDesc;
    }

    /**
     * <p>accept.</p>
     *
     * @param consumer a {@link java.util.function.Consumer} object.
     * @return a {@link io.github.hhy50.linker.asm.AsmClassBuilder} object.
     */
    public AsmClassBuilder accept(Consumer<MethodBody> consumer) {
        MethodBody body = new MethodBody((InvokeClassImplBuilder) this.classBuilder, this.methodVisitor, Type.getMethodType(methodDesc));
        consumer.accept(body);
        this.methodVisitor.visitMaxs(0, 0); // auto
        return this.classBuilder;
    }

    /**
     * <p>Getter for the field <code>classBuilder</code>.</p>
     *
     * @return a {@link io.github.hhy50.linker.asm.AsmClassBuilder} object.
     */
    public AsmClassBuilder getClassBuilder() {
        return classBuilder;
    }

    /**
     * <p>Setter for the field <code>classBuilder</code>.</p>
     *
     * @param classBuilder a {@link io.github.hhy50.linker.asm.AsmClassBuilder} object.
     * @return a {@link io.github.hhy50.linker.asm.MethodBuilder} object.
     */
    public MethodBuilder setClassBuilder(AsmClassBuilder classBuilder) {
        this.classBuilder = classBuilder;
        return this;
    }

    /**
     * <p>Getter for the field <code>methodVisitor</code>.</p>
     *
     * @return a {@link org.objectweb.asm.MethodVisitor} object.
     */
    public MethodVisitor getMethodVisitor() {
        return methodVisitor;
    }

    /**
     * <p>Setter for the field <code>methodVisitor</code>.</p>
     *
     * @param methodVisitor a {@link org.objectweb.asm.MethodVisitor} object.
     * @return a {@link io.github.hhy50.linker.asm.MethodBuilder} object.
     */
    public MethodBuilder setMethodVisitor(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
        return this;
    }
}
