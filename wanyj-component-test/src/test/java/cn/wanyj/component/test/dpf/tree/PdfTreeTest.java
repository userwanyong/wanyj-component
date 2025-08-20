package cn.wanyj.component.test.dpf.tree;

import cn.wanyj.component.test.dpf.tree.model.factory.DefaultStrategyFactory;
import cn.wanyj.component.test.dpf.tree.model.factory.DynamicContext;
import cn.wanyj.wanyj.component.dpf.tree.StrategyHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class PdfTreeTest {
    @Resource
    private DefaultStrategyFactory defaultStrategyFactory;

    @Test
    public void test() {
        StrategyHandler<String, DynamicContext, String> strategyHandler = defaultStrategyFactory.strategyHandler();
        String result = strategyHandler.apply("1379666", new DynamicContext());
        log.info("测试通过: {}",result);
    }

}
