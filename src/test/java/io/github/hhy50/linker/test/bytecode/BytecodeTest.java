package io.github.hhy50.linker.test.bytecode;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.action.LdcLoadAction;
import io.github.hhy50.linker.generate.bytecode.utils.Members;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.multi;

public class BytecodeTest {


    /**
     *
     * @throws IOException
     * @throws NoSuchFieldException
     */
    @Test
    public void test1() throws IOException, NoSuchFieldException {
        AsmClassBuilder builder = new AsmClassBuilder(Opcodes.ACC_PUBLIC,
                "io/github/hhy50/linker/test/bytecode/Test1", null, null, null);
        builder.defineField(Opcodes.ACC_PUBLIC, "name", String.class)
                .defineMethod(Opcodes.ACC_PUBLIC, "test1", Type.getMethodType(Type.VOID_TYPE), null)
                .intercept(Actions.of(Members.ofStore("name", LdcLoadAction.of("12345")),
                        Methods.invoke("println", Type.getMethodType(Type.VOID_TYPE, Type.getType(String.class)))
                                .setInstance(Members.of(System.class, "out"))
                                .setArgs(multi(Members.ofLoad("name"), Members.ofStore("name", Actions.loadNull()))),
                        mv -> {
                        }).andThen(Actions.vreturn()));
        builder.toBytecode();
    }
}
