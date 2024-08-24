package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.generate.MethodBody;

/**
 *
 */
public interface Action {

    void apply(MethodBody body);
}
