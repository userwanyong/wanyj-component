package cn.wanyj.component.test.rl;


import cn.wanyj.component.rl.types.annotations.RateLimiterInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 永
 * 动态限流组件测试类
 */
@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class RlController {

    /**
     * description 将 userId 作为限流标识，如果每秒请求数量超过 1 次，进行限流，调用 error 方法；
     * 如果 1 分钟内总限流次数达到 3 次，则加入黑名单，24h 后移出黑名单
     * http://127.0.0.1:8080/api/rl?userId=wanyj
     *
     * @param userId 用户 id
     * @return String
     */
    @GetMapping("/rl")
    @RateLimiterInterceptor(key = "userId", permitsPerSecond = 1, blacklistCount = 3, blacklistTime = 24, fallbackMethod = "error")
    public String rl(String userId) {
        log.info("pass: rl");
        return "pass: rl";
    }

    public String error(String userId) {
        log.warn("error: rateLimiter");
        return "error: rateLimiter";
    }
}
