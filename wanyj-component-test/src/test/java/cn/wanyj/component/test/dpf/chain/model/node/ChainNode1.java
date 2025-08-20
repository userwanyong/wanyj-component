package cn.wanyj.component.test.dpf.chain.model.node;

import cn.wanyj.component.test.dpf.chain.model.factory.DynamicContext;
import cn.wanyj.wanyj.component.dpf.chain.ChainHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChainNode1 implements ChainHandler<String, DynamicContext, String> {
    @Override
    public String apply(String userId, DynamicContext dynamicContext) {
        log.info("[ChainNode1]");
        return next(userId, dynamicContext);
    }
}
