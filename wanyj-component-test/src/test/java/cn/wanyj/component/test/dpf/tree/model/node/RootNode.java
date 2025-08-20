package cn.wanyj.component.test.dpf.tree.model.node;


import cn.wanyj.component.test.dpf.tree.model.AbstractXxxSupport;
import cn.wanyj.component.test.dpf.tree.model.factory.DynamicContext;
import cn.wanyj.wanyj.component.dpf.tree.StrategyHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RootNode extends AbstractXxxSupport {

    @Resource
    protected Node1 node1;

    @Override
    protected String doApply(String request, DynamicContext dynamicContext) {
        log.info("[RootNode]:{}", request);
        return router(request, dynamicContext);
    }

    @Override
    public StrategyHandler<String, DynamicContext, String> getNext(String request, DynamicContext dynamicContext) {
        return node1;
    }
}
