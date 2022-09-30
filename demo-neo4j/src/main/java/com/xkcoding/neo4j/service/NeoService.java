package com.xkcoding.neo4j.service;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.xkcoding.neo4j.model.Class;
import com.xkcoding.neo4j.model.Lesson;
import com.xkcoding.neo4j.model.Student;
import com.xkcoding.neo4j.model.Teacher;
import com.xkcoding.neo4j.payload.ClassmateInfoGroupByLesson;
import com.xkcoding.neo4j.payload.TeacherStudent;
import com.xkcoding.neo4j.repository.ClassRepository;
import com.xkcoding.neo4j.repository.LessonRepository;
import com.xkcoding.neo4j.repository.StudentRepository;
import com.xkcoding.neo4j.repository.TeacherRepository;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * NeoService
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 15:19
 */
@Service
public class NeoService {
    @Autowired
    private ClassRepository classRepo;

    @Autowired
    private LessonRepository lessonRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private TeacherRepository teacherRepo;

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Initialize data
     */
    @Transactional
    public void initData() {
        Initialize the teacher
        Teacher akai = Teacher.of("迈特凯");
        Teacher kakaxi = Teacher.of("旗木卡卡西");
        Teacher zilaiye = Teacher.of("自来也");
        Teacher gangshou = Teacher.of("纲手");
        Teacher dashewan = Teacher.of("大蛇丸");
        teacherRepo.save(akai);
        teacherRepo.save(kakaxi);
        teacherRepo.save(zilaiye);
        teacherRepo.save(gangshou);
        teacherRepo.save(dashewan);

        Initialize the course
        Lesson tishu = Lesson.of("体术", akai);
        Lesson huanshu = Lesson.of("幻术", kakaxi);
        Lesson shoulijian = Lesson.of("手里剑", kakaxi);
        Lesson luoxuanwan = Lesson.of("螺旋丸", zilaiye);
        Lesson xianshu = Lesson.of("仙术", zilaiye);
        Lesson yiliao = Lesson.of("医疗", gangshou);
        Lesson zhouyin = Lesson.of("咒印", dashewan);
        lessonRepo.save(tishu);
        lessonRepo.save(huanshu);
        lessonRepo.save(shoulijian);
        lessonRepo.save(luoxuanwan);
        lessonRepo.save(xianshu);
        lessonRepo.save(yiliao);
        lessonRepo.save(zhouyin);

        Initialize the class
        Class three = Class.of("第三班", akai);
        Class seven = Class.of("第七班", kakaxi);
        classRepo.save(three);
        classRepo.save(seven);

        Initialize students
        List<Student> threeClass = Lists.newArrayList(Student.of("漩涡鸣人", Lists.newArrayList(tishu, shoulijian, luoxuanwan, xianshu), seven), Student.of("宇智波佐助", Lists.newArrayList(huanshu, zhouyin, shoulijian), seven), Student.of("春野樱", Lists.newArrayList(tishu, yiliao, shoulijian), seven));
        List<Student> sevenClass = Lists.newArrayList(Student.of("李洛克", Lists.newArrayList(tishu), three), Student.of("日向宁次", Lists.newArrayList(tishu), three), Student.of("天天", Lists.newArrayList(tishu), three));

        studentRepo.saveAll(threeClass);
        studentRepo.saveAll(sevenClass);

    }

    /**
     * Delete data
     */
    @Transactional
    public void delete() {
        Use the statement to delete
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.query("match (n)-[r]-() delete n,r", Maps.newHashMap());
        session.query("match (n)-[r]-() delete r", Maps.newHashMap());
        session.query("match (n) delete n", Maps.newHashMap());
        transaction.commit();

        Use repository to delete
        studentRepo.deleteAll();
        classRepo.deleteAll();
        lessonRepo.deleteAll();
        teacherRepo.deleteAll();
    }

    /**
     * Query the selected course based on the student's name
     *
     * @param studentName Student Name
     * @param depth depth
     * @return Course List
     */
    public List<Lesson> findLessonsFromStudent(String studentName, int depth) {
        List<Lesson> lessons = Lists.newArrayList();
        studentRepo.findByName(studentName, depth).ifPresent(student -> lessons.addAll(student.getLessons()));
        return lessons;
    }

    /**
     * Check the number of students in the whole school
     *
     * @return Total number of students
     */
    public Long studentCount(String className) {
        if (StrUtil.isBlank(className)) {
            return studentRepo.count();
        } else {
            return studentRepo.countByClassName(className);
        }
    }

    /**
     * Enquire about classmate relationships, according to course
     *
     * @return Return to classmate relationships
     */
    public Map<String, List<Student>> findClassmatesGroupByLesson() {
        List<ClassmateInfoGroupByLesson> groupByLesson = studentRepo.findByClassmateGroupByLesson();
        Map<String, List<Student>> result = Maps.newHashMap();

        groupByLesson.forEach(classmateInfoGroupByLesson -> result.put(classmateInfoGroupByLesson.getLessonName(), classmateInfoGroupByLesson.getStudents()));

        return result;
    }

    /**
     * Enquire about all teacher-student relationships, including homeroom teacher/student, classroom teacher/student
     *
     * @return Teacher-student relationship
     */
    public Map<String, Set<Student>> findTeacherStudent() {
        List<TeacherStudent> teacherStudentByClass = studentRepo.findTeacherStudentByClass();
        List<TeacherStudent> teacherStudentByLesson = studentRepo.findTeacherStudentByLesson();
        Map<String, Set<Student>> result = Maps.newHashMap();

        teacherStudentByClass.forEach(teacherStudent -> result.put(teacherStudent.getTeacherName(), Sets.newHashSet(teacherStudent.getStudents())));

        teacherStudentByLesson.forEach(teacherStudent -> result.put(teacherStudent.getTeacherName(), Sets.newHashSet(teacherStudent.getStudents())));

        return result;
    }
}
