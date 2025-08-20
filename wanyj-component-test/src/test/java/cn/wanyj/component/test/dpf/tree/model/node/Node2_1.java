package cn.wanyj.component.test.dpf.tree.model.node;


import cn.wanyj.component.test.dpf.tree.model.AbstractXxxSupport;
import cn.wanyj.component.test.dpf.tree.model.factory.DynamicContext;
import cn.wanyj.wanyj.component.dpf.tree.StrategyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Node2_1 extends AbstractXxxSupport {


    @Override
    protected String doApply(String userId, DynamicContext dynamicContext) {
        log.info("[Node2_1]:{}", userId);
        return router(userId, dynamicContext);
    }

    @Override
    public StrategyHandler<String, DynamicContext, String> getNext(String userId, DynamicContext dynamicContext) {
        return defaultStrategyHandler;
    }
}
