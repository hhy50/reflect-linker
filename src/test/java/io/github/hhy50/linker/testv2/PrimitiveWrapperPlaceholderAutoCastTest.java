package io.github.hhy50.linker.testv2;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class PrimitiveWrapperPlaceholderAutoCastTest {

    public interface PrimitiveWrapperPlaceholderLinker {

        @Method.Expr("acceptBooleanPrimitive($0).acceptBooleanWrapper($0).readLog()")
        String booleanParamToPrimitiveThenWrapper(boolean value);

        @Method.Expr("acceptBooleanPrimitive($0).acceptBooleanWrapper($0).readLog()")
        String booleanWrapperParamToPrimitiveThenWrapper(Boolean value);

        @Method.Expr("acceptBooleanWrapper($0).acceptBooleanPrimitive($0).readLog()")
        String booleanParamToWrapperThenPrimitive(boolean value);

        @Method.Expr("acceptBooleanWrapper($0).acceptBooleanPrimitive($0).readLog()")
        String booleanWrapperParamToWrapperThenPrimitive(Boolean value);

        @Method.Expr("acceptCharPrimitive($0).acceptCharWrapper($0).readLog()")
        String charParamToPrimitiveThenWrapper(char value);

        @Method.Expr("acceptCharPrimitive($0).acceptCharWrapper($0).readLog()")
        String characterParamToPrimitiveThenWrapper(Character value);

        @Method.Expr("acceptCharWrapper($0).acceptCharPrimitive($0).readLog()")
        String charParamToWrapperThenPrimitive(char value);

        @Method.Expr("acceptCharWrapper($0).acceptCharPrimitive($0).readLog()")
        String characterParamToWrapperThenPrimitive(Character value);

        @Method.Expr("acceptBytePrimitive($0).acceptByteWrapper($0).readLog()")
        String byteParamToPrimitiveThenWrapper(byte value);

        @Method.Expr("acceptBytePrimitive($0).acceptByteWrapper($0).readLog()")
        String byteWrapperParamToPrimitiveThenWrapper(Byte value);

        @Method.Expr("acceptByteWrapper($0).acceptBytePrimitive($0).readLog()")
        String byteParamToWrapperThenPrimitive(byte value);

        @Method.Expr("acceptByteWrapper($0).acceptBytePrimitive($0).readLog()")
        String byteWrapperParamToWrapperThenPrimitive(Byte value);

        @Method.Expr("acceptShortPrimitive($0).acceptShortWrapper($0).readLog()")
        String shortParamToPrimitiveThenWrapper(short value);

        @Method.Expr("acceptShortPrimitive($0).acceptShortWrapper($0).readLog()")
        String shortWrapperParamToPrimitiveThenWrapper(Short value);

        @Method.Expr("acceptShortWrapper($0).acceptShortPrimitive($0).readLog()")
        String shortParamToWrapperThenPrimitive(short value);

        @Method.Expr("acceptShortWrapper($0).acceptShortPrimitive($0).readLog()")
        String shortWrapperParamToWrapperThenPrimitive(Short value);

        @Method.Expr("acceptIntPrimitive($0).acceptIntWrapper($0).readLog()")
        String intParamToPrimitiveThenWrapper(int value);

        @Method.Expr("acceptIntPrimitive($0).acceptIntWrapper($0).readLog()")
        String integerParamToPrimitiveThenWrapper(Integer value);

        @Method.Expr("acceptIntWrapper($0).acceptIntPrimitive($0).readLog()")
        String intParamToWrapperThenPrimitive(int value);

        @Method.Expr("acceptIntWrapper($0).acceptIntPrimitive($0).readLog()")
        String integerParamToWrapperThenPrimitive(Integer value);

        @Method.Expr("acceptLongPrimitive($0).acceptLongWrapper($0).readLog()")
        String longParamToPrimitiveThenWrapper(long value);

        @Method.Expr("acceptLongPrimitive($0).acceptLongWrapper($0).readLog()")
        String longWrapperParamToPrimitiveThenWrapper(Long value);

        @Method.Expr("acceptLongWrapper($0).acceptLongPrimitive($0).readLog()")
        String longParamToWrapperThenPrimitive(long value);

        @Method.Expr("acceptLongWrapper($0).acceptLongPrimitive($0).readLog()")
        String longWrapperParamToWrapperThenPrimitive(Long value);

        @Method.Expr("acceptFloatPrimitive($0).acceptFloatWrapper($0).readLog()")
        String floatParamToPrimitiveThenWrapper(float value);

        @Method.Expr("acceptFloatPrimitive($0).acceptFloatWrapper($0).readLog()")
        String floatWrapperParamToPrimitiveThenWrapper(Float value);

        @Method.Expr("acceptFloatWrapper($0).acceptFloatPrimitive($0).readLog()")
        String floatParamToWrapperThenPrimitive(float value);

        @Method.Expr("acceptFloatWrapper($0).acceptFloatPrimitive($0).readLog()")
        String floatWrapperParamToWrapperThenPrimitive(Float value);

        @Method.Expr("acceptDoublePrimitive($0).acceptDoubleWrapper($0).readLog()")
        String doubleParamToPrimitiveThenWrapper(double value);

        @Method.Expr("acceptDoublePrimitive($0).acceptDoubleWrapper($0).readLog()")
        String doubleWrapperParamToPrimitiveThenWrapper(Double value);

        @Method.Expr("acceptDoubleWrapper($0).acceptDoublePrimitive($0).readLog()")
        String doubleParamToWrapperThenPrimitive(double value);

        @Method.Expr("acceptDoubleWrapper($0).acceptDoublePrimitive($0).readLog()")
        String doubleWrapperParamToWrapperThenPrimitive(Double value);
    }

    public static class PrimitiveWrapperPlaceholderTarget {
        private String log;

        public PrimitiveWrapperPlaceholderTarget acceptBooleanPrimitive(boolean value) {
            append("boolean=" + value);
            return this;
        }

        public PrimitiveWrapperPlaceholderTarget acceptBooleanWrapper(Boolean value) {
            append("Boolean=" + value);
            return this;
        }

        public PrimitiveWrapperPlaceholderTarget acceptCharPrimitive(char value) {
            append("char=" + value);
            return this;
        }

        public PrimitiveWrapperPlaceholderTarget acceptCharWrapper(Character value) {
            append("Character=" + value);
            return this;
        }

        public PrimitiveWrapperPlaceholderTarget acceptBytePrimitive(byte value) {
            append("byte=" + value);
            return this;
        }

        public PrimitiveWrapperPlaceholderTarget acceptByteWrapper(Byte value) {
            append("Byte=" + value);
            return this;
        }

        public PrimitiveWrapperPlaceholderTarget acceptShortPrimitive(short value) {
            append("short=" + value);
            return this;
        }

        public PrimitiveWrapperPlaceholderTarget acceptShortWrapper(Short value) {
            append("Short=" + value);
            return this;
        }

        public PrimitiveWrapperPlaceholderTarget acceptIntPrimitive(int value) {
            append("int=" + value);
            return this;
        }

        public PrimitiveWrapperPlaceholderTarget acceptIntWrapper(Integer value) {
            append("Integer=" + value);
            return this;
        }

        public PrimitiveWrapperPlaceholderTarget acceptLongPrimitive(long value) {
            append("long=" + value);
            return this;
        }

        public PrimitiveWrapperPlaceholderTarget acceptLongWrapper(Long value) {
            append("Long=" + value);
            return this;
        }

        public PrimitiveWrapperPlaceholderTarget acceptFloatPrimitive(float value) {
            append("float=" + value);
            return this;
        }

        public PrimitiveWrapperPlaceholderTarget acceptFloatWrapper(Float value) {
            append("Float=" + value);
            return this;
        }

        public PrimitiveWrapperPlaceholderTarget acceptDoublePrimitive(double value) {
            append("double=" + value);
            return this;
        }

        public PrimitiveWrapperPlaceholderTarget acceptDoubleWrapper(Double value) {
            append("Double=" + value);
            return this;
        }

        private void append(String item) {
            if (this.log == null) {
                this.log = item;
            } else {
                this.log += "," + item;
            }
        }

        public String readLog() {
            String current = this.log;
            this.log = null;
            return current;
        }
    }

    @Test
    public void shouldAutoCastSamePlaceholderBetweenPrimitiveAndWrapperArguments() throws LinkerException {
        PrimitiveWrapperPlaceholderLinker linker = LinkerFactory.createLinker(
                PrimitiveWrapperPlaceholderLinker.class,
                new PrimitiveWrapperPlaceholderTarget()
        );

        Assert.assertEquals("boolean=true,Boolean=true", linker.booleanParamToPrimitiveThenWrapper(true));
        Assert.assertEquals("boolean=true,Boolean=true", linker.booleanWrapperParamToPrimitiveThenWrapper(Boolean.TRUE));
        Assert.assertEquals("Boolean=true,boolean=true", linker.booleanParamToWrapperThenPrimitive(true));
        Assert.assertEquals("Boolean=true,boolean=true", linker.booleanWrapperParamToWrapperThenPrimitive(Boolean.TRUE));

        Assert.assertEquals("char=Z,Character=Z", linker.charParamToPrimitiveThenWrapper('Z'));
        Assert.assertEquals("char=Z,Character=Z", linker.characterParamToPrimitiveThenWrapper('Z'));
        Assert.assertEquals("Character=Z,char=Z", linker.charParamToWrapperThenPrimitive('Z'));
        Assert.assertEquals("Character=Z,char=Z", linker.characterParamToWrapperThenPrimitive('Z'));

        Assert.assertEquals("byte=12,Byte=12", linker.byteParamToPrimitiveThenWrapper((byte) 12));
        Assert.assertEquals("byte=12,Byte=12", linker.byteWrapperParamToPrimitiveThenWrapper((byte) 12));
        Assert.assertEquals("Byte=12,byte=12", linker.byteParamToWrapperThenPrimitive((byte) 12));
        Assert.assertEquals("Byte=12,byte=12", linker.byteWrapperParamToWrapperThenPrimitive((byte) 12));

        Assert.assertEquals("short=1234,Short=1234", linker.shortParamToPrimitiveThenWrapper((short) 1234));
        Assert.assertEquals("short=1234,Short=1234", linker.shortWrapperParamToPrimitiveThenWrapper((short) 1234));
        Assert.assertEquals("Short=1234,short=1234", linker.shortParamToWrapperThenPrimitive((short) 1234));
        Assert.assertEquals("Short=1234,short=1234", linker.shortWrapperParamToWrapperThenPrimitive((short) 1234));

        Assert.assertEquals("int=123456,Integer=123456", linker.intParamToPrimitiveThenWrapper(123456));
        Assert.assertEquals("int=123456,Integer=123456", linker.integerParamToPrimitiveThenWrapper(123456));
        Assert.assertEquals("Integer=123456,int=123456", linker.intParamToWrapperThenPrimitive(123456));
        Assert.assertEquals("Integer=123456,int=123456", linker.integerParamToWrapperThenPrimitive(123456));

        Assert.assertEquals("long=123456789,Long=123456789", linker.longParamToPrimitiveThenWrapper(123456789L));
        Assert.assertEquals("long=123456789,Long=123456789", linker.longWrapperParamToPrimitiveThenWrapper(123456789L));
        Assert.assertEquals("Long=123456789,long=123456789", linker.longParamToWrapperThenPrimitive(123456789L));
        Assert.assertEquals("Long=123456789,long=123456789", linker.longWrapperParamToWrapperThenPrimitive(123456789L));

        Assert.assertEquals("float=12.5,Float=12.5", linker.floatParamToPrimitiveThenWrapper(12.5f));
        Assert.assertEquals("float=12.5,Float=12.5", linker.floatWrapperParamToPrimitiveThenWrapper(12.5f));
        Assert.assertEquals("Float=12.5,float=12.5", linker.floatParamToWrapperThenPrimitive(12.5f));
        Assert.assertEquals("Float=12.5,float=12.5", linker.floatWrapperParamToWrapperThenPrimitive(12.5f));

        Assert.assertEquals("double=12.75,Double=12.75", linker.doubleParamToPrimitiveThenWrapper(12.75d));
        Assert.assertEquals("double=12.75,Double=12.75", linker.doubleWrapperParamToPrimitiveThenWrapper(12.75d));
        Assert.assertEquals("Double=12.75,double=12.75", linker.doubleParamToWrapperThenPrimitive(12.75d));
        Assert.assertEquals("Double=12.75,double=12.75", linker.doubleWrapperParamToWrapperThenPrimitive(12.75d));
    }
}
