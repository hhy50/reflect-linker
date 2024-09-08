package io.github.hhy.linker.syslinker;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Runtime;
import io.github.hhy.linker.annotations.Target;


@Runtime
@Target.Bind(value = "java.lang.invoke.MethodName")
public interface MethodNameLinker {

    @Field.Getter("flags")
    int modifiers();
}
