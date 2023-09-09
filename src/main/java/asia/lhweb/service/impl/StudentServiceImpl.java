package asia.lhweb.service.impl;

import asia.lhweb.entity.Student;
import asia.lhweb.lhspringmvc.annotation.Service;
import asia.lhweb.service.StudentService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :罗汉
 * @date : 2023/9/6
 */
@Service
public class StudentServiceImpl implements StudentService {
    @Override
    public List<Student> listMonster() {
        //这里就模拟数据->DB
        List<Student> monsters =
                new ArrayList<>();
        monsters.add(new Student(100, "牛魔王", "123"));
        monsters.add(new Student(200, "老猫妖怪", "123456"));
        return monsters;
    }

    @Override
    public List<Student> findStudentByName(String name) {
        List<Student> students =
                new ArrayList<>();
        students.add(new Student(100, "牛魔王", " 400"));
        students.add(new Student(200, "老猫妖怪", "200"));
        students.add(new Student(300, "大象精", "100"));
        students.add(new Student(400, "黄袍怪", "300"));
        students.add(new Student(500, "白骨精", "800"));


        //创建集合返回查询到的monster集合

        List<Student> findStudents =
                new ArrayList<>();
        //遍历monsters,返回满足条件
        for (Student student : students) {
            if (student.getName().contains(name)) {
                findStudents.add(student);
            }
        }
        return findStudents;
    }
}
