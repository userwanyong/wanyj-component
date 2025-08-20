package cn.wanyj.wanyj.component.dpf.chain;


/**
 * @author 永
 * 链路处理器
 */
public interface ChainHandler<T, D extends ProceedDynamicContext, R> {
    /**
     * 处理具体的业务
     *
     * @param request        入参
     * @param dynamicContext 上下文
     * @return R
     */
    R apply(T request, D dynamicContext);

    /**
     * 获取下一个节点
     *
     * @param request        入参
     * @param dynamicContext 上下文
     * @return R
     */
    default R next(T request, D dynamicContext) {
        dynamicContext.setProceed(true);
        return null;
    }

    /**
     * 停止
     *
     * @param request        入参
     * @param dynamicContext 上下文
     * @param result         结果
     * @return R
     */
    default R stop(T request, D dynamicContext, R result) {
        dynamicContext.setProceed(false);
        return result;
    }
}
