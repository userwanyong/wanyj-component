package cn.wanyj.component.rl.config;


import cn.wanyj.component.rl.aop.RateLimiterAOP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 永
 */
@Configuration
public class RateLimiterAutoConfig {
    private final Logger log = LoggerFactory.getLogger(RateLimiterAutoConfig.class);

    @Bean
    public RateLimiterAOP rateLimiterAOP() {
        RateLimiterAOP rateLimiterAOP = new RateLimiterAOP();
        log.info("Rate limiter spring boot starter init success");
        return rateLimiterAOP;
    }
}
