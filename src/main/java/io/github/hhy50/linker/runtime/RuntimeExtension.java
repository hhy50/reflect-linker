package io.github.hhy50.linker.runtime;

import java.lang.reflect.Array;

/**
 * The type Runtime extension.
 */
public class RuntimeExtension {

    /**
     * Extension property object.
     *
     * @param obj       the obj
     * @param fieldName the field name
     * @param index     the index
     * @return the object
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
