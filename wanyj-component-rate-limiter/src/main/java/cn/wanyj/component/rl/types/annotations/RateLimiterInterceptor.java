package cn.wanyj.component.rl.types.annotations;

import java.lang.annotation.*;

/**
 * @author 永
 * 限流注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RateLimiterInterceptor {
    /**
     * 哪个字段作为限流标识
     */
    String key();

    /**
     * 限制频次（每秒请求次数）
     */
    int permitsPerSecond();

    /**
     * 黑名单拦截（多少次限制后加入黑名单）0-不限制
     */
    int blacklistCount() default 0;

    /**
     * 黑名单时间（多少时间后移出黑名单）默认 24h
     */
    int blacklistTime() default 24;

    /**
     * 拦截后的执行方法
     */
    String fallbackMethod();
}
