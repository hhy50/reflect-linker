package io.github.hhy50.linker.syslinker;


import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Runtime;


/**
 * The interface Direct method handle linker.
 */
//@Runtime
public interface DirectMethodHandleLinker {

    /**
     * Modifiers int.
     *
     * @return the int
     */
    @Runtime.Static(name = {"member", "member.flags"}, value = false)
    @Field.Getter("member.flags")
    int modifiers();

    /**
     * Gets member.
     *
     * @return the member
     */
    @Runtime.Static(name = {"member"}, value = false)
    @Field.Getter("member")
    MemberNameLinker getMember();
}
