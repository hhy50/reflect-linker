## reflect-linker
reflect-linker 是一个用于链接反射对象的库，它可以帮助你在运行时代替反射去使用对象中字段和方法。

简单尝试：

1. 引入依赖

```xml
<dependency>
    <groupId>io.github.hhy</groupId>
    <artifactId>reflect-linker</artifactId>
    <version>1.0.0</version>
</dependency>
```
2. 定义访问规则
以ArrayList举例
```java
@Target.Bind("java.util.ArrayList")
public interface MyArrayList {
    
    void add(Object o);
    
    @Field.Getter("elementData")
    Object[] getElementData();
    
    @Field.Setter("elementData")
    void setElementData(Object[] elementData);
}
```
3. 创建连接器
```java
class Example {
    public static void main(String[] args) throws LinkerException {
        MyArrayList list = LinkerFactory.createLinker(MyArrayList.class, new ArrayList<>());
        
        // do thing
    }
}
```

### 更多用法:

1. 链式表达式访问

```java
class User {
    private String name;
    private int age;
}
class UserWrap {
    private User user;
}
@Target.Bind("io.github.hhy50.linker.example.nest.UserWrap")
interface UserVisitor {
    @Field.Getter("user")
    User getUser();

    @Field.Setter("user")
    void setUser(User user);

    @Field.Setter("user.name")
    void setName(String val);

    @Field.Getter("user.name")
    String getName();

    @Field.Setter("user.age")
    void setAge(int val);

    @Field.Getter("user.age")
    int getAge();
}

class Example {
    public static void main(String[] args) throws LinkerException {
        UserWrap userWrap = new UserWrap();
        UserVisitor userVisitor = LinkerFactory.createLinker(UserVisitor.class, userWrap);
        userVisitor.setUser(new User());
        userVisitor.setAge(20);
        userVisitor.setName("example");

        // do something
    }
}
```

2. 动态类型

```java
class A { }
class B {
    String val = "this is b'val";
}
class A2 extends A {
    private B b;
    public A2(B b) {
        this.b = b;
    }
}
class MyObject {
    private A a;
}
@Target.Bind("io.github.hhy50.linker.example.dynamic.MyObject")
interface MyObjectVisitor {
    @Field.Setter("a")
    void setA(A val);

    @Field.Getter("a.b")
    B getB();
}
class Example {
    public static void main(String[] args) throws LinkerException {
        MyObject myObj = new MyObject();
        MyObjectVisitor myObjVisitor = LinkerFactory.createLinker(MyObjectVisitor.class, myObj);
        myObjVisitor.setA(new A2(new B()));
        System.out.println(myObjVisitor.getB().val);

        // do something
    }
}
```

上面表达式`@Field.Getter("a.b")`中, `A class`是没有`B field`的, `B field`存在于`A`的子类中, 这样的字段视为运行时字段, 这样会在调用对应方法时动态获取`A`的类型

如果您非常在意执行性能, 可以使用`@Typed`指定`A class`的类型, 将`B field`变为已知的字段
```java
//@Typed(name = "a", type = "io.github.hhy50.linker.example.dynamic.A2") // 也可以声明在这里
@Target.Bind("io.github.hhy50.linker.example.dynamic.MyObject")
interface MyObjectVisitor {
    @Field.Setter("a")
    void setA(A val);

    @Typed(name = "a", type = "io.github.hhy50.linker.example.dynamic.A2")
    @Field.Getter("a.b")
    B getB();
}
```
然后重新`run`
