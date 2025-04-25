package io.github.hhy50.linker.define;

import io.github.hhy50.linker.annotations.Target;
import io.github.hhy50.linker.exceptions.ParseException;
import io.github.hhy50.linker.exceptions.VerifyException;
import io.github.hhy50.linker.generate.ClassImplGenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.hhy50.linker.util.AnnotationUtils.isRuntime;

/**
 * The type Class define parse.
 */
public class ClassDefineParse {

    private static final Map<String, Map<String, InterfaceImplClass>> PARSED = new HashMap<>();

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
    public static InterfaceImplClass parseClass(Class<?> define, ClassLoader cl) throws ParseException, ClassNotFoundException, IOException {
        Target.Bind bindAnno = define.getDeclaredAnnotation(Target.Bind.class);
        if (bindAnno == null) {
            throw new VerifyException("use @Target.Bind specified a class  \n"+
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
    public static InterfaceImplClass parseClass(Class<?> define, Class<?> targetClass) throws ParseException, IOException, ClassNotFoundException {
        if (isRuntime(define)) {
            targetClass = Object.class;
        }

        String dynKey = targetClass == Object.class ? "runtime" : targetClass.getName().replace('.', '_');
        InterfaceImplClass defineClass = getCache(define, dynKey);
        if (defineClass != null) {
            return defineClass;
        }

        ParseContext parseContext = new ParseContext(define, targetClass);
        parseContext.setClassLoader(targetClass.getClassLoader());
        List<MethodDefine> absMethods = parseContext.parse();

        defineClass = new InterfaceImplClass(define.getName()+"$"+dynKey, absMethods);
        ClassImplGenerator.generateBytecode(define, targetClass, defineClass);
        putCache(define, dynKey, defineClass);
        return defineClass;
    }

    private static InterfaceImplClass getCache(Class<?> define, String dynKey) {
        Map<String, InterfaceImplClass> parsed = PARSED.computeIfAbsent(define.getName(), k -> new HashMap<>());
        return parsed.get(dynKey);
    }

    private static void putCache(Class<?> define, String dynKey, InterfaceImplClass classDefine) {
        Map<String, InterfaceImplClass> parsed = PARSED.computeIfAbsent(define.getName(), k -> new HashMap<>());
        parsed.put(dynKey, classDefine);
    }
}
