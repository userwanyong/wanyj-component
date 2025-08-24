package cn.wanyj.component.rl.aop;


import cn.wanyj.component.dcc.types.annotations.DccConfig;
import cn.wanyj.component.rl.types.annotations.RateLimiterInterceptor;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author 永
 * 限流切面1
 */
@Aspect
public class RateLimiterAOP {
    private final Logger log = LoggerFactory.getLogger(RateLimiterAOP.class);

    @DccConfig("open")
    private String rateLimiterSwitch;

    @Resource
    private RedissonClient redissonClient;

    private static final String BLACKLIST = "blacklist_";
    private static final String RATE_LIMITER = "rate_limiter_";


    @Pointcut("@annotation(cn.wanyj.component.rl.types.annotations.RateLimiterInterceptor)")
    public void aopPoint() {
    }

    @Around("aopPoint()&&@annotation(rateLimiterInterceptor)")
    public Object doRouter(ProceedingJoinPoint pj, RateLimiterInterceptor rateLimiterInterceptor) throws Throwable {
        // 判断限流开关是否开启，未开启直接放行
        if (StringUtils.isBlank(rateLimiterSwitch) || "close".equals(rateLimiterSwitch)) {
            return pj.proceed();
        }
        String key = rateLimiterInterceptor.key();
        if (StringUtils.isBlank(key)) {
            log.info(">>>@RateLimiterInterceptor key is null");
            throw new RuntimeException(">>>@RateLimiterInterceptor key is null");
        }
        // 获取字段的属性值
        String attrValue = getAttrValue(key, pj.getArgs());
        log.info(">>>@RateLimiterInterceptor attr value is {}", attrValue);

        // 黑名单判断
        int blacklistTime = rateLimiterInterceptor.blacklistTime();
        if (rateLimiterInterceptor.blacklistCount() != 0 && checkAndIncrementBlacklist(attrValue, rateLimiterInterceptor.blacklistCount())) {
            log.info(">>>限流-黑名单拦截({}h) attr value is {}", blacklistTime, attrValue);
            return fallbackMethodResult(pj, rateLimiterInterceptor.fallbackMethod());
        }

        // 获取分布式限流器
        RRateLimiter rateLimiter = getDistributedRateLimiter(attrValue, rateLimiterInterceptor.permitsPerSecond());

        // 限流拦截
        if (!rateLimiter.tryAcquire()) {
            if (rateLimiterInterceptor.blacklistCount() != 0) {
                String redisKey = BLACKLIST + attrValue;
                RAtomicLong counter = redissonClient.getAtomicLong(redisKey);
                long current = counter.incrementAndGet();
                if (current == 1) {
                    // 如果是第一次设置，则添加过期时间
                    counter.expire(blacklistTime, TimeUnit.HOURS);
                }
            }
            log.info(">>>限流-超频次拦截 attr value is {}", attrValue);
            return fallbackMethodResult(pj, rateLimiterInterceptor.fallbackMethod());
        }

        return pj.proceed();
    }

    private boolean checkAndIncrementBlacklist(String keyAttr, long maxCount) {
        String redisKey = BLACKLIST + keyAttr;
        RAtomicLong counter = redissonClient.getAtomicLong(redisKey);
        long current = Long.parseLong(String.valueOf(counter));
        return current >= maxCount;
    }

    private RRateLimiter getDistributedRateLimiter(String key, long permitsPerSecond) {
        String redisKey = RATE_LIMITER + key;
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(redisKey);
        if (!rateLimiter.isExists()) {
            rateLimiter.trySetRate(RateType.OVERALL, permitsPerSecond, 1, RateIntervalUnit.SECONDS);
        }
        return rateLimiter;
    }

    /**
     * 调用用户配置的回调方法，当拦截后，返回回调结果。
     */
    private Object fallbackMethodResult(JoinPoint jp, String fallbackMethod) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        Method method = jp.getTarget().getClass().getMethod(fallbackMethod, methodSignature.getParameterTypes());
        return method.invoke(jp.getThis(), jp.getArgs());
    }


    /**
     * 返回第一个满足条件的属性值
     */
    public String getAttrValue(String attr, Object[] args) {
        if (args[0] instanceof String) {
            return args[0].toString();
        }
        String filedValue = null;
        for (Object arg : args) {
            try {
                if (StringUtils.isNotBlank(filedValue)) {
                    break;
                }
                filedValue = String.valueOf(this.getValueByName(arg, attr));
            } catch (Exception e) {
                log.error("获取路由属性值失败 attr：{}", attr, e);
            }
        }
        return filedValue;
    }

    /**
     * 获取对象的特定属性值
     *
     * @param item 对象
     * @param name 属性名
     * @return 属性值
     */
    private Object getValueByName(Object item, String name) {
        try {
            Field field = getFieldByName(item, name);
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            Object o = field.get(item);
            field.setAccessible(false);
            return o;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 根据名称获取方法，该方法同时兼顾继承类获取父类的属性
     *
     * @param item 对象
     * @param name 属性名
     * @return 该属性对应方法
     */
    private Field getFieldByName(Object item, String name) {
        try {
            Field field;
            try {
                field = item.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                field = item.getClass().getSuperclass().getDeclaredField(name);
            }
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

}
