package com.xkcoding.neo4j;

import cn.hutool.json.JSONUtil;
import com.xkcoding.neo4j.model.Lesson;
import com.xkcoding.neo4j.model.Student;
import com.xkcoding.neo4j.service.NeoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * Test Neo4j
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 15:17
 */
@Slf4j
public class Neo4jTest extends SpringBootDemoNeo4jApplicationTests {
    @Autowired
    private NeoService neoService;

    /**
     * Test save
     */
    @Test
    public void testSave() {
        neoService.initData();
    }

    /**
     * Test removal
     */
    @Test
    public void testDelete() {
        neoService.delete();
    }

    /**
     * Test to find out which courses Naruto has taken
     */
    @Test
    public void testFindLessonsByStudent() {
        If the depth is 1, the attribute of the teacher of the course is null
        A depth of 2 assigns a value to the instructor of the course
        List<Lesson> lessons = neoService.findLessonsFromStudent("漩涡鸣人", 2);

        lessons.forEach(lesson -> log.info("【lesson】= {}", JSONUtil.toJsonStr(lesson)));
    }

    /**
     * Test check class size
     */
    @Test
    public void testCountStudent() {
        Long all = neoService.studentCount(null);
        log.info("【全校人数】= {}", all);
        Long seven = neoService.studentCount("第七班");
        log.info("【第七班人数】= {}", seven);
    }

    /**
     * Test to query classmate relationships according to the course
     */
    @Test
    public void testFindClassmates() {
        Map<String, List<Student>> classmates = neoService.findClassmatesGroupByLesson();
        classmates.forEach((k, v) -> log.info("因为一起上了【{}】这门课，成为同学关系的有：{}", k, JSONUtil.toJsonStr(v.stream().map(Student::getName).collect(Collectors.toList()))));
    }

    /**
     * Enquire about all teacher-student relationships, including homeroom teacher/student, classroom teacher/student
     */
    @Test
    public void testFindTeacherStudent() {
        Map<String, Set<Student>> teacherStudent = neoService.findTeacherStudent();
        teacherStudent.forEach((k, v) -> log.info("【{}】教的学生有 {}", k, JSONUtil.toJsonStr(v.stream().map(Student::getName).collect(Collectors.toList()))));
    }
}
