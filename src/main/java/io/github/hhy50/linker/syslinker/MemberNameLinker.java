package io.github.hhy50.linker.syslinker;

import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Target;


/**
 * The interface Member name linker.
 */
@Target.Bind(value = "java.lang.invoke.MemberName")
public interface MemberNameLinker {

    /**
     * Modifiers int.
     *
     * @return the int
     */
    @Field.Getter("flags")
    int modifiers();
}
