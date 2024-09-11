package io.github.hhy.linker.syslinker;


import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;


//@Runtime
/**
 * <p>DirectMethodHandleLinker interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
@Target.Bind(value = "java.lang.invoke.DirectMethodHandle")
public interface DirectMethodHandleLinker {

//    @Static(value = false, name = {"member", "flags"})
    /**
     * <p>modifiers.</p>
     *
     * @return a int.
     */
    @Field.Getter("member.flags")
    int modifiers();

    /**
     * <p>getMember.</p>
     *
     * @return a {@link io.github.hhy.linker.syslinker.MemberNameLinker} object.
     */
    @Field.Getter("member")
    MemberNameLinker getMember();
}
