package com.xkcoding.neo4j.repository;

import com.xkcoding.neo4j.model.Student;
import com.xkcoding.neo4j.payload.ClassmateInfoGroupByLesson;
import com.xkcoding.neo4j.payload.TeacherStudent;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Student node Repository
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 15:05
 */
public interface StudentRepository extends Neo4jRepository<Student, String> {
    /**
     * Find students by name
     *
     * @param name name
     * @param depth depth
     * @return Student information
     */
    Optional<Student> findByName(String name, @Depth int depth);

    /**
     * Check class size by class
     *
     * @param className class name
     * @return Class size
     */
    @Query("MATCH (s:Student)-[r:R_STUDENT_OF_CLASS]->(c:Class{name:{className}}) return count(s)")
    Long countByClassName(@Param("className") String className);


    /**
     * Enquire about students who meet the (student)-[course selection relationship]-(course)-[course selection relationship]-(student) relationship
     *
     * @return Return to classmate relationships
     */
    @Query("match (s:Student)-[:R_LESSON_OF_STUDENT]->(l:Lesson)<-[:R_LESSON_OF_STUDENT]-(:Student) with l.name as lessonName,collect(distinct s) as students return lessonName,students")
    List<ClassmateInfoGroupByLesson> findByClassmateGroupByLesson();

    /**
     * Enquire about teacher-student relationship, (student)-[class-student relationship]-(class)-[class teacher relationship]-(teacher)
     *
     * @return Return to teacher-student relationship
     */
    @Query("match (s:Student)-[:R_STUDENT_OF_CLASS]->(:Class)-[:R_BOSS_OF_CLASS]->(t:Teacher) with t.name as teacherName,collect(distinct s) as students return teacherName,students")
    List<TeacherStudent> findTeacherStudentByClass();

    /**
     * Enquire about teacher-student relationship, (student)-[course selection relationship]-(curriculum)-[teacher-teacher relationship]-(teacher)
     *
     * @return Return to teacher-student relationship
     */
    @Query("match ((s:Student)-[:R_LESSON_OF_STUDENT]->(:Lesson)-[:R_TEACHER_OF_LESSON]->(t:Teacher))with t.name as teacherName,collect(distinct s) as students return teacherName,students")
    List<TeacherStudent> findTeacherStudentByLesson();
}
