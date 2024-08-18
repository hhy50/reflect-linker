## reflect-linker
reflect-linker 是一个用于链接反射对象的库，它可以帮助你在运行时动态使用对象中字段和方法。

使用方式：

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

