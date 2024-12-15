package io.github.hhy50.linker.test.duck_type;

import io.github.hhy50.linker.annotations.Runtime;

public interface DuckVisitor {

    @Runtime
    String run();
}
