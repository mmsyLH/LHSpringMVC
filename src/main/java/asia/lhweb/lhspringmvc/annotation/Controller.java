package asia.lhweb.lhspringmvc.annotation;

import java.lang.annotation.*;

/**
 * 该注解用于表示是一个控制器注解
 *
 * @author 罗汉
 * @date 2023/09/05
 */
@Target(ElementType.TYPE)//修饰类型
@Retention(RetentionPolicy.RUNTIME)//作业范围  设置炜runtime将来方便反射
@Documented
public @interface Controller {
    //为什么要加呢？ 因为默认是类名+小写
    String value() default "";
}
