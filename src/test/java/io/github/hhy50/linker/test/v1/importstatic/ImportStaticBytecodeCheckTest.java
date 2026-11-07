package io.github.hhy50.linker.test.v1.importstatic;

import io.github.hhy50.linker.define.ClassDefineParser;
import io.github.hhy50.linker.define.GeneratedClass;
import io.github.hhy50.linker.test.v1.importstatic.ImportStaticTest.MyLinker;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

/**
 * 校验 public 静态方法生成为 invokestatic 直调
 */
public class ImportStaticBytecodeCheckTest {

    @Test
    public void testPublicStaticMethodUsesInvokestatic() throws Exception {
        GeneratedClass gClass = ClassDefineParser.parseClass(MyLinker.class,
                MyLinker.class.getClassLoader().loadClass(
                        "io.github.hhy50.linker.test.v1.importstatic.ImportStaticTest$MyTarget"));

        ClassNode cn = new ClassNode();
        new ClassReader(gClass.getBytecode()).accept(cn, 0);

        List<MethodInsnNode> invokeStatics = new ArrayList<>();
        for (org.objectweb.asm.tree.MethodNode mn : cn.methods) {
            for (org.objectweb.asm.tree.AbstractInsnNode insn : mn.instructions.toArray()) {
                if (insn instanceof MethodInsnNode && insn.getOpcode() == INVOKESTATIC) {
                    invokeStatics.add((MethodInsnNode) insn);
                }
            }
        }

        // pubMethod: public static -> invokestatic 直调
        Assert.assertTrue(invokeStatics.stream().anyMatch(insn ->
                insn.name.equals("pubMethod") && insn.owner.contains("StaticHolder")));
        // privMethod: private static -> 不应直调（走 MethodHandle）
        Assert.assertTrue(invokeStatics.stream().noneMatch(insn -> insn.name.equals("privMethod")));
        // defaultMethod: package-private static -> 不应直调
        Assert.assertTrue(invokeStatics.stream().noneMatch(insn -> insn.name.equals("defaultMethod")));
    }
}
