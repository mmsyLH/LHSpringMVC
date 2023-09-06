package asia.lhweb.lhspringmvc.servlet.annotation;

import java.lang.annotation.*;

/**
 * 用于指定控制器下面某个方法的映射路径
 * @author :罗汉
 * @date : 2023/9/5
 */
@Target(ElementType.METHOD)//修饰类型 直接标识在方法上
@Retention(RetentionPolicy.RUNTIME)//作业范围  设置炜runtime将来方便反射
@Documented
public @interface RequestMapping {
    String value() default "";
}
