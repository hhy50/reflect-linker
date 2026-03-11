package io.github.hhy50.linker.tools;

import java.lang.reflect.Field;

/**
 * The type Field type unique key.
 */
public class FieldTypeUniqueKey {
    private Field field;
    private Integer type;

    /**
     * Instantiates a new Field type unique key.
     *
     * @param field the field
     * @param type  the type
     */
    public FieldTypeUniqueKey(Field field, Integer type) {
        this.field = field;
        this.type = type;
    }

    /**
     * With getter field type unique key.
     *
     * @param reflect the reflect
     * @return the field type unique key
     */
    public static FieldTypeUniqueKey withGetter(Field reflect) {
        return new FieldTypeUniqueKey(reflect, 1);
    }

    /**
     * With setter field type unique key.
     *
     * @param reflect the reflect
     * @return the field type unique key
     */
    public static FieldTypeUniqueKey withSetter(Field reflect) {
        return new FieldTypeUniqueKey(reflect, 2);
    }

    @Override
    public int hashCode() {
        return 1008611;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FieldTypeUniqueKey) {
            FieldTypeUniqueKey other = (FieldTypeUniqueKey)obj;
            return this.field.equals(other.field) && this.type == other.type;
        }
        return false;
    }
}

