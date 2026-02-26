package io.github.hhy50.linker.generate;


import io.github.hhy50.linker.exceptions.ParseException;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.tools.Pair;
import org.objectweb.asm.Type;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 参数依赖分析
 */
public class ParameterTypeAnalysis {

    /**
     * The Parameter types.
     */
    final Type[] parameterTypes;

    /**
     * Instantiates a new Parameter type analysis.
     *
     * @param parameters the parameters
     */
    public ParameterTypeAnalysis(Parameter[] parameters) {
        this.parameterTypes = new Type[parameters.length];
    }


    /**
     * Analyse.
     *
     * @param parameterIndexTypes the parameter index types
     */
    public void analyse(List<Pair<Integer, Type>> parameterIndexTypes) {
        for (Pair<Integer, Type> entry : parameterIndexTypes) {
            int index = entry.getFirst();
            Type type = entry.getSecond();

            if (index >= this.parameterTypes.length) {
                throw new ParseException("Parameter index out of bounds for parameter: $" + index);
            }

            if (this.parameterTypes[index] != null && !this.parameterTypes[index].equals(type)) {
                this.parameterTypes[index] = ObjectVar.TYPE;
            } else {
                this.parameterTypes[index] = type;
            }
        }
    }

    /**
     * Get parameters type type [ ].
     *
     * @return the type [ ]
     */
    public Type[] getParametersType() {
        return Arrays.stream(this.parameterTypes).filter(Objects::nonNull).toArray(Type[]::new);
    }
}