package io.github.hhy.linker.test.statictest;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>StaticTest class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public class StaticTest {



    /**
     * <p>test1.</p>
     *
     * @throws io.github.hhy.linker.exceptions.LinkerException if any.
     */
    @Test
    public void test1() throws LinkerException {
        MyStaticClass myObj = LinkerFactory.createStaticLinker(MyStaticClass.class, StaticClass.class.getClassLoader());
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
