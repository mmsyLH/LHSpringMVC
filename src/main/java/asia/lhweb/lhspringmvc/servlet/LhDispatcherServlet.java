package asia.lhweb.lhspringmvc.servlet;

import asia.lhweb.lhspringmvc.context.LhWebApplicationContext;
import asia.lhweb.lhspringmvc.handler.LhHandler;
import asia.lhweb.lhspringmvc.servlet.annotation.Controller;
import asia.lhweb.lhspringmvc.servlet.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 本质就是一个servlet
 * 前端的控制器，用于拦截全部的前端URL请求
 *
 * @author :罗汉
 * @date : 2023/9/5
 */
public class LhDispatcherServlet extends HttpServlet {
    // 保存LhHandler
    private List<LhHandler> handlers = new ArrayList<>();
    // 定义自己的spring容器
    private LhWebApplicationContext lhWebApplicationContext = null;

    /**
     * 当猫启动的时候
     * 初始化自己的spring容器
     *
     * @throws ServletException servlet异常
     */
    @Override
    public void init() throws ServletException {
        System.out.println("init方法启动");
        lhWebApplicationContext = new LhWebApplicationContext();
        lhWebApplicationContext.init();
        //调用方法 完成url和控制器方法的映射
        initHandlerMapping();
        System.out.println("handlerList:"+handlers);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("LhDispatcherServlet的doGet");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("LhDispatcherServlet的doPost");
    }

    /**
     * 初始处理程序映射
     */
    private void initHandlerMapping() {
        if (lhWebApplicationContext.ioc.isEmpty()){
            //判断当前的ioc容器中是否为空
            return;
        }
        //不为空  遍历ioc容器的bean对象 然后进行url映射处理
        for (Map.Entry <String,Object> entry:lhWebApplicationContext.ioc.entrySet()){
            //先取出注入的Object的class对象
            Class<?> aClazz = entry.getValue().getClass();

            //如果注入的bean 是controller 就取出他所有的方法
            if (aClazz.isAnnotationPresent(Controller.class)){
                //取出它的所有方法
                Method[] declaredMethods = aClazz.getDeclaredMethods();
                //遍历方法
                for (Method declaredMethod : declaredMethods) {
                    //判断该方法是否有requestMapping注解
                    if (declaredMethod.isAnnotationPresent(RequestMapping.class)){
                        //取出RequestMapping注解里的值-》就是映射路径
                        RequestMapping requestMapping = declaredMethod.getAnnotation(RequestMapping.class);
                        String url = requestMapping.value();//value就是url

                        //创建一个LhHandler对象  这就是一个映射关系
                        LhHandler lhHandler = new LhHandler(url,entry.getValue(),declaredMethod);
                        handlers.add(lhHandler);

                    }
                }
            }
        }
    }
}
