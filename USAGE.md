# Expression Usage

## 1. Default same-name method call

If an interface method has no annotation, the framework treats it as a call to a method with the same name by default.

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

The `hello(String prefix)` above is equivalent to:

```java
interface UserLinker {
    @Method.Expr("hello($0)")
    String hello(String prefix);
}
```

## 2. Chained access

Both field expressions and method expressions support chained access.

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

## 3. Parameter placeholders, constants, and full forwarding

### Parameter placeholders

```java
interface UserLinker {
    @Method.Expr("rename($0)")
    void rename(String newName);

    // @Method.Expr("merge($0, $1)") is equivalent.
    @Method.Expr("merge(..)")
    String merge(String left, String right);
}
```

## 4. Nested expressions

The arguments of an expression can themselves be expressions. This is especially useful for complex call chains.

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

## 5. Null-safety

Null-safety is enabled with `?`. When added after a step, it means: if the result of that step is `null`, the whole expression returns the default value immediately.

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

Behavior:

- Reference types return `null` after short-circuiting
- Primitive types return the JVM default value after short-circuiting: `0` / `0L` / `0.0` / `false` / `\0`
- `?` only protects the current step; it does not automatically cover the entire chain

For example:

- `user?.address.city`: only protects `user`
- `user.address?.city`: only protects `address`
- `getUser()?.getAddress().getCity()`: only protects the return value of `getUser()`

## 6. Indexed access

Indexed access supports arrays, `List`, and `Map`, including mixed nesting.

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

Supported forms:

- `array[0]`
- `list[1]`
- `grid[1][0]`
- `map['key']`
- `mixed['teams'][1]['lead']`
- `methodCall()[0]`
- `methodCall()['key']`

Notes:

- String keys must use single quotes: `['captain']`
- Multi-dimensional indexes can be chained: `grid[1][0]`
- If the target value is `Object`, the framework will automatically try to convert it based on the interface method's return type

## 7. `@Autolink`

`@Autolink` can automatically handle linker parameters and linker return values.

That means:

- You pass a `PilotView` to the interface method
- The target method actually expects a `Pilot`
- The framework automatically unwraps `PilotView` to the underlying target object

Likewise, if the target method returns `Pilot` or `Badge`, while your interface defines `PilotView` or `BadgeView`, the framework automatically wraps them as new linkers.

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

`@Autolink` can be placed on:

- The interface: enables autolink for the entire linker by default
- A method: enables it only for the current method
- A getter: enables it for that getter's return value

## 8. Dynamic types and `@Typed`

When a field or method in an expression exists on the runtime type but not on the declared type, the framework falls back to runtime resolution.

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

    // The declared type of a is A, but the runtime object may actually be A2.
    @Field.Getter("a.b")
    B getB();

    // Assign a more precise type to token a to avoid guessing at runtime every time.
    @Typed(name = "a", value = "your.pkg.A2")
    @Method.Expr("a.b.getVal()")
    String getBVal();
}
```

Key points:

- If the declared type does not contain the field or method, the framework resolves it against the runtime type
- If you already know the real type of a token, you can declare it in advance with `@Typed`
- `@Typed` can be placed on an interface, method, or parameter
- `name` corresponds to the token name in the expression, such as `a`, `user`, or `user.profile`

## 9. Importing static members with `@ImportStatic`

`@ImportStatic` adds the **static methods** and **static fields** of the given classes to the lookup process, much like Java's static import.

Lookup rules:

- **this wins**: the target object's own fields/methods are looked up first; the imported classes are searched for static members only when nothing is found
- When multiple classes are imported, they are searched in declaration order
- It can be placed on the interface (applies to all methods) or on a single method (method-level completely overrides the class-level one)
- Public static methods (with public parameter/return types) are compiled directly into `invokestatic` calls;
  non-public static members (private, package-private, etc.) are invoked through privileged `MethodHandle` lookups

```java
import io.github.hhy50.linker.LinkerFactory;
import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.ImportStatic;
import io.github.hhy50.linker.annotations.Method;
import io.github.hhy50.linker.exceptions.LinkerException;

class Strings {
    public static String DEFAULT = "default";

    public static String join(String left, String right) {
        return left + "-" + right;
    }

    private static String secret() {
        return "secret";
    }
}

class MathUtil {
    public static int doubleIt(int x) {
        return x * 2;
    }
}

@ImportStatic(value = Strings.class, classes = {"your.pkg.MathUtil"}) // value takes a single class; classes takes class names, each element may contain multiple comma-separated names
interface MyLinker {
    // Resolved to the static method Strings.join
    String join(String left, String right);

    // Private static methods can be invoked too
    String secret();

    // Reads a static field
    @Field.Getter("DEFAULT")
    String getDefault();

    // this wins: if the target object has its own join method, that one is called
    String join(String s);

    // Imported static members can start a chained expression
    @Method.Expr("DEFAULT.length()")
    int defaultLength();

    // Method-level import: applies only to this method and completely overrides the class-level import
    @Method.Expr("doubleIt($0)")
    @ImportStatic(MathUtil.class)
    int doubleIt(int x);
}

public class Example {
    public static void main(String[] args) throws LinkerException {
        MyLinker linker = LinkerFactory.createLinker(MyLinker.class, new MyTarget());
        System.out.println(linker.join("ab", "cd")); // ab-cd
        System.out.println(linker.secret());         // secret
        System.out.println(linker.getDefault());     // default
    }
}
```
