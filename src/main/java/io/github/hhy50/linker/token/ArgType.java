package io.github.hhy50.linker.token;

import io.github.hhy50.linker.define.ParseContext;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

public interface ArgType {

    Type getType(ParseContext context, Method methodDefine);
}
