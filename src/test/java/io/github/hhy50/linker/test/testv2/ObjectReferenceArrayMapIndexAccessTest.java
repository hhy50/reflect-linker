package io.github.hhy50.linker.test.testv2;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ObjectReferenceArrayMapIndexAccessTest {

    public interface ObjectReferenceArrayMapIndexLinker {

        @Field.Getter("userArray[1]")
        UserValue readFieldArrayUserAsUser();

        @Field.Getter("userArray[0]")
        Object readFieldArrayUserAsObject();

        @Field.Getter("userGrid[1][0]")
        UserValue readFieldGridUserAsUser();

        @Field.Getter("userGrid[0][1]")
        Object readFieldGridUserAsObject();

        @Field.Getter("userMap['captain']")
        UserValue readFieldMapUserAsUser();

        @Field.Getter("userMap['pilot']")
        Object readFieldMapUserAsObject();

        @Field.Getter("mixed['teams'][1]['lead']")
        UserValue readFieldMixedLeadAsUser();

        @Field.Getter("mixed['teams'][0]['lead']")
        Object readFieldMixedLeadAsObject();

        @Method.Expr("userArrayObject()[1]")
        UserValue readMethodArrayUserFromObjectArray();

        @Method.Expr("userArrayTyped()[0]")
        Object readMethodArrayUserAsObjectFromTypedArray();

        @Method.Expr("userGridObject()[1][0]")
        UserValue readMethodGridUserFromObjectGrid();

        @Method.Expr("userGridTyped()[0][1]")
        Object readMethodGridUserAsObjectFromTypedGrid();

        @Method.Expr("userMapObject()['captain']")
        UserValue readMethodMapUserFromObjectMap();

        @Method.Expr("userMapTyped()['pilot']")
        Object readMethodMapUserAsObjectFromTypedMap();

        @Method.Expr("mixedObject()['teams'][1]['lead']")
        UserValue readMethodMixedLeadAsUser();

        @Method.Expr("mixedTyped()['teams'][0]['lead']")
        Object readMethodMixedLeadAsObject();
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

    public static class ObjectReferenceIndexTarget {
        private final UserValue[] userArray;
        private final UserValue[][] userGrid;
        private final Map<String, UserValue> userMap;
        private final Map<String, Object> mixed;

        public ObjectReferenceIndexTarget(UserValue[] userArray,
                                          UserValue[][] userGrid,
                                          Map<String, UserValue> userMap,
                                          Map<String, Object> mixed) {
            this.userArray = userArray;
            this.userGrid = userGrid;
            this.userMap = userMap;
            this.mixed = mixed;
        }

        public Object userArrayObject() {
            return userArray;
        }

        public UserValue[] userArrayTyped() {
            return userArray;
        }

        public Object userGridObject() {
            return userGrid;
        }

        public UserValue[][] userGridTyped() {
            return userGrid;
        }

        public Object userMapObject() {
            return userMap;
        }

        public Map<String, UserValue> userMapTyped() {
            return userMap;
        }

        public Object mixedObject() {
            return mixed;
        }

        public Map<String, Object> mixedTyped() {
            return mixed;
        }
    }

    @Test
    public void shouldAccessArrayIndexesAndAutoCastBetweenObjectAndReferenceType() throws LinkerException {
        ObjectReferenceArrayMapIndexLinker linker = LinkerFactory.createLinker(
                ObjectReferenceArrayMapIndexLinker.class,
                createTarget()
        );

        Assert.assertEquals("bravo", linker.readFieldArrayUserAsUser().getName());

        Object fieldArrayUser = linker.readFieldArrayUserAsObject();
        Assert.assertTrue(fieldArrayUser instanceof UserValue);
        Assert.assertEquals("alpha", ((UserValue) fieldArrayUser).getName());

        Assert.assertEquals("echo", linker.readFieldGridUserAsUser().getName());

        Object fieldGridUser = linker.readFieldGridUserAsObject();
        Assert.assertTrue(fieldGridUser instanceof UserValue);
        Assert.assertEquals("delta", ((UserValue) fieldGridUser).getName());

        Assert.assertEquals("bravo", linker.readMethodArrayUserFromObjectArray().getName());

        Object methodArrayUser = linker.readMethodArrayUserAsObjectFromTypedArray();
        Assert.assertTrue(methodArrayUser instanceof UserValue);
        Assert.assertEquals("alpha", ((UserValue) methodArrayUser).getName());

        Assert.assertEquals("echo", linker.readMethodGridUserFromObjectGrid().getName());

        Object methodGridUser = linker.readMethodGridUserAsObjectFromTypedGrid();
        Assert.assertTrue(methodGridUser instanceof UserValue);
        Assert.assertEquals("delta", ((UserValue) methodGridUser).getName());
    }

    @Test
    public void shouldAccessMapIndexesAndMixedIndexChains() throws LinkerException {
        ObjectReferenceArrayMapIndexLinker linker = LinkerFactory.createLinker(
                ObjectReferenceArrayMapIndexLinker.class,
                createTarget()
        );

        Assert.assertEquals("atlas", linker.readFieldMapUserAsUser().getName());

        Object fieldMapUser = linker.readFieldMapUserAsObject();
        Assert.assertTrue(fieldMapUser instanceof UserValue);
        Assert.assertEquals("nova", ((UserValue) fieldMapUser).getName());

        Assert.assertEquals("lyra", linker.readFieldMixedLeadAsUser().getName());

        Object fieldMixedLead = linker.readFieldMixedLeadAsObject();
        Assert.assertTrue(fieldMixedLead instanceof UserValue);
        Assert.assertEquals("orion", ((UserValue) fieldMixedLead).getName());

        Assert.assertEquals("atlas", linker.readMethodMapUserFromObjectMap().getName());

        Object methodMapUser = linker.readMethodMapUserAsObjectFromTypedMap();
        Assert.assertTrue(methodMapUser instanceof UserValue);
        Assert.assertEquals("nova", ((UserValue) methodMapUser).getName());

        Assert.assertEquals("lyra", linker.readMethodMixedLeadAsUser().getName());

        Object methodMixedLead = linker.readMethodMixedLeadAsObject();
        Assert.assertTrue(methodMixedLead instanceof UserValue);
        Assert.assertEquals("orion", ((UserValue) methodMixedLead).getName());
    }

    private ObjectReferenceIndexTarget createTarget() {
        UserValue alpha = new UserValue("alpha");
        UserValue bravo = new UserValue("bravo");
        UserValue delta = new UserValue("delta");
        UserValue echo = new UserValue("echo");
        UserValue atlas = new UserValue("atlas");
        UserValue nova = new UserValue("nova");
        UserValue orion = new UserValue("orion");
        UserValue lyra = new UserValue("lyra");

        UserValue[] userArray = new UserValue[]{alpha, bravo};
        UserValue[][] userGrid = new UserValue[][]{
                new UserValue[]{new UserValue("charlie"), delta},
                new UserValue[]{echo, new UserValue("foxtrot")}
        };

        Map<String, UserValue> userMap = new HashMap<String, UserValue>();
        userMap.put("captain", atlas);
        userMap.put("pilot", nova);

        Map<String, Object> firstTeam = new HashMap<String, Object>();
        firstTeam.put("lead", orion);
        Map<String, Object> secondTeam = new HashMap<String, Object>();
        secondTeam.put("lead", lyra);

        Map<String, Object> mixed = new HashMap<String, Object>();
        mixed.put("teams", new Object[]{firstTeam, secondTeam});

        return new ObjectReferenceIndexTarget(userArray, userGrid, userMap, mixed);
    }
}
