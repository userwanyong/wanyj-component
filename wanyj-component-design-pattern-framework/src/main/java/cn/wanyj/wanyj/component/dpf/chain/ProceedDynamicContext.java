package cn.wanyj.wanyj.component.dpf.chain;


/**
 * @author 永
 * 判断是否走向下一个节点
 */
public class ProceedDynamicContext {
    private boolean proceed;

    public ProceedDynamicContext() {
        this.proceed = true;
    }

    public boolean isProceed() {
        return proceed;
    }

    public void setProceed(boolean proceed) {
        this.proceed = proceed;
    }
}
