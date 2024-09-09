package io.github.hhy.linker.syslinker;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;


@Target.Bind(value = "java.lang.invoke.MemberName")
public interface MemberNameLinker {

    @Field.Getter("flags")
    int modifiers();
}
