package io.github.hhy50.linker;

import io.github.hhy50.linker.exceptions.LinkerException;

/**
 * <p>AccessTool class.</p>
 *
 * @author hanhaiyang
 * @version $Id : $Id
 */
public class AccessTool {

    /**
     * <p>createSysLinker.</p>
     *
     * @param <T>            a T object.
     * @param sysDefineClass a {@link java.lang.Class} object.
     * @param obj            a {@link java.lang.Object} object.
     * @return a T object.
     * @throws LinkerException the linker exception
     */
    public static <T> T createSysLinker(Class<T> sysDefineClass, Object obj) throws LinkerException {
        return LinkerFactory.createSysLinker(sysDefineClass, obj);
    }
}
