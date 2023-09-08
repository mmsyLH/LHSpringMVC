package asia.lhweb.lhspringmvc.servlet;

import asia.lhweb.lhspringmvc.context.LhWebApplicationContext;
import asia.lhweb.lhspringmvc.handler.LhHandler;
import asia.lhweb.lhspringmvc.servlet.annotation.Controller;
import asia.lhweb.lhspringmvc.servlet.annotation.RequestMapping;

import javax.servlet.ServletConfig;
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
    public void init(ServletConfig servletConfig) throws ServletException {
        // System.out.println("init方法启动");

        // 读取配置
        /*
            <init-param>
             <param-name>contextConfigLocation</param-name>
             <param-value>classpath:lhspringMVC.xml</param-value>
           </init-param>
         */
        String configLocation =
                servletConfig.getInitParameter("contextConfigLocation");//得到的是 classpath:lhspringMVC.xml
        lhWebApplicationContext = new LhWebApplicationContext(configLocation);
        lhWebApplicationContext.init();
        // 调用方法 完成url和控制器方法的映射
        initHandlerMapping();
        System.out.println("handlerList:" + handlers);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 调用方法 完成请求转发
        executeDispatch(req, resp);
    }

    /**
     * 初始处理程序映射
     */
    private void initHandlerMapping() {
        if (lhWebApplicationContext.ioc.isEmpty()) {
            // 判断当前的ioc容器中是否为空
            return;
        }
        // 不为空  遍历ioc容器的bean对象 然后进行url映射处理
        for (Map.Entry<String, Object> entry : lhWebApplicationContext.ioc.entrySet()) {
            // 先取出注入的Object的class对象
            Class<?> aClazz = entry.getValue().getClass();

            // 如果注入的bean 是controller 就取出他所有的方法
            if (aClazz.isAnnotationPresent(Controller.class)) {
                // 取出它的所有方法
                Method[] declaredMethods = aClazz.getDeclaredMethods();
                // 遍历方法
                for (Method declaredMethod : declaredMethods) {
                    // 判断该方法是否有requestMapping注解
                    if (declaredMethod.isAnnotationPresent(RequestMapping.class)) {
                        // 取出RequestMapping注解里的值-》就是映射路径
                        RequestMapping requestMapping = declaredMethod.getAnnotation(RequestMapping.class);
                        // 得到工程路径进行拼接
                        // String contextPath = getServletContext().getContextPath();
                        String url = requestMapping.value();
                        // String url = getServletContext().getContextPath() + requestMapping.value();// value就是url

                        // 创建一个LhHandler对象  这就是一个映射关系
                        LhHandler lhHandler = new LhHandler(url, entry.getValue(), declaredMethod);
                        handlers.add(lhHandler);

                    }
                }
            }
        }
    }


    /**
     * 编写方法，通过request对象，返回LhHandler对象 如果没有就返回null
     *
     * @param request 请求
     * @return {@link LhHandler}
     */
    private LhHandler getLhHandler(HttpServletRequest request) {
        // 1 先获取到用户请求的uri 比如http://localhost:8080/springmvc/student/list   uri就是/工程路径/student/list
        String requestURI = request.getRequestURI();
        // 遍历HandlerList
        for (LhHandler handler : handlers) {
            if (handler.getUrl().equals(requestURI)) {
                // 说明匹配成功
                return handler;
            }
        }
        // 2 没有找到这个uri
        return null;
    }

    /**
     * 分发请求的方案
     */
    private void executeDispatch(HttpServletRequest request, HttpServletResponse response) {
        LhHandler lhHandler = getLhHandler(request);
        try {
            if (lhHandler == null) {// 说明用户请求的资源不存在
                response.getWriter().write("<h1>404 not Found</h1>");

            } else {// 匹配成功 就反射调用handle里的那个控制器方法
                lhHandler.getMethod().invoke(lhHandler.getController(), request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
