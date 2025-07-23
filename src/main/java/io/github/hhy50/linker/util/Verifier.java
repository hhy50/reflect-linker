package io.github.hhy50.linker.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.BiPredicate;

/**
 * The interface Verifier.
 */
public interface Verifier extends BiPredicate<Method, Annotation> {

}
