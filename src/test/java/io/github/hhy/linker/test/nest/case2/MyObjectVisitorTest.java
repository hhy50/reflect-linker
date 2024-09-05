package io.github.hhy.linker.test.nest.case2;


import io.github.hhy.linker.LinkerFactory;
import io.github.hhy.linker.exceptions.LinkerException;
import io.github.hhy.linker.test.MyInteger;
import org.junit.Assert;
import org.junit.Test;

public class MyObjectVisitorTest {


    @Test
    public void test1() throws LinkerException {
        MyObject myObject = new MyObject();
        MyObjectVisitor linker = LinkerFactory.createLinker(MyObjectVisitor.class, myObject);
        MyInteger age = LinkerFactory.createLinker(MyInteger.class, 18);

        linker.setUser(LinkerFactory.createLinker(UserVisitor.class, new UserVo()));
        linker.setName("linker");
        linker.setAge(age);
        linker.setAddress("china");

        Assert.assertEquals(myObject.getUser().getName(), linker.getName()+"-vo");
        Assert.assertEquals("linker", linker.getSuperName());
        Assert.assertEquals(age, linker.getAge());
        Assert.assertEquals("china", linker.getAddress());
        Assert.assertEquals("name2", linker.getName2());
        Assert.assertEquals("name3", linker.getName3());
        Assert.assertEquals("name4", linker.getName4());
        System.out.println(linker.superToString());
        System.out.println(linker.getUser());
    }

    @Test
    public void test2() throws LinkerException {
        MyObject myObject = new MyObject();
        MyObjectVisitor2 linker = LinkerFactory.createLinker(MyObjectVisitor2.class, myObject);
        linker.setUser(new UserVo());
        linker.setName("linker");
        linker.setAge(18);
        linker.setAddress("china");

        Assert.assertEquals(myObject.getUser().getName(), linker.getName()+"-vo");
        Assert.assertEquals(myObject.getUser().getAge(), linker.getAge());
        Assert.assertEquals("china", linker.getAddress());
    }

}
