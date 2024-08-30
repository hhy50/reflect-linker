package io.github.hhy.linker.test.nest.case2;

public class UserVo extends User {

    private String address;

    public String getName() {
        return super.name+"-vo";
    }

    public String getAddress() {
        return address;
    }
}
