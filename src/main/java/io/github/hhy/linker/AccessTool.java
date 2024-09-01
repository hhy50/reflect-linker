package io.github.hhy.linker;

import io.github.hhy.linker.exceptions.LinkerException;

public class AccessTool {

    public static <T> T createSysLinker(Class<T> sysDefineClass, Object obj) throws LinkerException {
        return LinkerFactory.createSysLinker(sysDefineClass, obj);
    }
}
