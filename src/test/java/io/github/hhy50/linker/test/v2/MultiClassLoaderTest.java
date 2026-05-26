package io.github.hhy50.linker.test.v2;

import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Runtime;
import io.github.hhy50.linker.asm.AsmClassBuilder;
import io.github.hhy50.linker.generate.bytecode.action.*;
import io.github.hhy50.linker.generate.bytecode.utils.Args;
import io.github.hhy50.linker.generate.bytecode.utils.Members;
import io.github.hhy50.linker.generate.bytecode.utils.Methods;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * 测试多类加载器场景：
 * - A接口在当前类加载器中加载
 * - B类通过字节码动态生成并在自定义类加载器中加载
 * - 使用Linker创建A接口实例并链接到B类实例（通过Object引用）
 * - 验证通过反射访问动态生成类的方法和字段是否正常工作
 * <p>
 * 注意：使用reflect-linker的AsmClassBuilder和Action API来生成字节码，
 * 并通过Object引用来模拟跨类加载器场景，测试linker的反射访问能力。
 *
 * 必须指定运行时
 */
public class MultiClassLoaderTest {

    /**
     * 接口A - 在当前类加载器中加载
     */
    @Runtime
    public interface TargetLinker {

        @Method.Expr("getName()")
        String getName();

        @Method.Expr("setName($0)")
        void setName(String name);

        @Method.Expr("getAge()")
        int getAge();

        @Method.Expr("setAge($0)")
        void setAge(int age);

        @Field.Getter("name")
        String getNameField();

        @Field.Getter("age")
        int getAgeField();

        @Method.Expr("computeInfo($0, $1)")
        String computeInfo(String prefix, int multiplier);
    }

    /**
     * 自定义类加载器 - 用于加载动态生成的类
     */
    static class DynamicClassLoader extends ClassLoader {

        public DynamicClassLoader() {
            super(DynamicClassLoader.class.getClassLoader());
        }

        public Class<?> defineClass(String name, byte[] bytecode) {
            return defineClass(name, bytecode, 0, bytecode.length);
        }
    }

    /**
     * 使用reflect-linker的Action API动态生成目标类的字节码
     * 生成的类包含：
     * - private String name
     * - private int age
     * - getName() / setName(String)
     * - getAge() / setAge(int)
     * - computeInfo(String, int)
     */
    private static byte[] generateTargetClassBytecode(String className) {
        // 使用AsmClassBuilder构建类
        AsmClassBuilder builder = new AsmClassBuilder(
                Opcodes.ACC_PUBLIC,
                className,
                null,
                null,
                null
        );

        // 定义字段
        builder.defineField(Opcodes.ACC_PRIVATE, "name", String.class);
        builder.defineField(Opcodes.ACC_PRIVATE, "age", int.class);

        // 定义构造函数
        builder.defineConstruct(Opcodes.ACC_PUBLIC)
                .intercept(Methods.invokeSuper().thenReturn())
                .defineMethod(Opcodes.ACC_PUBLIC, "getName",
                        Type.getMethodType(Type.getType(String.class)), null)
                .intercept(Members.ofLoad("name").thenReturn())
                .defineMethod(Opcodes.ACC_PUBLIC, "setName", Type.getMethodType(Type.VOID_TYPE, Type.getType(String.class)), null)
                .intercept(
                        Members.ofStore("name", Args.of(0)),
                        Actions.vreturn()
                )
                .defineMethod(Opcodes.ACC_PUBLIC, "getAge",
                        Type.getMethodType(Type.INT_TYPE), null)
                .intercept(Members.ofLoad("age").thenReturn())
                .defineMethod(Opcodes.ACC_PUBLIC, "setAge",
                        Type.getMethodType(Type.VOID_TYPE, Type.INT_TYPE), null)
                .intercept(
                        Members.ofStore("age", Args.of(0)),
                        Actions.vreturn()
                );
        builder.defineMethod(Opcodes.ACC_PUBLIC, "computeInfo",
                        Type.getMethodType(Type.getType(String.class), Type.getType(String.class), Type.INT_TYPE), null)
                .intercept(new NewObjectAction(Type.getType(StringBuilder.class))
                        .invokeMethod("append", Type.getMethodType(Type.getType(StringBuilder.class), Type.getType(String.class)),
                                Args.of(0))
                        .invokeMethod("append", Type.getMethodType(Type.getType(StringBuilder.class), Type.getType(String.class)),
                                LdcLoadAction.of(": "))
                        .invokeMethod("append", Type.getMethodType(Type.getType(StringBuilder.class), Type.getType(String.class)),
                                Members.ofLoad("name"))
                        .invokeMethod("append", Type.getMethodType(Type.getType(StringBuilder.class), Type.getType(String.class)),
                                LdcLoadAction.of(", "))
                        .invokeMethod("append", Type.getMethodType(Type.getType(StringBuilder.class), Type.getType(int.class)),
                                Actions.of(Members.ofLoad("age"), Args.of(1), Actions.withVisitor(mv -> mv.visitInsn(Opcodes.IMUL))))
                        .invokeMethod("toString", Type.getMethodType(Type.getType(String.class))).thenReturn()
                );
        return builder.toBytecode();
    }

