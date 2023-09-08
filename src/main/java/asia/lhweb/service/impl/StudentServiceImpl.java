package asia.lhweb.service.impl;

import asia.lhweb.entity.Student;
import asia.lhweb.lhspringmvc.servlet.annotation.Service;
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
}
