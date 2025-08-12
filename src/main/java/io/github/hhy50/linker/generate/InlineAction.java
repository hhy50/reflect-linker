package io.github.hhy50.linker.generate;


import io.github.hhy50.linker.generate.bytecode.action.Action;

public interface InlineAction {

    Action invoke(Action... args);
}
