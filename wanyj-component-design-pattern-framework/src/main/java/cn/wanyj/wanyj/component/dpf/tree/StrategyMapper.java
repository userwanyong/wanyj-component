package cn.wanyj.wanyj.component.dpf.tree;


/**
 * @author 永
 * 策略映射器
 */
public interface StrategyMapper<T,D,R> {

    /**
     * 获取下一个节点
     * @param request 入参
     * @param dynamicContext 上下文
     * @return R
     */
    StrategyHandler<T,D,R> getNext(T request,D dynamicContext);

}
