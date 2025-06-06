package io.github.hhy50.linker.test.accessor;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class TestAccesssor {


    @Test
    public void test() throws LinkerException {
        User user = new User();
        user.age = 10;
        user.name = "haiyang";

        UserAccessor accessor = new UserAccessor(user);
        accessor.setAge(20);
        accessor.setName(LinkerFactory.createLinker(LString.class, "myName"));
        accessor.test1();
        UserAccessor.test2();

        Assert.assertEquals(accessor.getAge(), 20);
        Assert.assertEquals(accessor.getName(), "myName");
        Assert.assertEquals(accessor.self_toString(), "User.toString()");
        Assert.assertEquals(accessor.super_toString(), "UserParent.toString()");
        Assert.assertTrue(accessor.super_super_toString().startsWith(User.class.getName()+"@"));
    }
}
