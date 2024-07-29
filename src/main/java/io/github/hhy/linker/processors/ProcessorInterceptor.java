package io.github.hhy.linker.processors;

import io.github.hhy.linker.define.MethodDefine;

public interface ProcessorInterceptor {

    public MethodDefine intercept(MethodDefine define);
}
