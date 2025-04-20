package io.github.hhy50.linker.asm;

import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.ASM9;

public class ClassNodeWrap extends ClassVisitor {
    private LClassNode classNode;

    protected ClassNodeWrap(LClassNode classNode) {
        super(ASM9);
        this.classNode = classNode;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return classNode.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return classNode.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return classNode.visitAnnotation(descriptor, visible);
    }

    public byte[] toByteArray() {
        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
