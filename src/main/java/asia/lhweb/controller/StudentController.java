package asia.lhweb.controller;

import asia.lhweb.entity.Student;
import asia.lhweb.lhspringmvc.annotation.AutoWired;
import asia.lhweb.lhspringmvc.annotation.Controller;
import asia.lhweb.lhspringmvc.annotation.RequestMapping;
import asia.lhweb.lhspringmvc.annotation.ResponseBody;
import asia.lhweb.service.StudentService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 学生控制器
 *
 * @author 罗汉
 * @date 2023/09/06
 */
@Controller
public class StudentController {
    @AutoWired
    private StudentService studentService;

    @RequestMapping(value = "/student/list")
    public void listStudent(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");

        StringBuilder sb = new StringBuilder("<h1></h1>");
        List<Student> students = studentService.listMonster();
        sb.append("<table border='1px' width='px' style='border-collapse:collapse'>");
        for (Student student : students) {
            sb.append("<tr><td>" + student.getId() + "</td><td>" + student.getName() + "</td><td>" + student.getPwd() + "</td></tr>");
        }
        sb.append("</table>");
        try {
            response.getWriter().write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 增加方法，通过name返回对应的monster集合

    @RequestMapping(value = "/student/find")
    public void findMonsterByName(HttpServletRequest request, HttpServletResponse response, String name) {
        // 设置编码和返回类型
        response.setContentType("text/html;charset=utf-8");
        System.out.println("--接收到的name---" + name);
        StringBuilder content = new StringBuilder("<h1>学生列表信息</h1>");
        // 调用monsterService
        List<Student> students = studentService.findStudentByName(name);
        content.append("<table border='1px' width='400px' style='border-collapse:collapse'>");
        for (Student student : students) {
            content.append("<tr><td>" + student.getId() + "</td><td>" + student.getName() + "</td><td>" + student.getPwd() + "</td></tr>");
        }
        content.append("</table>");

        // 获取writer返回信息
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录
     *
     * @param sName 姓名
     * @return {@link String}
     */
    @RequestMapping(value = "/student/login")
    public String login(HttpServletRequest request, HttpServletResponse response, String sName) {
        System.out.println("login_name: " + sName);
        Boolean res = studentService.login(sName);
        // System.out.println("login方法："+res);
        request.setAttribute("sName", sName);
        if (res) {
            return "forward:/login_ok.jsp";
        } else {
            return "forward:/login_error.jsp";
        }
    }

    /**
     * 编写方法,返回json格式的数据
     * 1. 思路梳理
     * 2. 目标方法返回的结果是给springmvc底层通过反射调用的位置
     * 3. 我们在springmvc底层反射调用的位置，接收到结果并解析即可
     * 4. 方法上标注了 @ResponseBody 表示希望以json格式返回给客户端/浏览器
     * 5. 目标方法的实参，在springmvc底层通过封装好的参数数组，传入..
     *
     * @param request
     * @param response
     * @return
     */

    @RequestMapping("/student/list/json")
    @ResponseBody
    public List<Student> listStudentByJson(HttpServletRequest request, HttpServletResponse response) {

        List<Student> students = studentService.listStudent();
        return students;
    }
}
