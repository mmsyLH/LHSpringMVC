package asia.lhweb.lhspringmvc.servlet;

import asia.lhweb.lhspringmvc.context.LhWebApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 本质就是一个servlet
 * 前端的控制器，用于拦截全部的前端URL请求
 * @author :罗汉
 * @date : 2023/9/5
 */
public class LhDispatcherServlet extends HttpServlet {

    /**
     * 当猫启动的时候
     * 初始化自己的spring容器
     *
     * @throws ServletException servlet异常
     */
    @Override
    public void init() throws ServletException {
        System.out.println("init方法启动");
            LhWebApplicationContext lhWebApplicationContext = new LhWebApplicationContext();
            lhWebApplicationContext.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("LhDispatcherServlet的doGet");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("LhDispatcherServlet的doPost");
    }
}
