package io.github.hhy50.linker.test.generic;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class GenericTypeTest {

    @Test
    public void test() throws LinkerException {
        GenericTypeLinker linker = LinkerFactory.createLinker(GenericTypeLinker.class, new GenericType());
        Assert.assertNotNull(linker.getUsers());
        for (GenericTypeLinker.LUser user : linker.getUsers()) {
            Assert.assertNotNull(user.getName());
        }

        HashMap<Object, Object> map = new HashMap<>();
        MyHashMap myMap = LinkerFactory.createLinker(MyHashMap.class, map);

        myMap.put("key1", "value1");
        myMap.put(new Object[] {"key2", "value2"});

        Assert.assertEquals(myMap.get("key1"), "value1");
        Assert.assertEquals(myMap.get("key2"), "value2");

        MyStaticUser user = LinkerFactory.createStaticLinker(MyStaticUser.class, StaticUser.class);
        Assert.assertEquals(user.getName("linker"), "linker");
        Assert.assertEquals(user.getName(), "default");
    }
}
