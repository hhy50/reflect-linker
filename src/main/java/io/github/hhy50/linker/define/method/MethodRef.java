package io.github.hhy50.linker.define.method;

import io.github.hhy50.linker.generate.MethodHandle;
import io.github.hhy50.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy50.linker.token.ArgsToken;
import io.github.hhy50.linker.token.PlaceholderToken;
import io.github.hhy50.linker.token.Token;
import io.github.hhy50.linker.tools.Pair;
import io.github.hhy50.linker.util.TypeUtil;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * The type Method ref.
 */
public abstract class MethodRef {
    /**
     * The Name.
     */
    protected String name;

    /**
     * The Super class.
     */
    protected String superClass;

    /**
     *
     */
    private ArgsToken argsToken;

    /**
     * 数组访问
     */
    private List<Object> indexs;

    /**
     *
     */
    private boolean nullable;

    /**
     * Instantiates a new Method ref.
     *
     * @param name the name
     */
    public MethodRef(String name) {
        this.name = name;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets super class.
     *
     * @return the super class
     */
    public String getSuperClass() {
        return superClass;
    }

    /**
     * Sets super class.
     *
     * @param superClass the super class
     */
    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    /**
     * Define invoker method handle.
     *
     * @return the method handle
     */
    public abstract MethodHandle defineInvoker();


    /**
     * 这个方法返回来的类型用来定位具体的methodhandle, 所以类型是具体的类型
     *
     * @return the lookup type
     */
    public abstract Type getLookupType();

    /**
     * Gets generic type.
     *
     * @return the generic type
     */
    public Type getGenericType() {
        return getLookupType();
    }

    /**
     * Sets args token.
     *
     * @param argsToken the args token
     */
    public void setArgsToken(ArgsToken argsToken) {
        this.argsToken = argsToken;
    }


    /**
     * Gets args token.
     *
     * @return the args token
     */
    public ArgsToken getArgsToken() {
        return argsToken;
    }

    /**
     * Sets indexs.
     *
     * @param indexs the indexs
     */
    public void setIndexs(List<Object> indexs) {
        this.indexs = indexs;
    }

    /**
     * Gets indexs.
     *
     * @return the indexs
     */
    public List<Object> getIndexs() {
        return indexs;
    }

    /**
     * Is nullable boolean.
     *
     * @return the boolean
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Sets nullable.
     *
     * @param nullable the nullable
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    /**
     * Gets args index table.
     *
     * @return the args index table
     */
    @SuppressWarnings("unchecked")
    protected List<Pair<Integer, Type>> getArgsIndexTable() {
        Type genericType = getGenericType();
        Type[] argumentTypes = genericType.getArgumentTypes();
        if (this.argsToken == null || this.argsToken.isPlaceholderAll()) {
            return IntStream.range(0, argumentTypes.length).mapToObj(i -> Pair.of(i, argumentTypes[i])).collect(Collectors.toList());
        }
        List<Pair<Integer, Type>> its = new ArrayList<>();
        for (int i = 0; i < argumentTypes.length; i++) {
            Type argumentType = argumentTypes[i];
            Token token = this.argsToken.get(i);
            if (token instanceof PlaceholderToken) {
                its.add(Pair.of(((PlaceholderToken) token).index, argumentType));
            }
        }
        return its;
    }

    /**
     * 返回表达式的最后一个返回值的类型
     *
     * @return the return type
     */
    Type getReturnType() {
        Type t = this.getGenericType().getReturnType();
        if (this.indexs == null || this.indexs.isEmpty()) {
            return t;
        }
        if (TypeUtil.getDimensions(t) >= this.indexs.size()) {
            return Type.getType(t.getDescriptor().substring(this.indexs.size()));
        }
        return ObjectVar.TYPE;
    }
}
