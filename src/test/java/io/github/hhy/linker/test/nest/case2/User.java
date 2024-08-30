package io.github.hhy.linker.test.nest.case2;

public class User {
    protected String name;
    protected int age;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "name="+name+", age="+age;
    }
}
