package io.github.hhy50.linker.syslinker;


import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Target;


//@Runtime

/**
 * The interface Direct method handle linker.
 */
@Target.Bind(value = "java.lang.invoke.DirectMethodHandle")
public interface DirectMethodHandleLinker {

    /**
     * Modifiers int.
     *
     * @return the int
     */
//    @Static(value = false, name = {"member", "flags"})
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
