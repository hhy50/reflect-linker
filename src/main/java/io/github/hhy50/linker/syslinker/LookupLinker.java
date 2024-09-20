package io.github.hhy50.linker.syslinker;

import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Target;

import java.lang.invoke.MethodHandles;


/**
 * <p>LookupLinker interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
@Target.Bind("java.lang.invoke.MethodHandles$Lookup")
public interface LookupLinker {

    /**
     * <p>lookupImpl.</p>
     *
     * @return a {@link java.lang.invoke.MethodHandles.Lookup} object.
     */
    @Field.Getter("IMPL_LOOKUP")
    MethodHandles.Lookup lookupImpl();
}
