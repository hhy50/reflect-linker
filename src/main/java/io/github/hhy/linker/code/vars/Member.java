package io.github.hhy.linker.code.vars;

public abstract class Member {

    /**
     * 所属类
     */
    public String owner;

    /**
     * 成员名称
     */
    public String memberName;

    /**
     * 类型
     */
    public String type;

    public Member(String owner, String memberName, String type) {
        this.owner = owner;
        this.memberName = memberName;
        this.type = type;
    }
}
