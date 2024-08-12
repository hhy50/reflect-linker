package io.github.hhy.linker.runtime;

import java.lang.reflect.Array;

public class RuntimeExtension {

    /**
     * 通过运行时调用, 来支持扩展属性的访问
     * @param obj
     * @param fieldName
     * @param index
     * @return
     */
    public static Object extensionProperty(Object obj, String fieldName, String index) {
        if (obj.getClass().isArray() && fieldName.equals("length")) {
            return Array.getLength(obj);
        }
        if (obj.getClass().isArray() && index != null) {
            return Array.get(obj, Integer.parseInt(index));
        }
        return null;
    }
}
