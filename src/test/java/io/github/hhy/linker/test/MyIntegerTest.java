package io.github.hhy.linker.test;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class MyIntegerTest {


    @Test
    public void test() throws LinkerException {
        MyInteger myInteger = LinkerFactory.createLinker(MyInteger.class, new Integer(10));
        Assert.assertEquals(10, myInteger.getValue());
    }
}
