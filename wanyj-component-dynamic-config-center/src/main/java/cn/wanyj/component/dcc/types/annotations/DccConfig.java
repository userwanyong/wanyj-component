package cn.wanyj.component.dcc.types.annotations;

import java.lang.annotation.*;

/**
 * @author 永
 * 自定义动态配置中心注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface DccConfig {
    String value() default "";
}
