package cn.wanyj.component.test.dpf.chain.model.factory;

import cn.wanyj.component.test.dpf.chain.model.node.ChainNode1;
import cn.wanyj.component.test.dpf.chain.model.node.ChainNode2;
import cn.wanyj.wanyj.component.dpf.chain.BusinessLinkedList;
import cn.wanyj.wanyj.component.dpf.chain.ChainArmory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class DefaultChainFactory {

    @Bean
    public BusinessLinkedList<String, DynamicContext, String> d1(ChainNode1 node1, ChainNode2 node2){
        ChainArmory<String, DynamicContext, String> chainArmory = new ChainArmory<>("d1", node1, node2);
        return chainArmory.getBusinessLinkedList();
    }

}
