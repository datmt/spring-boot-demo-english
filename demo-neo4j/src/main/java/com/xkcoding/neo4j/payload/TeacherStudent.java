package com.xkcoding.neo4j.payload;

import com.xkcoding.neo4j.model.Student;
import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * <p>
 * Teacher-student relationship
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 19:18
 */
@Data
@QueryResult
public class TeacherStudent {
    /**
     * Teacher's name
     */
    private String teacherName;

    /**
     * Student information
     */
    private List<Student> students;
}
