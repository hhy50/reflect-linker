package io.github.hhy50.linker.generate.bytecode.action;


public interface InlineAction {

    Action invoke(Action... args);
}
