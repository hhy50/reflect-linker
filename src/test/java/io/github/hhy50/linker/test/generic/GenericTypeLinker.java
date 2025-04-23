package io.github.hhy50.linker.test.generic;

import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Target;

import java.util.List;

public interface GenericTypeLinker {

    @Target.Bind("io.github.hhy50.linker.test.generic.GenericType$User")
    interface LUser {

    }

    @Field.Getter("users")
    public List<LUser> getUsers();
}
