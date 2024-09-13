package io.github.hhy50.linker.asm;

import org.objectweb.asm.MethodVisitor;

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

    /**
     * <p>Constructor for MethodBuilder.</p>
     *
     * @param classBuilder a {@link AsmClassBuilder} object.
     * @param methodVisitor a {@link org.objectweb.asm.MethodVisitor} object.
     */
    public MethodBuilder(AsmClassBuilder classBuilder, MethodVisitor methodVisitor) {
        this.classBuilder = classBuilder;
        this.methodVisitor = methodVisitor;
    }

    /**
     * <p>accept.</p>
     *
     * @param consumer a {@link java.util.function.Consumer} object.
     * @return a {@link AsmClassBuilder} object.
     */
    public AsmClassBuilder accept(Consumer<MethodVisitor> consumer) {
        consumer.accept(this.methodVisitor);
        this.methodVisitor.visitMaxs(0, 0); // auto
        return this.classBuilder;
    }

    /**
     * <p>Getter for the field <code>classBuilder</code>.</p>
     *
     * @return a {@link AsmClassBuilder} object.
     */
    public AsmClassBuilder getClassBuilder() {
        return classBuilder;
    }

    /**
     * <p>Setter for the field <code>classBuilder</code>.</p>
     *
     * @param classBuilder a {@link AsmClassBuilder} object.
     * @return a {@link MethodBuilder} object.
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
     * @return a {@link MethodBuilder} object.
     */
    public MethodBuilder setMethodVisitor(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
        return this;
    }
}