    @Test
    public void shouldAccessDynamicallyGeneratedClassViaReflection() throws Exception {
        // 1. 创建自定义类加载器
        DynamicClassLoader classLoader = new DynamicClassLoader();


        // 2. 动态生成目标类的字节码
        String targetClassName = "io.github.hhy50.linker.test.generated.DynamicTarget";
        byte[] bytecode = generateTargetClassBytecode(targetClassName);

        // 3. 在自定义类加载器中加载类
        Class<?> targetClass = classLoader.defineClass(targetClassName, bytecode);

        // 4. 通过反射创建实例（只能用Object表示，模拟跨类加载器场景）
        Object targetInstance = targetClass.getDeclaredConstructor().newInstance();

        // 5. 验证类加载器
        System.out.println("Interface ClassLoader: " + TargetLinker.class.getClassLoader());
        System.out.println("Target ClassLoader: " + targetClass.getClassLoader());
        System.out.println("Target instance class: " + targetInstance.getClass().getName());

        // 6. 使用LinkerFactory创建接口实现，链接到动态生成的类实例
        // 关键点：接口和目标实例的类在不同的类加载器中
        TargetLinker linker = LinkerFactory.createLinker(TargetLinker.class, targetInstance);

        // 7. 测试方法调用 - setName/getName
        linker.setName("Alice");
        Assert.assertEquals("Alice", linker.getName());

        // 8. 测试方法调用 - setAge/getAge
        linker.setAge(25);
        Assert.assertEquals(25, linker.getAge());

        // 9. 测试字段访问
        Assert.assertEquals("Alice", linker.getNameField());
        Assert.assertEquals(25, linker.getAgeField());

        // 10. 测试带参数的方法调用
        String info = linker.computeInfo("User", 2);
        Assert.assertEquals("User: Alice, 50", info);

        // 11. 修改数据后再次验证
        linker.setName("Bob");
        linker.setAge(30);
        Assert.assertEquals("Bob", linker.getNameField());
        Assert.assertEquals(30, linker.getAgeField());
        Assert.assertEquals("Info: Bob, 90", linker.computeInfo("Info", 3));

        System.out.println("✓ Successfully accessed dynamically generated class via reflection!");
    }

    @Test
    public void shouldHandleNullValuesInDynamicClass() throws Exception {
        // 创建自定义类加载器和目标实例
        DynamicClassLoader classLoader = new DynamicClassLoader();
        String targetClassName = "io.github.hhy50.linker.test.generated.DynamicTargetNull";
        byte[] bytecode = generateTargetClassBytecode(targetClassName);
        Class<?> targetClass = classLoader.defineClass(targetClassName, bytecode);
        Object targetInstance = targetClass.getDeclaredConstructor().newInstance();

        // 创建linker
        TargetLinker linker = LinkerFactory.createLinker(TargetLinker.class, targetInstance);

        // 测试null值处理
        Assert.assertNull("Initial name should be null", linker.getName());
        Assert.assertEquals("Initial age should be 0", 0, linker.getAge());

        linker.setName("Test");
        Assert.assertEquals("Test", linker.getName());

        linker.setName(null);
        Assert.assertNull("Name should be null after setting to null", linker.getName());

        System.out.println("✓ Null value handling in dynamic class successful!");
    }

    @Test
    public void shouldWorkWithMultipleDynamicInstances() throws Exception {
        // 创建自定义类加载器
        DynamicClassLoader classLoader = new DynamicClassLoader();

        // 动态生成两个不同的类
        String targetClassName1 = "io.github.hhy50.linker.test.generated.DynamicTarget1";
        String targetClassName2 = "io.github.hhy50.linker.test.generated.DynamicTarget2";
        byte[] bytecode1 = generateTargetClassBytecode(targetClassName1);
        byte[] bytecode2 = generateTargetClassBytecode(targetClassName2);

        Class<?> targetClass1 = classLoader.defineClass(targetClassName1, bytecode1);
        Class<?> targetClass2 = classLoader.defineClass(targetClassName2, bytecode2);

        // 验证类不相等
        Assert.assertNotEquals("Classes should have different names",
                targetClass1.getName(), targetClass2.getName());

        // 创建两个实例
        Object instance1 = targetClass1.getDeclaredConstructor().newInstance();
        Object instance2 = targetClass2.getDeclaredConstructor().newInstance();

        // 创建两个linker
        TargetLinker linker1 = LinkerFactory.createLinker(TargetLinker.class, instance1);
        TargetLinker linker2 = LinkerFactory.createLinker(TargetLinker.class, instance2);

        // 分别设置不同的值
        linker1.setName("Instance1");
        linker1.setAge(10);

        linker2.setName("Instance2");
        linker2.setAge(20);

        // 验证数据隔离
        Assert.assertEquals("Instance1", linker1.getName());
        Assert.assertEquals(10, linker1.getAge());

        Assert.assertEquals("Instance2", linker2.getName());
        Assert.assertEquals(20, linker2.getAge());

        // 验证计算结果
        Assert.assertEquals("Result: Instance1, 30", linker1.computeInfo("Result", 3));
        Assert.assertEquals("Result: Instance2, 40", linker2.computeInfo("Result", 2));

        System.out.println("✓ Multiple dynamic instances successful!");
    }
}
