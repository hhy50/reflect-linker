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
| ~~`@Target.Bind("full.class.Name")`~~               | ~~显式绑定目标类~~            |

## 表达式用法

### 1. 默认同名方法调用

如果接口方法没有写注解，框架会默认把它当成同名方法调用。

```java
interface UserLinker {
    String hello(String prefix);
}

class User {
    private String hello(String prefix) {
        return prefix + " user";
    }
}
```

上面的 `hello(String prefix)` 等价于：

```java
interface UserLinker {
    @Method.Expr("hello($0)")
    String hello(String prefix);
}
```

### 2. 链式访问

字段表达式和方法表达式都支持链式访问。

```java
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;

interface OrderLinker {
    @Field.Getter("user.address.city")
    String readCityByField();

    @Method.Expr("getUser().getAddress().getCity()")
    String readCityByMethod();
}

class Order {
    private User user;

    private User getUser() {
        return user;
    }
}

class User {
    private Address address;

    private Address getAddress() {
        return address;
    }
}

class Address {
    private String city;

    private String getCity() {
        return city;
    }
}
```

### 3. 参数占位、常量和全量转发

#### 参数占位

```java
interface UserLinker {
    @Method.Expr("rename($0)")
    void rename(String newName);

    //    @Method.Expr("merge($0, $1)") // 等价
    @Method.Expr("merge(..)")
    String merge(String left, String right);
}
```

### 4. 嵌套表达式

表达式的参数本身也可以继续是表达式，这一点对复杂链路非常有用。

```java
import io.github.hhy50.linker.annotations.Method;

interface TargetLinker {
    @Method.Expr("pipeline().join(user.profile.name, user.profile.makeEnvelope($0).seal().finish(), 3)")
    String render(String suffix);
}

class Target {
    private User user;

    private Pipeline pipeline() {
        return new Pipeline();
    }
}

class User {
    private Profile profile;
}

class Profile {
    private String name;

    private Envelope makeEnvelope(String suffix) {
        return new Envelope(name + "-" + suffix);
    }
}

class Envelope {
    private final String value;

    Envelope(String value) {
        this.value = value;
    }

    private Envelope seal() {
        return this;
    }

    private String finish() {
        return "[" + value + "]";
    }
}

class Pipeline {
    private String join(String name, String label, int level) {
        return name + "|" + label + "|" + level;
    }
}
```

### 5. 空安全

空安全通过 `?` 开启，写在某一步后面表示：如果这一步的结果是 `null`，整个表达式直接返回默认值。

```java
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;

interface NullableLinker {
    @Field.Getter("a.b.c.d?.e.f?.g")
    String readFieldChainString();

    @Field.Getter("a.b.c.d?.e.f?.count")
    int readFieldChainCount();

    @Method.Expr("a.getB().getC().getD()?.getE().getF()?.getG()")
    String readMethodChainString();

    @Method.Expr("a.getB().getC().getD()?.getE().getF()?.getCount()")
    int readMethodChainCount();
}
```

行为说明：

- 引用类型短路后返回 `null`
- 基本类型短路后返回 JVM 默认值：`0` / `0L` / `0.0` / `false` / `\0`
- `?` 只保护当前步骤，不会自动覆盖整条链

例如：

- `user?.address.city`：只保护 `user`
- `user.address?.city`：只保护 `address`
- `getUser()?.getAddress().getCity()`：只保护 `getUser()` 的返回值

### 6. 索引访问

索引访问同时支持数组、`List`、`Map`，也支持混合嵌套。

```java
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;

interface UserView {
    @Field.Getter("name")
    String getName();
}

interface IndexLinker {
    @Field.Getter("users[0]")
    UserView firstUser();

    @Field.Getter("grid[1][0]")
    UserView secondRowFirst();

    @Field.Getter("userMap['captain']")
    UserView captain();

    @Field.Getter("mixed['teams'][1]['lead']")
    UserView secondLead();

    @Method.Expr("loadUsers()[0]")
    UserView firstUserFromMethod();

    @Method.Expr("loadUserMap()['captain']")
    UserView captainFromMethod();
}
```

支持的写法：

