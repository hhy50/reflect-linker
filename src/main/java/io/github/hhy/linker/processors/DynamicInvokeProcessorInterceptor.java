package io.github.hhy.linker.processors;

import io.github.hhy.linker.define.MethodDefine;


public class DynamicInvokeProcessorInterceptor implements ProcessorInterceptor {

    @Override
    public MethodDefine intercept(MethodDefine define) {
        return define;
    }
}
