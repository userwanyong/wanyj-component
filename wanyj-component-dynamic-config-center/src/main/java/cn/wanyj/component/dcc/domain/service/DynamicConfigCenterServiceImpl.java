package cn.wanyj.component.dcc.domain.service;


import cn.wanyj.component.dcc.config.DynamicConfigCenterAutoConfigProperties;
import cn.wanyj.component.dcc.domain.entity.AttributeEntity;
import cn.wanyj.component.dcc.types.annotations.DccConfig;
import cn.wanyj.component.dcc.types.common.Constants;
import io.micrometer.common.util.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 永
 * 动态配置中心实现类
 */
public class DynamicConfigCenterServiceImpl implements DynamicConfigCenterService {

    private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterServiceImpl.class);

    private final DynamicConfigCenterAutoConfigProperties properties;
    private final RedissonClient redissonClient;

    public DynamicConfigCenterServiceImpl(DynamicConfigCenterAutoConfigProperties properties, RedissonClient redissonClient) {
        this.properties = properties;
        this.redissonClient = redissonClient;
    }

    private final Map<String, Object> dccBeanGroup = new ConcurrentHashMap<>();
    private final Map<String, String> fieldNameGroup = new ConcurrentHashMap<>();

    @Override
    public Object proxyObject(Object bean) {
        Class<?> targetBeanClass = bean.getClass();
        Object targetBean = bean;
        if (AopUtils.isAopProxy(bean)) {
            targetBeanClass = AopUtils.getTargetClass(bean);
            targetBean = AopProxyUtils.getSingletonTarget(bean);
        }
        Field[] fields = targetBeanClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(DccConfig.class)) {
                continue;
            }
            DccConfig dccConfig = field.getAnnotation(DccConfig.class);
            String value = dccConfig.value();
            if (StringUtils.isBlank(value)) {
                log.error(">>>init attribute error: {}", field.getName() + " @DccConfig is not config value config case 「isSwitch:1」");
                throw new RuntimeException(field.getName() + " @DccConfig is not config value config case 「isSwitch:1」");
            }
            String attributeName = properties.getFinalAttributeName(field.getName());
            String defaultAttributeValue = value;
            if (value.contains(Constants.COLON)) {
                String[] split = value.split(Constants.COLON);
                attributeName = properties.getFinalAttributeName(split[0].trim());
                defaultAttributeValue = split.length == 2 ? split[1].trim() : null;
            }
            fieldNameGroup.put(attributeName, field.getName());
            try {
                if (StringUtils.isBlank(defaultAttributeValue)) {
                    log.error(">>>init attribute value error: {}", field.getName() + " @DccConfig value is blank");
                    throw new RuntimeException(field.getName() + " @DccConfig value is blank");
                }
                RBucket<String> bucket = redissonClient.getBucket(attributeName);
                if (!bucket.isExists()) {
                    bucket.set(defaultAttributeValue);
                } else {
                    defaultAttributeValue = bucket.get();
                }
                field.setAccessible(true);
                field.set(targetBean, defaultAttributeValue);
                field.setAccessible(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            dccBeanGroup.put(attributeName, targetBean);
        }
        return bean;
    }

    @Override
    public void adjustAttributeValue(AttributeEntity attributeEntity) {
        String attributeName = properties.getFinalAttributeName(attributeEntity.getAttributeName());
        String attributeValue = attributeEntity.getAttributeValue();
        RBucket<String> bucket = redissonClient.getBucket(attributeName);
        if (!bucket.isExists()) {
            log.warn(">>>dcc config attribute name:{} not exists", attributeName);
            return;
        }
        bucket.set(attributeValue);
        Object targetBean = dccBeanGroup.get(attributeName);
        if (targetBean == null) {
            return;
        }
        Class<?> targetBeanClass = targetBean.getClass();
        if (AopUtils.isAopProxy(targetBean)) {
            targetBeanClass = AopUtils.getTargetClass(targetBean);
        }
        String fieldName = fieldNameGroup.get(attributeName);
        try {
            Field field = targetBeanClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(targetBean, attributeValue);
            field.setAccessible(false);
            log.info(">>>dcc config adjust attribute value from redis success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
