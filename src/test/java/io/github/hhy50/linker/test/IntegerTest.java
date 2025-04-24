package io.github.hhy50.linker.test;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>MyIntegerTest class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 * @since 1.0.0
 */
public class IntegerTest {


    /**
     * <p>test.</p>
     *
     * @throws LinkerException if any.
     */
    @Test
    public void test() throws LinkerException {
        LInteger myInteger = LinkerFactory.createLinker(LInteger.class, new Integer(10));
        Assert.assertEquals(myInteger, 10);
    }
}