- `array[0]`
- `list[1]`
- `grid[1][0]`
- `map['key']`
- `mixed['teams'][1]['lead']`
- `methodCall()[0]`
- `methodCall()['key']`

注意：

- 字符串 key 必须写成单引号：`['captain']`
- 多维索引可以连续写：`grid[1][0]`
- 目标值如果是 `Object`，会根据接口方法的返回类型自动尝试转换

### 7. `@Autolink`

`@Autolink` 可以自动处理“linker 参数”和“linker 返回值”。

也就是说：

- 你传给接口方法的是 `PilotView`
- 目标方法真正需要的是 `Pilot`
- 框架会自动把 `PilotView` 解包成底层目标对象

同时，如果目标方法返回的是 `Pilot` 或 `Badge`，而你的接口定义返回的是 `PilotView` 或 `BadgeView`，框架也会自动帮你包装成新的
linker。

```java
import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Autolink;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;

interface PilotView {
    @Field.Getter("name")
    String getName();

    @Field.Getter("rank")
    int getRank();
}

interface BadgeView {
    @Field.Getter("code")
    String getCode();

    @Autolink
    @Field.Getter("owner")
    PilotView getOwner();
}

interface MissionLinker {
    @Autolink
    @Method.Expr("promote(..)")
    PilotView promote(PilotView pilot, int delta);

    @Autolink
    @Method.Expr("issue(..)")
    BadgeView issue(PilotView pilot, String code);
}

class Pilot {
    private final String name;
    private final int rank;

    Pilot(String name, int rank) {
        this.name = name;
        this.rank = rank;
    }
}

class Badge {
    private final String code;
    private final Pilot owner;

    Badge(String code, Pilot owner) {
        this.code = code;
        this.owner = owner;
    }
}

class MissionService {
    private Pilot promote(Pilot pilot, Integer delta) {
        return new Pilot(pilot.name + "-" + delta, pilot.rank + delta);
    }

    private Badge issue(Pilot pilot, String code) {
        return new Badge(code + "-" + pilot.name, pilot);
    }
}

public class Example {
    public static void main(String[] args) throws LinkerException {
        MissionLinker linker = LinkerFactory.createLinker(MissionLinker.class, new MissionService());
        PilotView pilot = LinkerFactory.createLinker(PilotView.class, new Pilot("mira", 5));

        PilotView promoted = linker.promote(pilot, 4);
        BadgeView badge = linker.issue(pilot, "seal");

        System.out.println(promoted.getName());
        System.out.println(promoted.getRank());
        System.out.println(badge.getCode());
        System.out.println(badge.getOwner().getName());
    }
}
```

`@Autolink` 可以标在：

- 接口上：整个 linker 默认启用 autolink
- 方法上：只对当前方法启用
- 某个 getter 上：对这个 getter 的返回值启用

### 8. 动态类型与 `@Typed`

当表达式里的字段或方法存在于“运行时真实类型”里，而不在声明类型里时，框架会退到运行时解析。

```java
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.annotations.Typed;

class A {
}

class B {
    String val = "this is b'val";

    String getVal() {
        return val;
    }
}

class A2 extends A {
    B b;

    A2(B b) {
        this.b = b;
    }
}

class Holder {
    private A a;
}

interface HolderLinker {
    @Field.Setter("a")
    void setA(A value);

    // a 的声明类型是 A，但运行时真实对象可能是 A2
    @Field.Getter("a.b")
    B getB();

    // 给 token a 指定更精确的类型，避免每次运行时再猜
    @Typed(name = "a", value = "your.pkg.A2")
    @Method.Expr("a.b.getVal()")
    String getBVal();
}
```

要点：

- 如果声明类型里不存在该字段/方法，框架会按运行时类型解析
- 如果你已经知道这个 token 的实际类型，可以用 `@Typed` 提前标注
- `@Typed` 写在接口上、方法上、参数上都可以
- `name` 对应表达式中的 token 名称，例如 `a`、`user`、`user.profile`

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

## 调试生成字节码

如果你想查看框架生成的 linker 实现类，可以先设置输出目录：

```java
LinkerFactory.setOutputPath("generated-linkers");
```

之后再执行 `createLinker(...)` / `createStaticLinker(...)`，生成的 `.class` 文件会输出到指定目录。
