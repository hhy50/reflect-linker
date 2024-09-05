package io.github.hhy.linker.syslinker;


import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Runtime;
import io.github.hhy.linker.annotations.Static;
import io.github.hhy.linker.annotations.Target;

@Target.Bind(value = "java.lang.invoke.MethodHandle", runtime = true)
public interface DirectMethodHandleLinker {

    @Static(value = false, name = {"member", "flags"})
    @Runtime
    @Field.Getter("member.flags")
    int modifiers();
}
