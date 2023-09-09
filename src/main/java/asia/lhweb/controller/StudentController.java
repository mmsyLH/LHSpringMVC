package asia.lhweb.controller;

import asia.lhweb.entity.Student;
import asia.lhweb.lhspringmvc.annotation.AutoWired;
import asia.lhweb.lhspringmvc.annotation.Controller;
import asia.lhweb.lhspringmvc.annotation.RequestMapping;
import asia.lhweb.service.StudentService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
}
