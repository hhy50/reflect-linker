package io.github.hhy.linker.runtime;

import java.lang.reflect.Array;

/**
 * <p>RuntimeExtension class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class RuntimeExtension {

    /**
     * 通过运行时调用, 来支持扩展属性的访问
     *
     * @param obj a {@link java.lang.Object} object.
     * @param fieldName a {@link java.lang.String} object.
     * @param index a {@link java.lang.String} object.
     * @return a {@link java.lang.Object} object.
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
