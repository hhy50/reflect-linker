package io.github.hhy50.linker.util;

import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.asm.AsmField;
import io.github.hhy50.linker.define.MethodDescriptor;
import io.github.hhy50.linker.generate.bytecode.action.Action;
import io.github.hhy50.linker.generate.bytecode.action.Actions;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.utils.Members;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.stream.IntStream;

import static io.github.hhy50.linker.generate.bytecode.action.Actions.multi;

/**
 * The type Bytecode util.
 */
public class BytecodeUtil {

    /**
     * 注入字段的getter方法
     * @param classBuilder the class builder
     * @param fieldName the field name
     * @param getterName the getter name
     */
    public static void injectGetter(AsmClassBuilder classBuilder, String fieldName, String getterName) {
        AsmField field = classBuilder.getField(fieldName);
        if (field == null) {
            throw new RuntimeException("Field not found: " + fieldName);
        }
        if (getterName == null) {
            if (field.type == Type.BOOLEAN_TYPE) {
                getterName = "is" + toUpperCamelCase(fieldName);
            } else {
                getterName = "get" + toUpperCamelCase(fieldName);
            }
        }
        classBuilder.defineMethod(Opcodes.ACC_PUBLIC, getterName, Type.getMethodType(field.type), null)
                .intercept(Members.load(fieldName).thenReturn());
    }

    /**
     * 注入字段的setter方法
     * @param classBuilder the class builder
     * @param fieldName the field name
     * @param setterName the setter name
     */
    public static void injectSetter(AsmClassBuilder classBuilder, String fieldName, String setterName) {
        AsmField field = classBuilder.getField(fieldName);
        if (field == null) {
            throw new RuntimeException("Field not found: " + fieldName);
        }
        if (setterName == null) {
            setterName = "set" + toUpperCamelCase(fieldName);
        }
        classBuilder.defineMethod(Opcodes.ACC_PUBLIC, setterName, Type.getMethodType(Type.VOID_TYPE, field.type), null)
                .intercept(Members.ofStore(fieldName, Args.of(0)), Actions.vreturn());
    }

    /**
     * Inject getter and setter.
     *
     * @param classBuilder the class builder
     * @param fieldName the field name
     */
    public static void injectGetterAndSetter(AsmClassBuilder classBuilder, String fieldName) {
        injectGetter(classBuilder, fieldName, "get" + fieldName);
        injectSetter(classBuilder, fieldName, "set" + fieldName);
    }

    /**
     * Inject construct.
     *
     * @param classBuilder the class builder
     */
    public static void injectConstruct(AsmClassBuilder classBuilder) {
        // 空参构造
        classBuilder.defineConstruct(Opcodes.ACC_PUBLIC)
                .intercept(Methods.invokeSuper().thenReturn());
        // 带参构造
        List<AsmField> fields = classBuilder.getFields();
        if (!fields.isEmpty()) {
            int size = fields.size();
            Action[] stores = IntStream.range(0, size)
                    .mapToObj(i -> Members.ofStore(fields.get(i).name, Args.of(i))).toArray(Action[]::new);

            classBuilder.defineConstruct(Opcodes.ACC_PUBLIC, fields.stream().map(f -> f.type).toArray(Type[]::new))
                    .intercept(Methods.invokeSuper(MethodDescriptor.ofConstructor()),
                            multi(stores),
                            Actions.vreturn());
        }
    }

    /**
     * To upper camel case string.
     *
     * @param name the name
     * @return  the string
     */
    static String toUpperCamelCase(String name) {
        char[] charArray = name.toCharArray();
        charArray[0] = Character.toUpperCase(charArray[0]);
        return new String(charArray);
    }
}
