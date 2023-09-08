package asia.lhweb.lhspringmvc.servlet.annotation;

import java.lang.annotation.*;

/**
 * 该注解用于表示是一个service
 *
 * @author 罗汉
 * @date 2023/09/05
 */
@Target(ElementType.TYPE)//修饰类
@Retention(RetentionPolicy.RUNTIME)//作业范围  设置runtime将来方便反射
@Documented//生成文档的时候也能体现出来
public @interface Service {
    //为什么要加呢？ 因为默认是类名+小写
    String value() default "";
}
