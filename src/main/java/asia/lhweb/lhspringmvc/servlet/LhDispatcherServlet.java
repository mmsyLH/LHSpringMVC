package asia.lhweb.lhspringmvc.servlet;

import asia.lhweb.lhspringmvc.annotation.RequestParam;
import asia.lhweb.lhspringmvc.context.LhWebApplicationContext;
import asia.lhweb.lhspringmvc.handler.LhHandler;
import asia.lhweb.lhspringmvc.annotation.Controller;
import asia.lhweb.lhspringmvc.annotation.RequestMapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
                servletConfig.getInitParameter("contextConfigLocation");// 得到的是 classpath:lhspringMVC.xml
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

                // 目标将: HttpServletRequest 和 HttpServletResponse封装到参数数组
                // 1. 得到目标方法的所有形参参数信息[对应的数组]
                Class<?>[] parameterTypes =
                        lhHandler.getMethod().getParameterTypes();

                // 2. 创建一个参数数组[对应实参数组], 在后面反射调用目标方法时，会使用到
                Object[] params =
                        new Object[parameterTypes.length];

                // 3遍历parameterTypes形参数组,根据形参数组信息，将实参填充到实参数组

                for (int i = 0; i < parameterTypes.length; i++) {
                    // 取出每一个形参类型
                    Class<?> parameterType = parameterTypes[i];
                    // 如果这个形参是HttpServletRequest, 将request填充到params
                    // 在原生SpringMVC中,是按照类型来进行匹配，这里简化使用名字来进行匹配
                    if ("HttpServletRequest".equals(parameterType.getSimpleName())) {
                        params[i] = request;
                    } else if ("HttpServletResponse".equals(parameterType.getSimpleName())) {
                        params[i] = response;
                    }
                }

                // 将http请求参数封装到params数组中, 提示，要注意填充实参的时候，顺序问题

                // 1. 获取http请求的参数集合
                // 处理提交的数据中文乱码
                request.setCharacterEncoding("utf-8");
                Map<String, String[]> parameterMap = request.getParameterMap();
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    String name = entry.getKey();// 指的是请求中的参数名
                    String value = entry.getValue()[0];// 这里只是实现简单的一个参数名对应一个参数
                    // 获得参数是在方法总的第几个位置
                    int indexReqParameter = getIndexReqParameter(lhHandler.getMethod(), name);
                    if (indexReqParameter != -1) {// 说明找到了对应的位置
                        params[indexReqParameter] = value;

                    } else {// 没有找到@RequestParameter注解对应的参数,就会使用默认的机制进行匹配
                        // 1 先拿到目标方法所有形参名称 不是形参的类型！！！！！！！！！！ 如name request job这样的

                        // 2 对得到的目标方法的所有形参名进行遍历，如果匹配就把当前请求的参数值填充到我们的实参数组中
                        List<String> nameReqParameters = getNameReqParameter(lhHandler.getMethod());
                        for (int i = 0; i < nameReqParameters.size(); i++) {
                            // 如果Url中的实参名和形参名一样
                            if (nameReqParameters.get(i).equals(name)) {// 匹配成功
                                params[i] =value;//填充到实参数组
                                break;
                            }
                        }

                    }
                }

                // 方法反射
                Object res = lhHandler.getMethod().invoke(lhHandler.getController(), params);
                if(res instanceof String){
                    String viewName = (String)res;
                    if (viewName.contains(":")){//说明你返回的String 结果可能是forword:/login_ok.jsp 或 redirect:/xxx/xx/xx.xx
                        String viewType = viewName.split(":")[0];
                        String viewPage = viewName.split(":")[1];
                        //判断是forward 还是 redirect
                        if ("forward".equals(viewType)){//说明希望是请求转发
                            request.getRequestDispatcher(viewPage).forward(request, response);
                        }else if ("redirect".equals(viewType)){
                            response.sendRedirect(viewPage);
                        }
                    }

                }//
                //对返回结果进行解析

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取请求参数在目标方法中的索引
     *
     * @param method 目标方法
     * @param name   请求的名称  指的是url中 参数的name
     * @return int
     */
    private int getIndexReqParameter(Method method, String name) {
        // 1 得到目标方法中的全部请求参数
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            // 取出当前的形参参数
            Parameter parameter = parameters[i];
            // 判断是否存在这个注解
            boolean annotationPresent = parameter.isAnnotationPresent(RequestParam.class);
            if (annotationPresent) {// 存在这个注解
                // 取出当前这个参数的 @RequestParam(value = "xxx")
                RequestParam requestParamAnnotation = parameter.getAnnotation(RequestParam.class);
                String value = requestParamAnnotation.value();
                // 这里就是匹配的比较
                if (name.equals(value)) {
                    return i;// 找到请求的参数，对应的目标方法的形参的位置
                }
            }
        }
        // 如果没有匹配成功，就返回-1
        return -1;
    }

    /**
     * 获取目标方法中的形参名称数组
     *
     * @param method 目标方法
     * @return {@link List}<{@link String}>返回全部的形参名称
     */
    private List<String> getNameReqParameter(Method method) {
        ArrayList<String> parameterList = new ArrayList<>();

        // 获取到所有的参数名  有一个小细节
        /**
         * 在默认的情况下
         * parameter.getName()得到的名字不是形参真正的名字！！！！！！！！！！
         * 而是[arg0,arg1,arg2,arg3...]
         *  这里我们要引入一个插件，使用java8特性，这样才能解决
         */
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            String name = parameter.getName();
            parameterList.add(name);
        }
        System.out.println("目标方法的形参列表=" + parameterList);
        return parameterList;
    }
}
