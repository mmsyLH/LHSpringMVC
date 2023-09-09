package asia.lhweb.service;

import asia.lhweb.entity.Student;

import java.util.List;

/**
 * @author :罗汉
 * @date : 2023/9/6
 */
public interface StudentService {
    List<Student> listMonster();

    List<Student> findStudentByName(String name);

    Boolean login(String sName);

    List<Student> listStudent();
}
