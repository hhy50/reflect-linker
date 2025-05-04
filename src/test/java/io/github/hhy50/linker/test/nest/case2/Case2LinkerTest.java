package io.github.hhy50.linker.test.nest.case2;


import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import io.github.hhy50.linker.test.LInteger;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>MyObjectVisitorTest class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public class Case2LinkerTest {

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
        MyObject myObject = new MyObject();
        Case2_Linker case2Linker = LinkerFactory.createLinker(Case2_Linker.class, myObject);
        LInteger age = LinkerFactory.createLinker(LInteger.class, 18);

        case2Linker.setUser(LinkerFactory.createLinker(LUser.class, new UserVo()));
        case2Linker.setName("linker");
        case2Linker.setAge(age);
        case2Linker.setAddress("china");

        Assert.assertNotNull(case2Linker.getUser());
        Assert.assertEquals(myObject.getUser().getName(), case2Linker.getName()+"-vo");
        Assert.assertEquals(age, case2Linker.getAge());
        Assert.assertEquals("linker", case2Linker.getSuperName());
        Assert.assertEquals("china", case2Linker.getAddress());
        Assert.assertEquals("name2", case2Linker.getName2());
        Assert.assertEquals("name3", case2Linker.getName3());
        Assert.assertEquals("name4", case2Linker.getName4());

        case2Linker.setAge2(19);
        Assert.assertEquals(case2Linker.getAge(), 19);

        case2Linker.setAge3(20);
        Assert.assertEquals(case2Linker.getAge(), 20);

    }

    /**
     * <p>test2.</p>
     *
     * @throws LinkerException if any.
     */
    @Test
    public void test2() throws LinkerException {
        MyObject myObject = new MyObject();
        Case2_Linker_Typed linker = LinkerFactory.createLinker(Case2_Linker_Typed.class, myObject);
        linker.setUser(new UserVo());
        linker.setName("linker");
        linker.setAge(18);
        linker.setAddress("china");

        Assert.assertEquals(myObject.getUser().getName(), linker.getName()+"-vo");
        Assert.assertEquals(myObject.getUser().getAge(), linker.getAge());
        Assert.assertEquals("china", linker.getAddress());
    }

}
