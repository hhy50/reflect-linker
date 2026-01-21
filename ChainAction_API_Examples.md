# ChainAction 优化说明

## 主要优化内容

### 1. join 方法优化
- **原实现问题**：数组拷贝时使用了错误的长度 `newVarInsts.length - 1`，应该是 `varInsts.length`
- **优化后**：
  - 修复了数组拷贝长度错误
  - 增加了空数组检查，避免不必要的数组操作
  - 代码更清晰易读

### 2. 新增的流式 API

#### 数组操作相关
```java
// 合并多个 VarInst 到数组
ChainAction<VarInst[]> joinAll(ChainAction<VarInst>... chains)

// 在数组末尾追加单个元素
ChainAction<VarInst[]> append(ChainAction<VarInst[]> chain1, ChainAction<VarInst> chain2)

// 连接两个数组
ChainAction<VarInst[]> concat(ChainAction<VarInst[]> chain1, ChainAction<VarInst[]> chain2)
```

#### 函数式操作
```java
// 创建常量值的 ChainAction
ChainAction<T> constant(T value)

// 查看值但不转换（副作用）
ChainAction<T> peek(Consumer<T> consumer)
ChainAction<T> peekBody(BiFunction<MethodBody, T, Void> consumer)

// FlatMap 操作
ChainAction<Out> flatMap(Function<T, ChainAction<Out>> func)

// 过滤操作
ChainAction<T> filter(Predicate<T> predicate)

// 提供默认值
ChainAction<T> orElse(T defaultValue)
ChainAction<T> orElseGet(Supplier<T> supplier)
```

#### 组合操作
```java
// 组合两个 ChainAction
ChainAction<R> combine(ChainAction<U> other, BiFunction<T, U, R> combiner)

// 将两个 ChainAction 打包成 Pair
ChainAction<Pair<T, U>> zip(ChainAction<U> other)

// 执行 Action 后返回当前链
ChainAction<T> andThen(Action action)
```

## 使用示例

### 示例 1：使用 joinAll 合并多个参数
```java
ChainAction<VarInst> arg1 = ChainAction.of(() -> varInst1);
ChainAction<VarInst> arg2 = ChainAction.of(() -> varInst2);
ChainAction<VarInst> arg3 = ChainAction.of(() -> varInst3);

ChainAction<VarInst[]> allArgs = ChainAction.joinAll(arg1, arg2, arg3);
```

### 示例 2：使用 peek 进行调试
```java
ChainAction<VarInst> chain = someChain
    .peek(var -> System.out.println("Variable: " + var.getName()))
    .map(var -> transform(var));
```

### 示例 3：使用 filter 和 orElse
```java
ChainAction<VarInst> chain = someChain
    .filter(var -> var.getType() != Type.VOID_TYPE)
    .orElse(defaultVar);
```

### 示例 4：使用 flatMap 进行链式转换
```java
ChainAction<VarInst> chain = someChain
    .flatMap(var -> {
        if (needsTransform(var)) {
            return transformChain(var);
        }
        return ChainAction.constant(var);
    });
```

### 示例 5：使用 combine 组合多个链
```java
ChainAction<VarInst> owner = getOwnerChain();
ChainAction<VarInst[]> args = getArgsChain();

ChainAction<MethodInvokeAction> invokeAction = owner.combine(args, 
    (ownerVar, argsArray) -> createMethodInvoke(ownerVar, argsArray)
);
```

### 示例 6：使用 zip 打包结果
```java
ChainAction<VarInst> var1 = getVar1();
ChainAction<VarInst> var2 = getVar2();

ChainAction<Pair<VarInst, VarInst>> paired = var1.zip(var2);
paired.map(pair -> {
    VarInst left = pair.getLeft();
    VarInst right = pair.getRight();
    // 使用 left 和 right
});
```

## 性能优化

1. **join 方法**：修复了数组拷贝 bug，增加了空数组快速路径
2. **mapOwnerAndArgs**：增加了空数组检查，避免数组越界
3. **constant**：提供了创建常量链的高效方式
4. **所有新方法**：都采用了惰性求值，只在 doChain 时才执行

## 向后兼容性

所有原有的 API 保持不变，新增的 API 不会影响现有代码的使用。
