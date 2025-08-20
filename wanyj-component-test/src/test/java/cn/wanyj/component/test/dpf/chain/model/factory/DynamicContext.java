package cn.wanyj.component.test.dpf.chain.model.factory;

import cn.wanyj.wanyj.component.dpf.chain.ProceedDynamicContext;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DynamicContext extends ProceedDynamicContext {
    private int sum;

    private Map<String, Object> dataObjects = new HashMap<>();

    public <T> void setValue(String key, T value) {
        dataObjects.put(key, value);
    }

    public <T> T getValue(String key) {
        return (T) dataObjects.get(key);
    }
}
