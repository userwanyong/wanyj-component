package cn.wanyj.component.dcc.config;


import cn.wanyj.component.dcc.domain.service.DynamicConfigCenterService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

/**
 * @author 永
 */
@Configuration
public class DynamicConfigCenterAutoConfig implements BeanPostProcessor {

    private final DynamicConfigCenterService dynamicConfigCenterService;

    public DynamicConfigCenterAutoConfig(DynamicConfigCenterService dynamicConfigCenterService) {
        this.dynamicConfigCenterService = dynamicConfigCenterService;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return dynamicConfigCenterService.proxyObject(bean);
    }
}
