package io.github.hhy50.linker.test.nest.case2;

/**
 * <p>User class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public class User {
    protected String name;
    protected int age;

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Getter for the field <code>age</code>.</p>
     *
     * @return a int.
     */
    public int getAge() {
        return age;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "name="+name+", age="+age;
    }

    public String getName(String name) {
        return name+":getName()";
    }
}
