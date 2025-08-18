package cn.wanyj.component.dcc.config;


import cn.wanyj.component.dcc.domain.entity.AttributeEntity;
import cn.wanyj.component.dcc.domain.service.DynamicConfigCenterService;
import cn.wanyj.component.dcc.domain.service.DynamicConfigCenterServiceImpl;
import cn.wanyj.component.dcc.listener.DynamicConfigCenterAdjustListener;
import cn.wanyj.component.dcc.types.common.Constants;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 永
 */
@Configuration
@EnableConfigurationProperties(value = DynamicConfigCenterAutoConfigProperties.class)
public class DynamicConfigCenterRegisterAutoConfig {

    @Bean
    public DynamicConfigCenterService dynamicConfigCenterService(DynamicConfigCenterAutoConfigProperties properties, RedissonClient redissonClient) {
        return new DynamicConfigCenterServiceImpl(properties, redissonClient);
    }

    @Bean
    public DynamicConfigCenterAdjustListener dynamicConfigCenterAdjustListener(DynamicConfigCenterService dynamicConfigCenterService) {
        return new DynamicConfigCenterAdjustListener(dynamicConfigCenterService);
    }

    @Bean
    public RTopic dynamicConfigCenterRedisTopic(DynamicConfigCenterAutoConfigProperties properties, RedissonClient redissonClient, DynamicConfigCenterAdjustListener dynamicConfigCenterAdjustListener) {
        RTopic topic = redissonClient.getTopic(Constants.getTopic(properties.getSystem()));
        topic.addListener(AttributeEntity.class, dynamicConfigCenterAdjustListener);
        return topic;
    }

}
