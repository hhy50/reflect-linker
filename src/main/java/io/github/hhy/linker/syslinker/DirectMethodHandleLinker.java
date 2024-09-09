package io.github.hhy.linker.syslinker;


import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;


//@Runtime
@Target.Bind(value = "java.lang.invoke.DirectMethodHandle")
public interface DirectMethodHandleLinker {

//    @Static(value = false, name = {"member", "flags"})
    @Field.Getter("member.flags")
    int modifiers();

    @Field.Getter("member")
    MemberNameLinker getMember();
}