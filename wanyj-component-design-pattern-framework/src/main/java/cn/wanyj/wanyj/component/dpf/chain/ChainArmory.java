package cn.wanyj.wanyj.component.dpf.chain;


/**
 * @author 永
 * 链路装配
 */
public class ChainArmory<T, D extends ProceedDynamicContext, R>{

    private final BusinessLinkedList<T, D, R> businessLinkedList;

    /**
     * 构建责任链
     * @param linkName 责任链名称
     * @param chainHandlers 责任链节点
     */
    @SafeVarargs
    public ChainArmory(String linkName, ChainHandler<T, D, R>...chainHandlers) {
        businessLinkedList=new BusinessLinkedList<>(linkName);
        for (ChainHandler<T, D, R> chainHandler : chainHandlers) {
            businessLinkedList.add(chainHandler);
        }
    }

    public BusinessLinkedList<T, D, R> getBusinessLinkedList() {
        return businessLinkedList;
    }
}
