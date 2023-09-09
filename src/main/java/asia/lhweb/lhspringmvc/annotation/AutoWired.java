package asia.lhweb.lhspringmvc.annotation;

import java.lang.annotation.*;

/**
 * 自动装配注解
 * @author :罗汉
 * @date : 2023/9/5
 */
@Target(ElementType.FIELD)//修饰类型 直接标识在方法上
@Retention(RetentionPolicy.RUNTIME)//作业范围  设置炜runtime将来方便反射
@Documented
public @interface AutoWired {
    String value() default "";
}
