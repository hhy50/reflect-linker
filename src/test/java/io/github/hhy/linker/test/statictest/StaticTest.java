package io.github.hhy.linker.test.statictest;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.exceptions.LinkerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StaticTest {


    @Test
    public void test1() throws LinkerException {
        MyStaticClass myObj = LinkerFactory.createStaticLinker(MyStaticClass.class, StaticClass.class);
        Assertions.assertTrue(myObj.getA() == StaticClass.getA());
        Assertions.assertTrue(myObj.getA() == StaticClass.getA());
        Assertions.assertTrue(myObj.getA() == StaticClass.getA());
        Assertions.assertTrue(myObj.getA() == StaticClass.getA());
//        Assertions.assertTrue(myObj.getA2().equals("1234"));
//        Assertions.assertTrue(myObj.getA2().equals("1234"));
//        Assertions.assertTrue(myObj.getA2().equals("1234"));
//        Assertions.assertTrue(myObj.getA2().equals("1234"));

        String str = new String("1234");
        myObj.setA(str);
        myObj.setA(str);
        myObj.setA(str);
        myObj.setA(str);
        myObj.setA(str);
        Assertions.assertTrue(str == StaticClass.getA());
        Assertions.assertTrue(str == StaticClass.getA());
        Assertions.assertTrue(str == StaticClass.getA());
        Assertions.assertTrue(str == StaticClass.getA());
    }
}
