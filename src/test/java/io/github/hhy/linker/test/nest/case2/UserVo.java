package io.github.hhy.linker.test.nest.case2;

/**
 * <p>UserVo class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public class UserVo extends User {

    private String address;

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return super.name+"-vo";
    }

    /**
     * <p>Getter for the field <code>address</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAddress() {
        return address;
    }

    private static String getName2() {
        return "name2";
    }

    private String getName3() {
        return "name3";
    }

    /**
     * <p>getName4.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName4() {
        return "name4";
    }
}
