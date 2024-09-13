package io.github.hhy50.linker.syslinker;

import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Target;


/**
 * <p>MemberNameLinker interface.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
@Target.Bind(value = "java.lang.invoke.MemberName")
public interface MemberNameLinker {

    /**
     * <p>modifiers.</p>
     *
     * @return a int.
     */
    @Field.Getter("flags")
    int modifiers();
}
