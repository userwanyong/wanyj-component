package cn.wanyj.component.test.dpf.tree.model.node;


import cn.wanyj.component.test.dpf.tree.model.AbstractXxxSupport;
import cn.wanyj.component.test.dpf.tree.model.factory.DynamicContext;
import cn.wanyj.wanyj.component.dpf.tree.StrategyHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
public class Node1 extends AbstractXxxSupport {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private Node2_1 node2_1;

    @Resource
    private Node2_2 node2_2;

    /**
     * 多线程异步加载数据
     *
     * @param userId         入参
     * @param dynamicContext 上下文
     */
    @Override
    protected void multiThread(String userId, DynamicContext dynamicContext) {
        CompletableFuture<Integer> mock1 = CompletableFuture.supplyAsync(() -> {
            log.info("线程1加载数据");
            return new Random().nextBoolean() ? 1 : 2;
        }, threadPoolExecutor);

        CompletableFuture<Integer> mock2 = CompletableFuture.supplyAsync(() -> {
            log.info("线程2加载数据");
            return new Random().nextBoolean() ? 1 : 2;
        }, threadPoolExecutor);

        CompletableFuture.allOf(mock1, mock2)
                .thenRun(() -> {
                    dynamicContext.setValue("mock1", mock1.join());
                    dynamicContext.setValue("mock2", mock2.join());
                }).join();

    }

    @Override
    protected String doApply(String userId, DynamicContext dynamicContext) {
        log.info("[Node1]:{}", userId);
        // 预期的和
        int sum = 3;
        log.info("预期和 sum={}", sum);

        dynamicContext.setSum(sum);

        return router(userId, dynamicContext);
    }

    @Override
    public StrategyHandler<String, DynamicContext, String> getNext(String userId, DynamicContext dynamicContext) {
        int mock1 = dynamicContext.getValue("mock1");
        int mock2 = dynamicContext.getValue("mock2");
        int sum = dynamicContext.getSum();
        log.info("mock1={},mock2={},sum={}", mock1, mock2, sum);
        if (mock1 + mock2 == sum) {
            log.info("mock1+mock2==sum, 符合预期, 走node2_1节点");
            return node2_1;
        }
        log.info("mock1+mock2!=sum, 不符合预期, 走node2_2节点");
        return node2_2;
    }
}
