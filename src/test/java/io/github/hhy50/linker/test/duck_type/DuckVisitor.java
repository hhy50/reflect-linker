package io.github.hhy50.linker.test.duck_type;

import io.github.hhy50.linker.annotations.Runtime;
import io.github.hhy50.linker.annotations.Target;

@Target.Bind("io.github.hhy50.linker.test.duck_type.Duck")
public interface DuckVisitor {

    @Runtime
    String run();
}
