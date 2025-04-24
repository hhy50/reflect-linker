package io.github.hhy50.linker.asm;

import io.github.hhy50.linker.asm.tree.LClassNode;
import io.github.hhy50.linker.define.provider.TargetProvider;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM9;

/**
 * The type Class visitor wrap.
 */
public class ClassVisitorWrap extends ClassVisitor {
    private LClassNode classVisitor;

    /**
     * Instantiates a new Class visitor wrap.
     *
     * @param classVisitor the class visitor
     */
    protected ClassVisitorWrap(LClassNode classVisitor) {
        super(ASM9);
        this.classVisitor = classVisitor;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return classVisitor.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return classVisitor.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return classVisitor.visitAnnotation(descriptor, visible);
    }

    /**
     * Gets target.
     *
     * @return the target
     */
    public ClassVisitor getTarget() {
        return (ClassVisitor) ((TargetProvider) classVisitor).getTarget();
    }

    /**
     * Accept.
     *
     * @param classVisitor the class visitor
     */
    public void accept(ClassVisitor classVisitor) {
        this.classVisitor.accept(classVisitor);
    }
}
