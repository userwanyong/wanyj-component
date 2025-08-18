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