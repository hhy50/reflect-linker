package io.github.hhy.linker.test.nest;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.exceptions.LinkerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class NestObjTest {


    @Test
    public void test1() throws LinkerException {
        // get
        ObjVisitor obj = LinkerFactory.createLinker(ObjVisitor.class, new Obj());
        Object a = obj.getA();
        Object b = obj.getB();
        Object c = obj.getC();
        Object c2 = obj.getC2();
        String str = obj.getStr2();
        String str2 = obj.getStr2();

        Assertions.assertNotNull(a);
        Assertions.assertNotNull(b);
        Assertions.assertNotNull(c);
        Assertions.assertNotNull(c2);
        Assertions.assertNotNull(str);
        Assertions.assertNotNull(str2);

        // set
        a = new A2();
        b = new B();
        c = new C("c");
        c2 = new C("c2");
        str = "new_string_1";
        str2 = "new_string_2";

        obj.setA(a);
        obj.setB(b);
        obj.setC(c);
        obj.setC2(c2);
        obj.setStr(str);
        obj.setStr2(str2);

        Assertions.assertEquals(obj.getA(), a);
        Assertions.assertEquals(obj.getB(), b);
        Assertions.assertEquals(obj.getC(), c);
        Assertions.assertEquals(obj.getStr(), str);
        Assertions.assertEquals(obj.getStr2(), str2);
    }
}