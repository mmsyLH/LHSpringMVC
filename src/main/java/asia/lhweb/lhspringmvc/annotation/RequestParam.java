package asia.lhweb.lhspringmvc.annotation;

import java.lang.annotation.*;

/**
 * 请求参数注解
 * @author :罗汉
 * @date : 2023/9/5
 */
@Target(ElementType.METHOD)//修饰类型 直接标识在方法上
@Retention(RetentionPolicy.RUNTIME)//作业范围  设置炜runtime将来方便反射
@Documented
public @interface RequestParam {
    String value() default "";
}
