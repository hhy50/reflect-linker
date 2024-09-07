package io.github.hhy.linker.syslinker;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Runtime;
import io.github.hhy.linker.annotations.Target;

import java.lang.invoke.MethodHandles;


@Runtime
@Target.Bind("java.lang.invoke.MethodHandles$Lookup")
public interface LookupLinker {

    @Field.Getter("IMPL_LOOKUP")
    MethodHandles.Lookup lookupImpl();
}
