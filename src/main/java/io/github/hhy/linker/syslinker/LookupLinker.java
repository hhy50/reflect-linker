package io.github.hhy.linker.syslinker;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Runtime;
import io.github.hhy.linker.annotations.Static;
import io.github.hhy.linker.annotations.Target;

import java.lang.invoke.MethodHandles;


/**
 * <p>LookupLinker interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
@Runtime
@Target.Bind("java.lang.invoke.MethodHandles$Lookup")
public interface LookupLinker {

    /**
     * <p>lookupImpl.</p>
     *
     * @return a {@link java.lang.invoke.MethodHandles.Lookup} object.
     */
    @Static(name = "IMPL_LOOKUP")
    @Field.Getter("IMPL_LOOKUP")
    MethodHandles.Lookup lookupImpl();
}
