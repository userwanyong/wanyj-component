package cn.wanyj.wanyj.component.dpf.tree;


/**
 * @author 永
 * 策略处理器
 */
public interface StrategyHandler<T,D,R> {

    StrategyHandler DEFAULT = (T, D) -> null;

    /**
     * 处理具体的业务
     * @param request 入参
     * @param dynamicContext 上下文
     * @return R
     */
    R apply(T request,D dynamicContext);

}
