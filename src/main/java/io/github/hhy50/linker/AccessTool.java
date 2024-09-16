package io.github.hhy50.linker;

import io.github.hhy50.linker.exceptions.LinkerException;

/**
 * <p>AccessTool class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class AccessTool {

    /**
     * <p>createSysLinker.</p>
     *
     * @param sysDefineClass a {@link java.lang.Class} object.
     * @param obj a {@link java.lang.Object} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws io.github.hhy50.linker.exceptions.LinkerException if any.
     */
    public static <T> T createSysLinker(Class<T> sysDefineClass, Object obj) throws LinkerException {
        return LinkerFactory.createSysLinker(sysDefineClass, obj);
    }
}
