package io.github.hhy50.linker.test.constructor;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;


public class MyObjectTest {


    @Test
    public void test() throws LinkerException, NoSuchMethodException, IllegalAccessException {
        MyObjectVisitor staticLinker = LinkerFactory.createStaticLinker(MyObjectVisitor.class, MyObject.class);
        MyObjectVisitor o1 = staticLinker.newInstance1("haiyang");
        MyObjectVisitor o2 = staticLinker.newInstance2("haiyang");

        Assert.assertNotNull(o1);
        Assert.assertNotNull(o2);
        Assert.assertEquals(o1, o2);
    }
}
