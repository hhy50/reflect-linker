package io.github.hhy50.linker.test.autolink;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.asm.tree.LClassNode;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.IOException;

public class AutoLinkTest {



    @Test
    public void test1() throws IOException, LinkerException {
        ClassNode classNode = new ClassNode();
        classNode.access = Opcodes.ACC_PUBLIC;
        classNode.name = "io/github/hhy50/linker/test/autolink/User";
        classNode.superName = "java/lang/Object";

        classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "name1", "Ljava/lang/String;", null, null));
        classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "name2", "Ljava/lang/String;", null, null));
        classNode.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "name3", "Ljava/lang/String;", null, null));
        classNode.visitEnd();

        AsmClassBuilder classBuilder = AsmClassBuilder.wrap(LinkerFactory.createLinker(LClassNode.class, classNode));

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        byte[] bytecode1 = classWriter.toByteArray();
        byte[] bytecode2 = classBuilder.toBytecode();
    }

}
