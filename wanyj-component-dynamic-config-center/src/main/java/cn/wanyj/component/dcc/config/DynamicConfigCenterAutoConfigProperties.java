package cn.wanyj.component.dcc.config;


import cn.wanyj.component.dcc.types.common.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 永
 */
@ConfigurationProperties(prefix = "wanyj.component.dcc", ignoreInvalidFields = true)
public class DynamicConfigCenterAutoConfigProperties {

    /**
     * 系统名称
     */
    private String system;

    public String getFinalAttributeName(String attributeName) {
        return this.system + Constants.UNDERLINE + attributeName;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}
