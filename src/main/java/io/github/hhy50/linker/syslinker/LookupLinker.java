package io.github.hhy50.linker.syslinker;

import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Target;

import java.lang.invoke.MethodHandles;


/**
 * The interface Lookup linker.
 */
@Target.Bind("java.lang.invoke.MethodHandles$Lookup")
public interface LookupLinker {

    /**
     * Lookup method handles . lookup.
     *
     * @return the method handles . lookup
     */
    @Field.Getter("IMPL_LOOKUP")
    MethodHandles.Lookup lookupImpl();
}
