package io.github.hhy50.linker.syslinker;


import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Runtime;


/**
 * The interface Direct method handle linker.
 */
@Runtime
@Runtime.Static(name = {"member", "member.flags"}, value = false)
public interface DirectMethodHandleLinker {

    /**
     * Modifiers int.
     *
     * @return the int
     */
    @Field.Getter("member.flags")
    int modifiers();

    /**
     * Gets member.
     *
     * @return the member
     */
    @Field.Getter("member")
    MemberNameLinker getMember();
}
