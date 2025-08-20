package cn.wanyj.component.test.dpf.chain;

import cn.wanyj.component.test.dpf.chain.model.factory.DynamicContext;
import cn.wanyj.wanyj.component.dpf.chain.BusinessLinkedList;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DpfChainTest {

    @Resource
    private BusinessLinkedList<String, DynamicContext, String> businessLinkedList;

    @Test
    public void test() {
        String result = businessLinkedList.apply("1379666", new DynamicContext());
        log.info("测试通过:{}", result);
    }

}
