package io.github.hhy50.linker.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.Opcodes.ASM9;

public class ClassNodeWrap extends ClassVisitor {
    private LClassNode classNode;

    protected ClassNodeWrap(LClassNode classNode) {
        super(ASM9);
        this.classNode = classNode;
    }

    public byte[] toByteArray() {
        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
