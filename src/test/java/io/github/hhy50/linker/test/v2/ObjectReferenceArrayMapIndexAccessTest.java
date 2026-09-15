package io.github.hhy50.linker.test.v2;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.define.ParseContext;
import io.github.hhy50.linker.define.md.AbsInterfaceMetadata;
import io.github.hhy50.linker.define.md.AbsMethodMetadata;
import io.github.hhy50.linker.define.method.EarlyMethodRef;
import io.github.hhy50.linker.define.method.MethodExprRef;
import io.github.hhy50.linker.define.method.MethodExprStep;
import io.github.hhy50.linker.exceptions.LinkerException;
import io.github.hhy50.linker.generate.invoker.Getter;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public interface FieldIndexMethodLinker {

        @Method.Expr("userGrid[1][0].getName()")
        String secondRowFirstUserName();
    }

    public interface ListIndexAccessLinker {

        @Field.Getter("userList[1]")
        UserValue readFieldListUserAsUser();

        @Field.Getter("userList[0]")
        Object readFieldListUserAsObject();

        @Field.Getter("userLists[1][0]")
        UserValue readFieldNestedListUserAsUser();

        @Method.Expr("userList[0].getName()")
        String firstListUserName();
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
        private final List<UserValue> userList;
        private final List<List<UserValue>> userLists;

        public ObjectReferenceIndexTarget(UserValue[] userArray,
                                          UserValue[][] userGrid,
                                          Map<String, UserValue> userMap,
                                          Map<String, Object> mixed,
                                          List<UserValue> userList,
                                          List<List<UserValue>> userLists) {
            this.userArray = userArray;
            this.userGrid = userGrid;
            this.userMap = userMap;
            this.mixed = mixed;
            this.userList = userList;
            this.userLists = userLists;
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

    @Test
    public void shouldResolveListElementTypeFromSignatureOnIndexAccess() throws LinkerException {
        ListIndexAccessLinker linker = LinkerFactory.createLinker(
                ListIndexAccessLinker.class,
                createTarget()
        );

        Assert.assertEquals("bravo", linker.readFieldListUserAsUser().getName());

        Object fieldListUser = linker.readFieldListUserAsObject();
        Assert.assertTrue(fieldListUser instanceof UserValue);
        Assert.assertEquals("alpha", ((UserValue) fieldListUser).getName());

        Assert.assertEquals("echo", linker.readFieldNestedListUserAsUser().getName());

        Assert.assertEquals("alpha", linker.firstListUserName());
    }

    @Test
    public void shouldKeepComponentTypeWhenMethodFollowsFieldIndex() throws Exception {
        AbsInterfaceMetadata interfaceMetadata = new AbsInterfaceMetadata(
                FieldIndexMethodLinker.class,
                ObjectReferenceIndexTarget.class
        );
        ParseContextArrayIndexTypeRegressionTest.ParseContextLinker parseContext = LinkerFactory
                .createStaticLinker(ParseContextArrayIndexTypeRegressionTest.ParseContextLinker.class, ParseContext.class)
                .newInstance(interfaceMetadata, ObjectReferenceIndexTarget.class);
        parseContext.setClassLoader(ObjectReferenceIndexTarget.class.getClassLoader());

        java.lang.reflect.Method reflectMethod = FieldIndexMethodLinker.class.getMethod("secondRowFirstUserName");
        MethodExprRef exprRef = parseContext.parseMethod(parseContext.preParse(interfaceMetadata, reflectMethod));
        List<MethodExprStep> steps = exprRef.getStepMethods();

        Assert.assertEquals(2, steps.size());
        Assert.assertTrue(steps.get(0).getMethodRef().defineInvoker() instanceof Getter.WithEarly);
        Assert.assertTrue(
                "expected getName() to stay early-bound after userGrid[1][0]",
                steps.get(1).getMethodRef() instanceof EarlyMethodRef
        );

        FieldIndexMethodLinker linker = LinkerFactory.createLinker(FieldIndexMethodLinker.class, createTarget());
        Assert.assertEquals("echo", linker.secondRowFirstUserName());
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

        List<UserValue> userList = new ArrayList<UserValue>();
        userList.add(alpha);
        userList.add(bravo);

        List<List<UserValue>> userLists = new ArrayList<List<UserValue>>();
        userLists.add(new ArrayList<UserValue>());
        userLists.add(new ArrayList<UserValue>());
        userLists.get(1).add(echo);

        return new ObjectReferenceIndexTarget(userArray, userGrid, userMap, mixed, userList, userLists);
    }

    public static class ParseContextArrayIndexTypeRegressionTest {

        public interface SingleIndexMethodArrayLinker {

            @Method.Expr("users()[0].getName()")
            String firstUserName();
        }

        public interface MultiIndexMethodArrayLinker {

            @Method.Expr("userGrid()[1][0].getName()")
            String secondRowFirstUserName();
        }

        public interface MultiIndexFieldArrayLinker {

            @Field.Getter("userGrid[1][0].name")
            String secondRowFirstUserNameByField();
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

        public static class ArrayIndexTarget {

            private final UserValue[] users = new UserValue[]{new ParseContextArrayIndexTypeRegressionTest.UserValue("alpha")};

            private final UserValue[][] userGrid = new UserValue[][]{{new ParseContextArrayIndexTypeRegressionTest.UserValue("bravo")}, {new ParseContextArrayIndexTypeRegressionTest.UserValue("charlie")}};

            public UserValue[] users() {
                return users;
            }

            public UserValue[][] userGrid() {
                return userGrid;
            }
        }

        @Test
        public void shouldKeepComponentTypeAfterSingleDimMethodIndex() throws Exception {
            MethodExprRef exprRef = parseMethodExpr(
                    SingleIndexMethodArrayLinker.class,
                    "firstUserName",
                    ArrayIndexTarget.class
            );

            List<MethodExprStep> steps = exprRef.getStepMethods();
            Assert.assertEquals(2, steps.size());
            Assert.assertTrue(steps.get(0).getMethodRef() instanceof EarlyMethodRef);
            Assert.assertTrue(
                    "expected getName() to stay early-bound after users()[0]",
                    steps.get(1).getMethodRef() instanceof EarlyMethodRef
            );
        }

        @Test
        public void shouldKeepComponentTypeAfterMultiDimMethodIndex() throws Exception {
            MethodExprRef exprRef = parseMethodExpr(
                    MultiIndexMethodArrayLinker.class,
                    "secondRowFirstUserName",
                    ArrayIndexTarget.class
            );

            List<MethodExprStep> steps = exprRef.getStepMethods();
            Assert.assertEquals(2, steps.size());
            Assert.assertTrue(steps.get(0).getMethodRef() instanceof EarlyMethodRef);
            Assert.assertTrue(
                    "expected getName() to stay early-bound after userGrid()[1][0]",
                    steps.get(1).getMethodRef() instanceof EarlyMethodRef
            );
        }

        @Test
        public void shouldKeepComponentTypeAfterMultiDimFieldIndex() throws Exception {
            MethodExprRef exprRef = parseMethodExpr(
                    MultiIndexFieldArrayLinker.class,
                    "secondRowFirstUserNameByField",
                    ArrayIndexTarget.class
            );

            List<MethodExprStep> steps = exprRef.getStepMethods();
            Assert.assertEquals(2, steps.size());
            Assert.assertTrue(steps.get(0).getMethodRef().defineInvoker() instanceof Getter.WithEarly);
            Assert.assertTrue(
                    "expected name field to stay early-bound after userGrid[1][0]",
                    steps.get(1).getMethodRef().defineInvoker() instanceof Getter.WithEarly
            );
        }

        private MethodExprRef parseMethodExpr(Class<?> defineClass, String methodName, Class<?> targetClass) throws Exception {
            AbsInterfaceMetadata interfaceMetadata = new AbsInterfaceMetadata(defineClass, targetClass);

            ParseContextLinker parseContext = LinkerFactory.createStaticLinker(ParseContextLinker.class, ParseContext.class)
                    .newInstance(interfaceMetadata, targetClass);
            parseContext.setClassLoader(targetClass.getClassLoader());
            java.lang.reflect.Method reflectMethod = defineClass.getMethod(methodName);
            return parseContext.parseMethod(parseContext.preParse(interfaceMetadata, reflectMethod));
        }

        interface ParseContextLinker {
            @Method.Constructor
            ParseContextLinker newInstance(AbsInterfaceMetadata metadata, Class<?> targetClass);

            void setClassLoader(ClassLoader classLoader);

            AbsMethodMetadata preParse(AbsInterfaceMetadata classMetadata, java.lang.reflect.Method method);

            MethodExprRef parseMethod(AbsMethodMetadata metadata);
        }
    }
}
