<div align="center">
  <h1>reflect-linker</h1>
  <p>使用接口 + 注解/表达式在运行时链接对象字段和方法，并通过生成字节码调用 <code>MethodHandle</code>。</p>
</div>

---

## 简介

`reflect-linker` 是一个面向“对象访问”的轻量库。

你只需要定义一个接口，并在接口方法上声明字段表达式或方法表达式，框架就会在运行时生成对应的实现类，把原本需要手写反射的逻辑转成可直接调用的
linker。

它适合用在下面这些场景：

- 访问私有字段、私有方法、静态字段、静态方法
- 使用反射连续访问对象的方法和字段
- 某些javaagent的场景中使用

## 特性

- 支持字段读取、字段写入、方法调用、构造器调用
- 支持链式的字段表达式和方法表达式
- 嵌套表达式, 空安全, 索引访问
- `Autolink`无缝使用链接对象
- `@ImportStatic` 把工具类的静态方法/静态字段导入查找过程

## 安装

要求：JDK 8+

```xml

<dependency>
    <groupId>io.github.hhy50</groupId>
    <artifactId>reflect-linker</artifactId>
    <version>2.0.0</version>
</dependency>
```

## 核心 API

- `LinkerFactory.createLinker(Class<T> define, Object target)`：链接到目标对象
- `LinkerFactory.createStaticLinker(Class<T> define, Class<?> targetClass)`：创建静态链接器, 可以操作静态字段和静态方法

用于调试的:

- `LinkerFactory.setOutputPath(String path)`：把生成的字节码输出到指定目录

## 快速开始

下面是一个最简单的例子：通过接口访问对象字段和方法。

```java
import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.exceptions.LinkerException;

interface UserLinker {
    @Field.Setter("name")
    void setName(String name);

    @Field.Getter("name")
    String getName();

    @Field.Getter("address")
    String getAddress();

    // 没有写注解时，默认按“同名方法调用”处理
    String hello(String prefix);
}

class User {
    private String name;
    private String address = "Hangzhou";

    private String hello(String prefix) {
        return prefix + ", " + name;
    }
}

public class Example {
    public static void main(String[] args) throws LinkerException {
        UserLinker linker = LinkerFactory.createLinker(UserLinker.class, new User());
        linker.setName("reflect-linker");

        System.out.println(linker.getName());
        System.out.println(linker.getAddress());
        System.out.println(linker.hello("Hi"));
    }
}
```

## 核心注解

| 注解                                                  | 作用                     |
|-----------------------------------------------------|------------------------|
| `@Field.Getter("expr")`                             | 读取字段，支持链式、空安全、索引       |
| `@Field.Setter("expr")`                             | 设置字段，支持链式定位最后一个字段      |
| `@Field.StaticGetter("expr")`                       | 读取静态字段                 |
| `@Field.StaticSetter("expr")`                       | 设置静态字段                 |
| `@Method.Expr("expr")`                              | 显式声明方法表达式              |
| `@Method.Constructor`                               | 把接口方法映射到目标类构造器         |
| `@Method.InvokeSuper("xxx")`                        | 调用指定父类实现               |
| `@Typed(name = "token", value = "full.class.Name")` | 为表达式中的 token 指定更精确的类型  |
| `@Runtime`                                          | 把接口标记为运行时解析模式          |
| `@Runtime.Static`                                   | 指定某些运行时字段/方法按静态成员处理    |
| `@Autolink`                                         | 自动把参数或返回值包装/解包为 linker |
| `@ImportStatic`                                     | 把指定类的静态方法/静态字段加入查找过程（this 优先） |
| ~~`@Target.Bind("full.class.Name")`~~               | ~~显式绑定目标类~~            |

### `expr` 示例

| 表达式 | 说明 |
|-------|------|
| `name` | 访问目标对象的 `name` 字段 |
| `user.address.city` | 按顺序访问多级嵌套字段 |
| `users[0].name` | 访问数组或 `List` 的第一个元素，再读取其字段 |
| `usersById['admin'].name` | 使用 key 访问 `Map` 中的值，再读取其字段 |
| `user?.address.city` | `user` 为 `null` 时直接返回默认值 |
| `getUser().getName()` | 链式调用多个方法 |
| `findUser($0)` | 将接口方法的第一个参数传给目标方法 |
| `merge($0, $1)` | 按下标传递接口方法的多个参数 |
| `merge(..)` | 按声明顺序转发接口方法的全部参数 |
| `format(user.name, 'VIP', 1)` | 使用嵌套表达式、字符串和整数常量作为参数 |

详细规则请参阅[表达式用法](USAGE-ZH.md)。

## 内置函数

| 内置函数 | 说明 | 示例 |
|---------|------|------|
| `class()` | 根据传入的类全限定名获取对应的 `Class` 对象 | `class('java.lang.String')`，等价于 `Class.forName("java.lang.String")` |

## 其它常用能力

### 构造器映射

```java
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Target;

@Target.Bind("java.util.ArrayList")
interface ArrayListLinker {
    @Method.Constructor
    ArrayListLinker newList();

    @Method.Expr("add(..)")
    void add(Object value);
}
```

### 调用指定父类实现

```java
interface FatherVisitor {
    @Method.InvokeSuper("your.pkg.Father1")
    @Method.Expr("aaa()")
    String father1Aaa();
}
```

### `@ImportStatic` 导入静态成员

`@ImportStatic` 可以把工具类的静态方法/静态字段加入查找过程。

```java
import io.github.hhy50.linker.annotations.ImportStatic;

class MyStringUtil {
    public static String trim(String s) {
        return s.trim();
    }
}

@ImportStatic(MyStringUtil.class)
interface MyLinker {
    // 同名调用会被解析到导入的静态方法 MyStringUtil.trim
    String trim(String s);
}
```

`@ImportStatic` 可以标注在接口上（对所有方法生效），也可以标注在单个方法上（覆盖类级导入）。详细规则见[表达式用法](USAGE-ZH.md)。

## 调试生成字节码

如果你想查看框架生成的 linker 实现类，可以先设置输出目录：

```java
LinkerFactory.setOutputPath("generated-linkers");
```

之后再执行 `createLinker(...)` / `createStaticLinker(...)`，生成的 `.class` 文件会输出到指定目录。
