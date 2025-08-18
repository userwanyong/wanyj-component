package cn.wanyj.component.dcc.listener;


import cn.wanyj.component.dcc.domain.entity.AttributeEntity;
import cn.wanyj.component.dcc.domain.service.DynamicConfigCenterService;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 永
 * 监听来自 redis 的数据变更
 */
public class DynamicConfigCenterAdjustListener implements MessageListener<AttributeEntity> {

    private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterAdjustListener.class);

    private final DynamicConfigCenterService dynamicConfigCenterService;

    public DynamicConfigCenterAdjustListener(DynamicConfigCenterService dynamicConfigCenterService) {
        this.dynamicConfigCenterService = dynamicConfigCenterService;
    }

    @Override
    public void onMessage(CharSequence charSequence, AttributeEntity attributeEntity) {
        try {
            log.info(">>>dcc config start adjust attribute from redis >>> name:{}, value:{}", attributeEntity.getAttributeName(),attributeEntity.getAttributeValue());
            dynamicConfigCenterService.adjustAttributeValue(attributeEntity);
        } catch (Exception e) {
            log.error(">>>dcc config adjust attribute value from redis error: {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
