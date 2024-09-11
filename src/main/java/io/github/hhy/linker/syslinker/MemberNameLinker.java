package io.github.hhy.linker.syslinker;

import io.github.hhy.linker.annotations.Field;
import io.github.hhy.linker.annotations.Target;


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
