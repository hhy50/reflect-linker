package io.github.hhy50.linker.token;

import io.github.hhy50.linker.define.ParseContext;
import io.github.hhy50.linker.exceptions.ParseException;
import io.github.hhy50.linker.util.ParseUtil;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * The type Placeholder token.
 */
public class PlaceholderToken implements Token, ArgType {

    /**
     * The Index.
     */
    public int index;

    /**
     * Instantiates a new Placeholder token.
     *
     * @param index the index
     */
    public PlaceholderToken(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "$"+index;
    }

    @Override
    public Type getType(ParseContext context, Method methodDefine) {
        Parameter[] parameterTypes = methodDefine.getParameters();
        if (parameterTypes.length-1 < index) {
            throw new ParseException("Invalid placeholder index");
        }
        return TypeUtil.getType(ParseUtil.getRawType(parameterTypes[index]));
    }
}
