package io.github.hhy50.linker.test.testv2;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

public class ObjectReferenceAutoCastTest {

    public interface ObjectReferenceLinker {

        @Method.Expr("acceptObject($0).acceptString($0).readLog()")
        String stringParamToObjectThenString(String value);

        @Method.Expr("acceptString($0).acceptObject($0).readLog()")
        String stringParamToStringThenObject(String value);

        @Method.Expr("acceptObject($0).acceptString($0).readLog()")
        String objectParamToObjectThenString(Object value);

        @Method.Expr("acceptString($0).acceptObject($0).readLog()")
        String objectParamToStringThenObject(Object value);

        @Method.Expr("acceptObject($0).acceptUser($0).readLog()")
        String userParamToObjectThenUser(UserValue value);

        @Method.Expr("acceptUser($0).acceptObject($0).readLog()")
        String userParamToUserThenObject(UserValue value);

        @Method.Expr("acceptObject($0).acceptUser($0).readLog()")
        String objectParamToObjectThenUser(Object value);

        @Method.Expr("acceptUser($0).acceptObject($0).readLog()")
        String objectParamToUserThenObject(Object value);

        @Method.Expr("stringObjectValue()")
        String readObjectStringAsString();

        @Method.Expr("stringValue()")
        Object readStringAsObject();

        @Method.Expr("userObjectValue()")
        UserValue readObjectUserAsUser();

        @Method.Expr("userValue()")
        Object readUserAsObject();
    }

    public static class UserValue {
        private final String name;

        public UserValue(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class ObjectReferenceTarget {
        private String log;

        public ObjectReferenceTarget acceptObject(Object value) {
            append("Object=" + describe(value));
            return this;
        }

        public ObjectReferenceTarget acceptString(String value) {
            append("String=" + value);
            return this;
        }

        public ObjectReferenceTarget acceptUser(UserValue value) {
            append("User=" + value.getName());
            return this;
        }

        public Object stringObjectValue() {
            return "alpha";
        }

        public String stringValue() {
            return "beta";
        }

        public Object userObjectValue() {
            return new UserValue("tom");
        }

        public UserValue userValue() {
            return new UserValue("jerry");
        }

        private void append(String item) {
            if (this.log == null) {
                this.log = item;
            } else {
                this.log += "," + item;
            }
        }

        private String describe(Object value) {
            if (value instanceof UserValue) {
                return "UserValue:" + ((UserValue) value).getName();
            }
            return value.getClass().getSimpleName() + ":" + value;
        }

        public String readLog() {
            String current = this.log;
            this.log = null;
            return current;
        }
    }

    @Test
    public void shouldAutoCastArgumentsBetweenObjectAndReferenceType() throws LinkerException {
        ObjectReferenceLinker linker = LinkerFactory.createLinker(ObjectReferenceLinker.class, new ObjectReferenceTarget());
        UserValue user = new UserValue("neo");

        Assert.assertEquals("Object=String:alpha,String=alpha", linker.stringParamToObjectThenString("alpha"));
        Assert.assertEquals("String=alpha,Object=String:alpha", linker.stringParamToStringThenObject("alpha"));
        Assert.assertEquals("Object=String:alpha,String=alpha", linker.objectParamToObjectThenString("alpha"));
        Assert.assertEquals("String=alpha,Object=String:alpha", linker.objectParamToStringThenObject("alpha"));

        Assert.assertEquals("Object=UserValue:neo,User=neo", linker.userParamToObjectThenUser(user));
        Assert.assertEquals("User=neo,Object=UserValue:neo", linker.userParamToUserThenObject(user));
        Assert.assertEquals("Object=UserValue:neo,User=neo", linker.objectParamToObjectThenUser(user));
        Assert.assertEquals("User=neo,Object=UserValue:neo", linker.objectParamToUserThenObject(user));
    }

    @Test
    public void shouldAutoCastReturnValuesBetweenObjectAndReferenceType() throws LinkerException {
        ObjectReferenceLinker linker = LinkerFactory.createLinker(ObjectReferenceLinker.class, new ObjectReferenceTarget());

        Assert.assertEquals("alpha", linker.readObjectStringAsString());
        Assert.assertEquals("beta", linker.readStringAsObject());

        Assert.assertEquals("tom", linker.readObjectUserAsUser().getName());

        Object userAsObject = linker.readUserAsObject();
        Assert.assertTrue(userAsObject instanceof UserValue);
        Assert.assertEquals("jerry", ((UserValue) userAsObject).getName());
    }
}
