# Getting Started

## 1. 动态配置中心

### 1.1. 简介
一个分布式动态配置中心，基于Redisson实现，通过发布/订阅模式实现属性值的动态更新

### 1.2 优点
- 保证了分布式场景的数据一致性
- 不需要频繁的从Redis中获取数据

### 1.3 使用方法
#### step1: 引入依赖
```xml
<dependency>
    <groupId>cn.wanyj</groupId>
    <artifactId>dynamic-config-center-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```
#### step2: 配置yml
```yml
wanyj:
  component:
    dcc:
      system:
spring:
  data:
    redis:
      database: 
      host:
      port:
      password:
      ssl: 
      timeout:
      connectTimeout:
      clientName:
      cluster:
        nodes:
      sentinel:
        master:
        nodes:
```
对于Redisson的配置，可以参考Redisson的官方文档：https://redisson.pro/docs/integration-with-spring 按需配置即可
#### step3: 使用注解
1. 使用字段名作为属性名
```java
@DccConfig("0")
private String t1;
```
2. 使用自定义属性名
```java
@DccConfig("custom:0")
private String t2;
```

## 2. 设计模式框架 - 规则树

### 2.1. 简介
解耦业务逻辑与流程流转，将特定业务代码封装成节点，动态组装使用

### 2.2 优点
- 避免频繁地编写 if…else 逻辑
- 可以回到已经走过的节点，实现节点复用

### 2.3 使用方法
#### step1: 引入依赖
```xml
<dependency>
    <groupId>cn.wanyj</groupId>
    <artifactId>design-pattern-framework</artifactId>
    <version>1.0.0</version>
</dependency>
```
#### step2: 根据需求，自定义上下文实体
```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DynamicContext {
    private int sum;

    private Map<String, Object> dataObjects = new HashMap<>();

    public <T> void setValue(String key, T value) {
        dataObjects.put(key, value);
    }

    public <T> T getValue(String key) {
        return (T) dataObjects.get(key);
    }
}
```
#### step3: 自定义抽象类继承`AbstractMultiThreadStrategyRouter<T, D, R>`类
```java
public abstract class AbstractXxxSupport extends AbstractMultiThreadStrategyRouter<入参, 上下文实体, 出参> {
    
}
```
#### step4: 编写各个节点，并继承自定义的抽象类，重写`doApply`和`getNext`方法，如果有数据要加载，重写`multiThread`方法
```java
@Slf4j
@Component
public class Node1 extends AbstractXxxSupport {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private Node2_1 node2_1;

    @Resource
    private Node2_2 node2_2;
    
    @Override
    protected void multiThread(String userId, DynamicContext dynamicContext) {
        CompletableFuture<Integer> mock1 = CompletableFuture.supplyAsync(() -> {
            log.info("线程1加载数据");
            return new Random().nextBoolean() ? 1 : 2;
        }, threadPoolExecutor);

        CompletableFuture<Integer> mock2 = CompletableFuture.supplyAsync(() -> {
            log.info("线程2加载数据");
            return new Random().nextBoolean() ? 1 : 2;
        }, threadPoolExecutor);

        CompletableFuture.allOf(mock1, mock2)
                .thenRun(() -> {
                    dynamicContext.setValue("mock1", mock1.join());
                    dynamicContext.setValue("mock2", mock2.join());
                }).join();
    }

    @Override
    protected String doApply(String userId, DynamicContext dynamicContext) {
        log.info("[Node1]:{}", userId);
        int sum = 3;
        log.info("预期和 sum={}", sum);
        dynamicContext.setSum(sum);
        return router(userId, dynamicContext);
    }

    @Override
    public StrategyHandler<String, DynamicContext, String> getNext(String userId, DynamicContext dynamicContext) {
        int mock1 = dynamicContext.getValue("mock1");
        int mock2 = dynamicContext.getValue("mock2");
        int sum = dynamicContext.getSum();
        log.info("mock1={},mock2={},sum={}", mock1, mock2, sum);
        if (mock1+mock2== sum) {
            log.info("mock1+mock2==sum, 符合预期, 走node2_1节点");
            return node2_1;
        }
        log.info("mock1+mock2!=sum, 不符合预期, 走node2_2节点");
        return node2_2;
    }
}
```
#### step5: 指定入口节点
```java
@Service
public class DefaultStrategyFactory {

    private final Node1 node1;

    public DefaultStrategyFactory(Node1 node1) {
        this.node1 = node1;
    }

    public StrategyHandler<String, DynamicContext, String> strategyHandler() {
        return node1;
    }
}
```
#### step6: 传入参数，执行规则树
```java
StrategyHandler<String, DynamicContext, String> strategyHandler = defaultStrategyFactory.strategyHandler();
String result = strategyHandler.apply("1379666", new DynamicContext());
```

## 3. 设计模式框架 - 责任链

### 3.1. 简介
预先组装好责任链节点，符合条件的请求按顺序执行

### 3.2 优点
- 拆成有针对性的节点，避免一个类中写大量代码
- 便于调整先后顺序

### 3.3 使用方法
#### step1: 引入依赖
```xml
<dependency>
    <groupId>cn.wanyj</groupId>
    <artifactId>design-pattern-framework</artifactId>
    <version>1.0.0</version>
</dependency>
```
#### step2: 根据需求，自定义上下文实体，并继承`ProceedDynamicContext`类
```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DynamicContext extends ProceedDynamicContext{
    private int sum;

    private Map<String, Object> dataObjects = new HashMap<>();

    public <T> void setValue(String key, T value) {
        dataObjects.put(key, value);
    }

    public <T> T getValue(String key) {
        return (T) dataObjects.get(key);
    }
}
```

#### step3: 编写各个节点，并实现`ChainHandler<T, D extends ProceedDynamicContext, R>`接口
- next(T request, D dynamicContext) 继续执行下一个节点
- stop(T request, D dynamicContext, R result) 停止执行，返回当前结果
```java
@Slf4j
@Component
public class ChainNode1 implements ChainHandler<String, DynamicContext, String> {
    @Override
    public String apply(String userId, DynamicContext dynamicContext) {
        log.info("[ChainNode1]");
        return next(userId, dynamicContext);
    }
}
```
#### step5: 组装责任链
```java
@Service
public class DefaultChainFactory {
    @Bean
    public BusinessLinkedList<String, DynamicContext, String> d1(ChainNode1 node1, ChainNode2 node2){
        ChainArmory<String, DynamicContext, String> chainArmory = new ChainArmory<>("d1", node1, node2);
        return chainArmory.getBusinessLinkedList();
    }
}
```
#### step6: 传入参数，执行责任链
```java
@Resource
private BusinessLinkedList<String, DynamicContext, String> businessLinkedList;
String result = businessLinkedList.apply("1379666", new DynamicContext());
```