package cn.wanyj.wanyj.component.dpf.chain;


/**
 * @author 永
 * 业务链路
 */
public class BusinessLinkedList<T, D extends ProceedDynamicContext, R> extends MyLinkedList<ChainHandler<T, D, R>> implements ChainHandler<T, D, R> {

    public BusinessLinkedList(String name) {
        super(name);
    }

    /**
     * 依次执行责任链
     * @param request 入参
     * @param dynamicContext 上下文
     * @return R
     */
    @Override
    public R apply(T request, D dynamicContext) {
        Node<ChainHandler<T, D, R>> current = this.first;
        do {
            ChainHandler<T, D, R> item = current.item;
            R apply = item.apply(request, dynamicContext);
            if (!dynamicContext.isProceed()) {
                return apply;
            }
            current = current.next;
        } while (current != null);
        return null;
    }
}
