package cn.wanyj.component.test.dpf.tree.model.factory;


import cn.wanyj.component.test.dpf.tree.model.node.RootNode;
import cn.wanyj.wanyj.component.dpf.tree.StrategyHandler;
import org.springframework.stereotype.Service;

@Service
public class DefaultStrategyFactory {

    private final RootNode rootNode;

    public DefaultStrategyFactory(RootNode rootNode) {
        this.rootNode = rootNode;
    }

    public StrategyHandler<String, DynamicContext, String> strategyHandler() {
        return rootNode;
    }

}
