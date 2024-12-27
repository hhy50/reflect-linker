package io.github.hhy50.linker.test.nest.case4;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;

public class CallSupperTest {


    @org.junit.Test
    public void test() throws LinkerException {
        FatherVisitor linker = LinkerFactory.createLinker(FatherVisitor.class, new User());

        Assert.assertEquals(linker.father1_aaa(), "Father1");
        Assert.assertEquals(linker.father2_aaa(), "Father2");
        Assert.assertEquals(linker.father3_aaa(), "Father3");
        Assert.assertEquals(linker.father_aaa(), "Father3");
        Assert.assertEquals(linker.my_aaa(), "User");
    }
}
