package io.github.hhy50.linker.test.v2;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class PrimitiveWrapperReturnAutoCastTest {

    public interface PrimitiveWrapperReturnLinker {

        @Method.Expr("booleanPrimitiveValue()")
        Boolean readBooleanPrimitiveAsWrapper();

        @Method.Expr("booleanWrapperValue()")
        boolean readBooleanWrapperAsPrimitive();

        @Method.Expr("charPrimitiveValue()")
        Character readCharPrimitiveAsWrapper();

        @Method.Expr("charWrapperValue()")
        char readCharWrapperAsPrimitive();

        @Method.Expr("bytePrimitiveValue()")
        Byte readBytePrimitiveAsWrapper();

        @Method.Expr("byteWrapperValue()")
        byte readByteWrapperAsPrimitive();

        @Method.Expr("shortPrimitiveValue()")
        Short readShortPrimitiveAsWrapper();

        @Method.Expr("shortWrapperValue()")
        short readShortWrapperAsPrimitive();

        @Method.Expr("intPrimitiveValue()")
        Integer readIntPrimitiveAsWrapper();

        @Method.Expr("intWrapperValue()")
        int readIntWrapperAsPrimitive();

        @Method.Expr("longPrimitiveValue()")
        Long readLongPrimitiveAsWrapper();

        @Method.Expr("longWrapperValue()")
        long readLongWrapperAsPrimitive();

        @Method.Expr("floatPrimitiveValue()")
        Float readFloatPrimitiveAsWrapper();

        @Method.Expr("floatWrapperValue()")
        float readFloatWrapperAsPrimitive();

        @Method.Expr("doublePrimitiveValue()")
        Double readDoublePrimitiveAsWrapper();

        @Method.Expr("doubleWrapperValue()")
        double readDoubleWrapperAsPrimitive();
    }

    public static class PrimitiveWrapperReturnTarget {

        public boolean booleanPrimitiveValue() {
            return true;
        }

        public Boolean booleanWrapperValue() {
            return Boolean.FALSE;
        }

        public char charPrimitiveValue() {
            return 'P';
        }

        public Character charWrapperValue() {
            return 'W';
        }

        public byte bytePrimitiveValue() {
            return 11;
        }

        public Byte byteWrapperValue() {
            return 22;
        }

        public short shortPrimitiveValue() {
            return 111;
        }

        public Short shortWrapperValue() {
            return 222;
        }

        public int intPrimitiveValue() {
            return 1111;
        }

        public Integer intWrapperValue() {
            return 2222;
        }

        public long longPrimitiveValue() {
            return 11111L;
        }

        public Long longWrapperValue() {
            return 22222L;
        }

        public float floatPrimitiveValue() {
            return 11.5f;
        }

        public Float floatWrapperValue() {
            return 22.5f;
        }

        public double doublePrimitiveValue() {
            return 11.75d;
        }

        public Double doubleWrapperValue() {
            return 22.75d;
        }
    }

    @Test
    public void shouldAutoCastReturnValuesBetweenPrimitiveAndWrapper() throws LinkerException {
        PrimitiveWrapperReturnLinker linker = LinkerFactory.createLinker(
                PrimitiveWrapperReturnLinker.class,
                new PrimitiveWrapperReturnTarget()
        );

        Assert.assertEquals(Boolean.TRUE, linker.readBooleanPrimitiveAsWrapper());
        Assert.assertFalse(linker.readBooleanWrapperAsPrimitive());

        Assert.assertEquals(Character.valueOf('P'), linker.readCharPrimitiveAsWrapper());
        Assert.assertEquals('W', linker.readCharWrapperAsPrimitive());

        Assert.assertEquals(Byte.valueOf((byte) 11), linker.readBytePrimitiveAsWrapper());
        Assert.assertEquals((byte) 22, linker.readByteWrapperAsPrimitive());

        Assert.assertEquals(Short.valueOf((short) 111), linker.readShortPrimitiveAsWrapper());
        Assert.assertEquals((short) 222, linker.readShortWrapperAsPrimitive());

        Assert.assertEquals(Integer.valueOf(1111), linker.readIntPrimitiveAsWrapper());
        Assert.assertEquals(2222, linker.readIntWrapperAsPrimitive());

        Assert.assertEquals(Long.valueOf(11111L), linker.readLongPrimitiveAsWrapper());
        Assert.assertEquals(22222L, linker.readLongWrapperAsPrimitive());

        Assert.assertEquals(Float.valueOf(11.5f), linker.readFloatPrimitiveAsWrapper());
        Assert.assertTrue(Float.valueOf(linker.readFloatWrapperAsPrimitive()).equals(22.5f));

        Assert.assertEquals(Double.valueOf(11.75d), linker.readDoublePrimitiveAsWrapper());
        Assert.assertTrue(Double.valueOf(linker.readDoubleWrapperAsPrimitive()).equals(22.75d));
    }
}
