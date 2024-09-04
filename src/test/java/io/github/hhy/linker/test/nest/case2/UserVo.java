package io.github.hhy.linker.test.nest.case2;

public class UserVo extends User {

    private String address;

    public String getName() {
        return super.name+"-vo";
    }

    public String getAddress() {
        return address;
    }

    private String getName2() {
        return "name2";
    }

    private String getName3() {
        return "name3";
    }

    public String getName4() {
        return "name4";
    }
}
