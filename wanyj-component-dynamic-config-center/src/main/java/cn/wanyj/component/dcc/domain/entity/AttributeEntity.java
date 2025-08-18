package cn.wanyj.component.dcc.domain.entity;


/**
 * @author 永
 * 属性值实体
 */
public class AttributeEntity {

    private String attributeName;

    private String attributeValue;

    public AttributeEntity(String attributeName, String attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }
}
