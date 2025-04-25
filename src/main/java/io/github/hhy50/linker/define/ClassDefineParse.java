package io.github.hhy50.linker.define;

import io.github.hhy50.linker.annotations.Target;
import io.github.hhy50.linker.define.field.EarlyFieldRef;
import io.github.hhy50.linker.define.field.FieldRef;
import io.github.hhy50.linker.define.field.RuntimeFieldRef;

import io.github.hhy50.linker.exceptions.ParseException;
import io.github.hhy50.linker.exceptions.VerifyException;
import io.github.hhy50.linker.generate.ClassImplGenerator;
import io.github.hhy50.linker.token.TokenParser;
import io.github.hhy50.linker.util.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static io.github.hhy50.linker.util.AnnotationUtils.isRuntime;
import static io.github.hhy50.linker.util.ClassUtil.getTypeDefines;

/**
 * The type Class define parse.
 */
public class ClassDefineParse {

    private static final Map<String, Map<String, InterfaceImplClassDefine>> PARSED = new HashMap<>();
    private static final TokenParser TOKEN_PARSER = new TokenParser();

    /**
     * Parse class interface impl class define.
     *
     * @param define the define
     * @param cl     the cl
     * @return the interface impl class define
     * @throws ParseException         the parse exception
     * @throws ClassNotFoundException the class not found exception
     * @throws IOException            the io exception
     */
    public static InterfaceImplClassDefine parseClass(Class<?> define, ClassLoader cl) throws ParseException, ClassNotFoundException, IOException {
        Target.Bind bindAnno = define.getDeclaredAnnotation(Target.Bind.class);
        if (bindAnno == null) {
            throw new VerifyException("use @Target.Bind specified a class  \n" +
                    "                  or use @Runtime designated as runtime");
        }
        Class<?> targetClass = cl.loadClass(bindAnno.value());
        return parseClass(define, targetClass);
    }

    /**
     * Parse class interface impl class define.
     *
     * @param define      the define
     * @param targetClass the target class
     * @return interface impl class define
     * @throws ParseException         the parse exception
     * @throws IOException            the io exception
     * @throws ClassNotFoundException the class not found exception
     */
    public static InterfaceImplClassDefine parseClass(Class<?> define, Class<?> targetClass) throws ParseException, IOException, ClassNotFoundException {
//        ClassLoader cl = targetClass.getClassLoader();
//        if (cl == null) cl = ClassLoader.getSystemClassLoader();
        if (isRuntime(define)) {
            targetClass = Object.class;
        }

        String dynKey = targetClass == Object.class ? "runtime" : targetClass.getName().replace('.', '_');
        InterfaceImplClassDefine defineClass = getCache(define, dynKey);
        if (defineClass != null) {
            return defineClass;
        }

        ParseContext parseContext = new ParseContext(define, targetClass);
        parseContext.setClassLoader(targetClass.getClassLoader());
        List<MethodDefine> methods = parseContext.parse();




        defineClass = doParseClass(define, targetClass, cl);
        defineClass.setClassName(define.getName() + "$" + dynKey);

        ClassImplGenerator.generateBytecode(defineClass);
        putCache(define, dynKey, defineClass);
        return defineClass;
    }

    private static InterfaceImplClassDefine getCache(Class<?> define, String dynKey) {
        Map<String, InterfaceImplClassDefine> parsed = PARSED.computeIfAbsent(define.getName(), k -> new HashMap<>());
        return parsed.get(dynKey);
    }

    private static void putCache(Class<?> define, String dynKey, InterfaceImplClassDefine classDefine) {
        Map<String, InterfaceImplClassDefine> parsed = PARSED.computeIfAbsent(define.getName(), k -> new HashMap<>());
        parsed.put(dynKey, classDefine);
    }




}
