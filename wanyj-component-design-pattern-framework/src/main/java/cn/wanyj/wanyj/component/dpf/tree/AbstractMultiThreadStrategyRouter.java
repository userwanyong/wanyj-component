package cn.wanyj.wanyj.component.dpf.tree;


/**
 * @author 永
 * 路由抽象类
 */
public abstract class AbstractMultiThreadStrategyRouter<T, D, R> implements StrategyHandler<T, D, R>, StrategyMapper<T, D, R>{

    protected StrategyHandler<T, D, R> defaultStrategyHandler = StrategyHandler.DEFAULT;

    /**
     * 加载数据,处理业务
     * multiThread: 异步加载数据(如果需要)
     * doApply: 必须重写,编写具体的业务逻辑
     * @param request 入参
     * @param dynamicContext 上下文
     * @return R
     */
    @Override
    public R apply(T request, D dynamicContext) {
        // 异步加载数据
        multiThread(request,dynamicContext);
        // 处理业务逻辑
        return doApply(request, dynamicContext);
    }

    /**
     * 路由到下一节点
     * @param request 入参
     * @param dynamicContext 上下文
     * @return R
     */
    public R router(T request, D dynamicContext) {
        // 获取要执行的节点
        StrategyHandler<T, D, R> strategyHandler = getNext(request, dynamicContext);
        // 执行业务逻辑
        if (strategyHandler != null) {
            return strategyHandler.apply(request, dynamicContext);
        }
        return defaultStrategyHandler.apply(request, dynamicContext);
    }

    protected void multiThread(T request, D dynamicContext) {}

    protected abstract R doApply(T request, D dynamicContext);
}
