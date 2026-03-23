package io.github.hhy50.linker.define;

import io.github.hhy50.linker.annotations.Target;
import io.github.hhy50.linker.define.md.AbsInterfaceMetadata;
import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.exceptions.ParseException;
import io.github.hhy50.linker.exceptions.VerifyException;
import io.github.hhy50.linker.generate.ClassImplGenerator;
import io.github.hhy50.linker.generate.builtin.RuntimeProvider;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Class define parse.
 */
public class ClassDefineParser {

    private static final Map<String, SoftReference<GeneratedClass>> PARSED = new HashMap<>();

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
    public static GeneratedClass parseClass(Class<?> define, ClassLoader cl) throws ParseException, ClassNotFoundException, IOException {
        Target.Bind bindAnno = define.getDeclaredAnnotation(Target.Bind.class);
        if (bindAnno == null) {
            throw new VerifyException("use @Target.Bind specified a class \n" +
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
    public static GeneratedClass parseClass(Class<?> define, Class<?> targetClass) throws ParseException, IOException, ClassNotFoundException {
        AbsInterfaceMetadata classMetadata = new AbsInterfaceMetadata(define, targetClass);
        boolean runtime = classMetadata.isRuntime();
        Class<?> parseTargetClass = runtime ? Object.class : targetClass;
        String dynKey = parseTargetClass == Object.class ? "runtime" : parseTargetClass.getName().replace('.', '_');
        String implClassName = define.getName() + "$" + dynKey;

        GeneratedClass cached = getCached(implClassName);
        if (cached != null) {
            return cached;
        }

        ParseContext parseContext = new ParseContext(classMetadata, parseTargetClass);
        parseContext.setClassLoader(parseTargetClass.getClassLoader());
        List<MethodExprRef> absMethods = parseContext.parse();

        List<Class<?>> interfaces = new ArrayList<>();
        interfaces.add(define);
        if (runtime) {
            interfaces.add(RuntimeProvider.class);
        }

        GeneratedClass generatedClass = ClassImplGenerator.generateBytecode(classMetadata, implClassName, absMethods, interfaces);
        PARSED.put(implClassName, new SoftReference<>(generatedClass));
        return generatedClass;
    }

    private static GeneratedClass getCached(String key) {
        synchronized (PARSED) {
            SoftReference<GeneratedClass> defineCache = PARSED.get(key);
            if (defineCache == null) {
                return null;
            }

            GeneratedClass generatedClass = defineCache.get();
            if (generatedClass == null) {
                PARSED.remove(key);
            }
            return generatedClass;
        }
    }
}
