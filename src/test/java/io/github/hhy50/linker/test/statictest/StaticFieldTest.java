package io.github.hhy50.linker.test.statictest;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>StaticTest class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public class StaticFieldTest {



    /**
     * <p>test1.</p>
     *
     * @throws LinkerException if any.
     */
    @Test
    public void test1() throws LinkerException {
        LStaticFieldClass myObj = LinkerFactory.createStaticLinker(LStaticFieldClass.class, StaticClass.class);
        Assert.assertNotNull(myObj.getObjAaa2());
        Assert.assertNotNull(myObj.getObj2Aaa2());
        Assert.assertTrue(myObj.getA() == StaticClass.getA());
        Assert.assertTrue(myObj.getA() == StaticClass.getA());
        Assert.assertTrue(myObj.getA() == StaticClass.getA());
        Assert.assertTrue(myObj.getA() == StaticClass.getA());

//        Assert.assertTrue(myObj.getA2().equals("1234"));
//        Assert.assertTrue(myObj.getA2().equals("1234"));
//        Assert.assertTrue(myObj.getA2().equals("1234"));
//        Assert.assertTrue(myObj.getA2().equals("1234"));

        String str = new String("1234");
        myObj.setA(str);
        myObj.setA(str);
        myObj.setA(str);
        myObj.setA(str);
        myObj.setA(str);
        Assert.assertTrue(str == StaticClass.getA());
        Assert.assertTrue(str == StaticClass.getA());
        Assert.assertTrue(str == StaticClass.getA());
        Assert.assertTrue(str == StaticClass.getA());
    }
}
