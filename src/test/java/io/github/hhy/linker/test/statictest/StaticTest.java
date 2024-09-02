package io.github.hhy.linker.test.statictest;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class StaticTest {



    @Test
    public void test1() throws LinkerException {
        MyStaticClass myObj = LinkerFactory.createStaticLinker(MyStaticClass.class, StaticClass.class.getClassLoader());
        Assert.assertNotNull(myObj.getObjAaa2());
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
