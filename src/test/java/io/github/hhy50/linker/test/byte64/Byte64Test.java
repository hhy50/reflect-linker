package io.github.hhy50.linker.test.byte64;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class Byte64Test {

    @Test
    public void test() throws LinkerException {
        Byte64 linker = LinkerFactory.createLinker(Byte64.class, new Byte64Impl());
        Assert.assertTrue(Double.valueOf(linker.doubleValue()).equals(1.1));
        Assert.assertTrue(Float.valueOf(linker.floatValue()).equals(2.2f));
        Assert.assertEquals(linker.longValue(), 3);
        Assert.assertEquals(linker.intValue(), 4);
    }
}
