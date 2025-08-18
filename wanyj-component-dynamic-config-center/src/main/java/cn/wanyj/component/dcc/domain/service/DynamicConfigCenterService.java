package cn.wanyj.component.dcc.domain.service;


import cn.wanyj.component.dcc.domain.entity.AttributeEntity;

/**
 * @author 永
 * 动态配置中心服务接口
 */
public interface DynamicConfigCenterService {
    /**
     * 启动时对属性进行赋值
     *
     * @param bean bean
     * @return bean
     */
    Object proxyObject(Object bean);

    /**
     * 动态调整属性值
     *
     * @param attributeEntity 属性实体
     */
    void adjustAttributeValue(AttributeEntity attributeEntity);
}
