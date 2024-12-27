package io.github.hhy50.linker.test.generic;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Test;

import java.util.HashMap;

public class MyHashMapGenericType {

    @Test
    public void test() throws LinkerException {
        HashMap<Object, Object> map = new HashMap<>();
        MyHashMap myMap = LinkerFactory.createLinker(MyHashMap.class, map);

        myMap.put(new Object[] {"key1", "value1"});
        myMap.put(new Object[] {"key2", "value2"});
    }
}