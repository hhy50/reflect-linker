package io.github.hhy50.linker.test.finalfield;


public class User {
    private static final String _name = "1234";
    private final String name;

    public User(String u) {
        this.name = u;
    }

    public String getName() {
        return name;
    }

    public static Object getStaticName() {
        try {
            return User.class.getDeclaredField("_name").get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
