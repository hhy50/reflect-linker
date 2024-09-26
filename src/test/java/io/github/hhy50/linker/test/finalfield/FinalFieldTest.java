package io.github.hhy50.linker.test.finalfield;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class FinalFieldTest {


    @Test
    public void test1() throws LinkerException {
        User user = new User("1234");
        UserVisitor linker = LinkerFactory.createLinker(UserVisitor.class, user);
        linker.setName("111");
        linker.setStaticName("111");

        Assert.assertEquals(linker.getName(), user.getName());
        Assert.assertEquals(linker.getStaticName(), User.getStaticName());
    }
}
