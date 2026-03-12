package io.github.hhy50.linker.test.testv2;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class ObjectWrapperAutoCastTest {

    public interface ObjectWrapperLinker {

        @Method.Expr("acceptObject($0).acceptInteger($0).readLog()")
        String integerParamToObjectThenInteger(Integer value);

        @Method.Expr("acceptInteger($0).acceptObject($0).readLog()")
        String integerParamToIntegerThenObject(Integer value);

        @Method.Expr("acceptObject($0).acceptInteger($0).readLog()")
        String objectParamToObjectThenInteger(Object value);

        @Method.Expr("acceptInteger($0).acceptObject($0).readLog()")
        String objectParamToIntegerThenObject(Object value);

        @Method.Expr("acceptObject($0).acceptBoolean($0).readLog()")
        String booleanParamToObjectThenBoolean(Boolean value);

        @Method.Expr("acceptBoolean($0).acceptObject($0).readLog()")
        String booleanParamToBooleanThenObject(Boolean value);

        @Method.Expr("acceptObject($0).acceptBoolean($0).readLog()")
        String objectParamToObjectThenBoolean(Object value);

        @Method.Expr("acceptBoolean($0).acceptObject($0).readLog()")
        String objectParamToBooleanThenObject(Object value);

        @Method.Expr("acceptObject($0).acceptDouble($0).readLog()")
        String doubleParamToObjectThenDouble(Double value);

        @Method.Expr("acceptDouble($0).acceptObject($0).readLog()")
        String doubleParamToDoubleThenObject(Double value);

        @Method.Expr("acceptObject($0).acceptDouble($0).readLog()")
        String objectParamToObjectThenDouble(Object value);

        @Method.Expr("acceptDouble($0).acceptObject($0).readLog()")
        String objectParamToDoubleThenObject(Object value);

        @Method.Expr("integerObjectValue()")
        Integer readObjectIntegerAsWrapper();

        @Method.Expr("integerValue()")
        Object readIntegerAsObject();

        @Method.Expr("booleanObjectValue()")
        Boolean readObjectBooleanAsWrapper();

        @Method.Expr("booleanValue()")
        Object readBooleanAsObject();

        @Method.Expr("doubleObjectValue()")
        Double readObjectDoubleAsWrapper();

        @Method.Expr("doubleValue()")
        Object readDoubleAsObject();
    }

    public static class ObjectWrapperTarget {
        private String log;

        public ObjectWrapperTarget acceptObject(Object value) {
            append("Object=" + describe(value));
            return this;
        }

        public ObjectWrapperTarget acceptInteger(Integer value) {
            append("Integer=" + value);
            return this;
        }

        public ObjectWrapperTarget acceptBoolean(Boolean value) {
            append("Boolean=" + value);
            return this;
        }

        public ObjectWrapperTarget acceptDouble(Double value) {
            append("Double=" + value);
            return this;
        }

        public Object integerObjectValue() {
            return 123;
        }

        public Integer integerValue() {
            return 456;
        }

        public Object booleanObjectValue() {
            return Boolean.TRUE;
        }

        public Boolean booleanValue() {
            return Boolean.FALSE;
        }

        public Object doubleObjectValue() {
            return 45.5d;
        }

        public Double doubleValue() {
            return 67.75d;
        }

        private void append(String item) {
            if (this.log == null) {
                this.log = item;
            } else {
                this.log += "," + item;
            }
        }

        private String describe(Object value) {
            return value.getClass().getSimpleName() + ":" + value;
        }

        public String readLog() {
            String current = this.log;
            this.log = null;
            return current;
        }
    }

    @Test
    public void shouldAutoCastArgumentsBetweenObjectAndWrapper() throws LinkerException {
        ObjectWrapperLinker linker = LinkerFactory.createLinker(ObjectWrapperLinker.class, new ObjectWrapperTarget());

        Assert.assertEquals("Object=Integer:123,Integer=123", linker.integerParamToObjectThenInteger(123));
        Assert.assertEquals("Integer=123,Object=Integer:123", linker.integerParamToIntegerThenObject(123));
        Assert.assertEquals("Object=Integer:123,Integer=123", linker.objectParamToObjectThenInteger(123));
        Assert.assertEquals("Integer=123,Object=Integer:123", linker.objectParamToIntegerThenObject(123));

        Assert.assertEquals("Object=Boolean:true,Boolean=true", linker.booleanParamToObjectThenBoolean(Boolean.TRUE));
        Assert.assertEquals("Boolean=true,Object=Boolean:true", linker.booleanParamToBooleanThenObject(Boolean.TRUE));
        Assert.assertEquals("Object=Boolean:true,Boolean=true", linker.objectParamToObjectThenBoolean(Boolean.TRUE));
        Assert.assertEquals("Boolean=true,Object=Boolean:true", linker.objectParamToBooleanThenObject(Boolean.TRUE));

        Assert.assertEquals("Object=Double:45.5,Double=45.5", linker.doubleParamToObjectThenDouble(45.5d));
        Assert.assertEquals("Double=45.5,Object=Double:45.5", linker.doubleParamToDoubleThenObject(45.5d));
        Assert.assertEquals("Object=Double:45.5,Double=45.5", linker.objectParamToObjectThenDouble(45.5d));
        Assert.assertEquals("Double=45.5,Object=Double:45.5", linker.objectParamToDoubleThenObject(45.5d));
    }

    @Test
    public void shouldAutoCastReturnValuesBetweenObjectAndWrapper() throws LinkerException {
        ObjectWrapperLinker linker = LinkerFactory.createLinker(ObjectWrapperLinker.class, new ObjectWrapperTarget());

        Assert.assertEquals(Integer.valueOf(123), linker.readObjectIntegerAsWrapper());
        Assert.assertEquals(Integer.valueOf(456), linker.readIntegerAsObject());

        Assert.assertEquals(Boolean.TRUE, linker.readObjectBooleanAsWrapper());
        Assert.assertEquals(Boolean.FALSE, linker.readBooleanAsObject());

        Assert.assertEquals(Double.valueOf(45.5d), linker.readObjectDoubleAsWrapper());
        Assert.assertEquals(Double.valueOf(67.75d), linker.readDoubleAsObject());
    }
}
