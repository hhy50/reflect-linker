package io.github.hhy.linker.test.nest;

import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;


public class NestObjTest {


    @Test
    public void test1() throws LinkerException {
        // get
        ObjVisitor obj = LinkerFactory.createLinker(ObjVisitor.class, new Obj());
//        ObjVisitor$impl obj = new ObjVisitor$impl(new Obj());
        Object a = obj.getA();
        Object b = obj.getB();
        Object c = obj.getC();
        Object c2 = obj.getC2();
        String str = obj.getStr2();
        String str2 = obj.getStr2();
        Object d = obj.getD();

        Assert.assertNotNull(a);
        Assert.assertNotNull(b);
        Assert.assertNotNull(c);
        Assert.assertNotNull(c2);
        Assert.assertNotNull(str);
        Assert.assertNotNull(str2);
        Assert.assertNotNull(d);
        Assert.assertTrue( (Integer) d == 10);

        // set
        a = new A2();
        b = new B();
        c = new C("c");
        c2 = new C("c2");
        str = "new_string_1";
        str2 = "new_string_2";
        d = 20;

        obj.setA(a);
        obj.setB(b);
        obj.setC(c);
        obj.setC2(c2);
        obj.setStr(str);
        obj.setStr2(str2);
        obj.setD((Integer) d);

        Assert.assertEquals(obj.getA(), a);
        Assert.assertEquals(obj.getB(), b);
        Assert.assertEquals(obj.getC(), c);
        Assert.assertEquals(obj.getStr(), str);
        Assert.assertEquals(obj.getStr2(), str2);
        Assert.assertEquals(obj.getD(), d);
    }

    @Test
    public void test2() throws LinkerException {
        // get
        ObjVisitor2 obj = LinkerFactory.createLinker(ObjVisitor2.class, new Obj());
//        ObjVisitor$impl obj = new ObjVisitor$impl(new Obj());
        Object a = obj.getA();
        Object b = obj.getB();
        Object c = obj.getC();
        Object c2 = obj.getC2();
        String str = obj.getStr2();
        String str2 = obj.getStr2();
        Object d = obj.getD();

        Assert.assertNotNull(a);
        Assert.assertNotNull(b);
        Assert.assertNotNull(c);
        Assert.assertNotNull(c2);
        Assert.assertNotNull(str);
        Assert.assertNotNull(str2);
        Assert.assertNotNull(d);
        Assert.assertTrue( (Integer) d == 10);

        // set
        a = new A2();
        b = new B();
        c = new C("c");
        c2 = new C("c2");
        str = "new_string_1";
        str2 = "new_string_2";
        d = 20;

        obj.setA(a);
        obj.setB(b);
        obj.setC(c);
        obj.setC2(c2);
        obj.setStr(str);
        obj.setStr2(str2);
        obj.setD((Integer) d);

        Assert.assertEquals(obj.getA(), a);
        Assert.assertEquals(obj.getB(), b);
        Assert.assertEquals(obj.getC(), c);
        Assert.assertEquals(obj.getStr(), str);
        Assert.assertEquals(obj.getStr2(), str2);
        Assert.assertEquals(obj.getD(), d);
    }
}