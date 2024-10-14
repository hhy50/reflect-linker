package io.github.hhy50.linker.test.duck_type;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class DuckTypeTest {

    @Test
    public void test1() throws LinkerException {
        DuckVisitor duck1 = LinkerFactory.createLinker(DuckVisitor.class, new Duck());
        DuckVisitor duck2 = LinkerFactory.createLinker(DuckVisitor.class, new Frog());
        Assert.assertEquals(duck1.run(), "duck");
        Assert.assertEquals(duck2.run(), "frog");
    }
}
