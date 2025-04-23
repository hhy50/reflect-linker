package io.github.hhy50.linker.test.nest.case2;


import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import io.github.hhy50.linker.test.MyInteger;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>MyObjectVisitorTest class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public class LinkerTest {

    static class MyObject {
        private User user;

        /**
         * <p>Getter for the field <code>user</code>.</p>
         *
         * @return a {@link User} object.
         */
        public User getUser() {
            return user;
        }
    }

    /**
     * <p>test1.</p>
     *
     * @throws LinkerException if any.
     */
    @Test
    public void test1() throws LinkerException {
        LinkerFactory.setOutputPath("C:\\Users\\49168\\IdeaProjects\\reflect-linker\\target");
        MyObject myObject = new MyObject();
        Linker linker = LinkerFactory.createLinker(Linker.class, myObject);
        MyInteger age = LinkerFactory.createLinker(MyInteger.class, 18);

        linker.setUser(LinkerFactory.createLinker(LUser.class, new UserVo()));
        linker.setName("linker");
        linker.setAge(age);
        linker.setAddress("china");

        Assert.assertNotNull(linker.getUser());
        Assert.assertEquals(myObject.getUser().getName(), linker.getName()+"-vo");
        Assert.assertEquals(age, linker.getAge());
        Assert.assertEquals("linker", linker.getSuperName());
        Assert.assertEquals("china", linker.getAddress());
        Assert.assertEquals("name2", linker.getName2());
        Assert.assertEquals("name3", linker.getName3());
        Assert.assertEquals("name4", linker.getName4());
    }

    /**
     * <p>test2.</p>
     *
     * @throws LinkerException if any.
     */
    @Test
    public void test2() throws LinkerException {
        MyObject myObject = new MyObject();
        Linker_Typed linker = LinkerFactory.createLinker(Linker_Typed.class, myObject);
        linker.setUser(new UserVo());
        linker.setName("linker");
        linker.setAge(18);
        linker.setAddress("china");

        Assert.assertEquals(myObject.getUser().getName(), linker.getName()+"-vo");
        Assert.assertEquals(myObject.getUser().getAge(), linker.getAge());
        Assert.assertEquals("china", linker.getAddress());
    }

}
