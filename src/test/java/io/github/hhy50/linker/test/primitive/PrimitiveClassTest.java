package io.github.hhy50.linker.test.primitive;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class PrimitiveClassTest {

    @Test
    public void test() throws LinkerException {
        PrimitiveClass linker = LinkerFactory.createLinker(PrimitiveClass.class, new PrimitiveClassImpl());
        Assert.assertTrue(Double.valueOf(linker.doubleValue()).equals(1.1));
        Assert.assertTrue(Float.valueOf(linker.floatValue()).equals(2.2f));
        Assert.assertEquals(3, linker.longValue());
        Assert.assertEquals(4, linker.intValue());
    }
}
