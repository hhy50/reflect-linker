package io.github.hhy50.linker.test.nest.case3;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class TestStatic {


    @Test
    public void test1() throws LinkerException {
        FirstVisitor linker = LinkerFactory.createStaticLinker(FirstVisitor.class, TestStatic.class.getClassLoader());
        Assert.assertEquals(linker.getThreeClass(), ThreeClass.class.getName());

        linker.setThreeClass("1234");
        Assert.assertEquals(linker.getThreeClass(), "1234");
    }
}
