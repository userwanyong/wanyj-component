package cn.wanyj.component.dcc.types.common;


/**
 * @author 永
 */
public class Constants {
    public final static String DYNAMIC_CONFIG_CENTER_REDIS_TOPIC = "dynamic_config_center_redis_topic_";

    public final static String COLON = ":";

    public final static String UNDERLINE = "_";

    public static String getTopic(String systemName) {
        return DYNAMIC_CONFIG_CENTER_REDIS_TOPIC + systemName;
    }

}
