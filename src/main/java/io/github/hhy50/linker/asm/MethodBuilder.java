package io.github.hhy50.linker.asm;

import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.Map;
import java.util.function.Consumer;

/**
 * The type Method builder.
 */
public class MethodBuilder {

    private final AsmClassBuilder classBuilder;

    private final MethodBody methodBody;

    private final MethodDescriptor methodDescriptor;

    private final boolean isStatic;

    /**
     * @param classBuilder
     * @param md
     * @param isStatic
     * @param methodVisitor
     */
    public MethodBuilder(AsmClassBuilder classBuilder, MethodDescriptor md, boolean isStatic, MethodVisitor methodVisitor) {
        this.classBuilder = classBuilder;
        this.methodDescriptor = md;
        this.isStatic = isStatic;
        this.methodBody = new MethodBody(this, methodVisitor);
    }

    /**
     * Accept asm class builder.
     *
     * @param consumer the consumer
     * @return the asm class builder
     */
    public AsmClassBuilder accept(Consumer<MethodBody> consumer) {
        consumer.accept(methodBody);

        this.methodBody.end();
        return this.classBuilder;
    }

    /**
     * Intercept asm class builder.
     * @param action
     * @return
     */
    public AsmClassBuilder intercept(Action action) {
        action.apply(methodBody);

        this.methodBody.end();
        return this.classBuilder;
    }

    /**
     * Add annotation method builder.
     *
     * @param descriptor the descriptor
     * @param props      the props
     * @return the method builder
     */
    public MethodBuilder addAnnotation(String descriptor, Map<String, Object> props) {
        AnnotationVisitor annotationVisitor = this.methodBody.getWriter()
                .visitAnnotation(descriptor, true);
        if (props != null && props.size() > 0) {
            for (Map.Entry<String, Object> kv : props.entrySet()) {
                annotationVisitor.visit(kv.getKey(), kv.getValue());
            }
        }
        annotationVisitor.visitEnd();
        return this;
    }

    /**
     * Gets class builder.
     *
     * @return the class builder
     */
    public AsmClassBuilder getClassBuilder() {
        return classBuilder;
    }

    /**
     * Gets method body.
     * @return
     */
    public MethodBody getMethodBody() {
        return methodBody;
    }

    /**
     * Gets method name.
     * @return
     */
    public MethodDescriptor getMethodDescriptor() {
        return methodDescriptor;
    }

    /**
     * Gets method desc.
     * @return
     */
    public String getMethodDesc() {
        return methodDescriptor.getDesc();
    }

    /**
     * Is static boolean.
     * @return
     */
    public boolean isStatic() {
        return isStatic;
    }
}
