package io.github.hhy50.linker.test.generic;

import io.github.hhy50.linker.annotations.Field;

import java.util.List;

public interface GenericTypeLinker {

    @Field.Getter("users")
    public List<Object> getUsers();
}
