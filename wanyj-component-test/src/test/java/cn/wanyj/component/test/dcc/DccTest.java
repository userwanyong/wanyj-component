package cn.wanyj.component.test.dcc;

import cn.wanyj.component.dcc.domain.entity.AttributeEntity;
import cn.wanyj.component.dcc.types.annotations.DccConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RTopic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class DccTest {

    @DccConfig("0")
    private String t1;

    @DccConfig("custom:0")
    private String t2;

    @Resource
    private RTopic dynamicConfigCenterRedisTopic;

    @Test
    public void testInit() {
        log.info("默认属性名:{},属性值:{}", "t1", t1);
        log.info("自定义属性名:{},属性值:{}", "custom", t2);
    }

    @Test
    public void testAdjustAttributeValue() throws InterruptedException {
        dynamicConfigCenterRedisTopic.publish(new AttributeEntity("t1", "1"));
        dynamicConfigCenterRedisTopic.publish(new AttributeEntity("custom", "1"));
        new CountDownLatch(1).await();
    }

}
