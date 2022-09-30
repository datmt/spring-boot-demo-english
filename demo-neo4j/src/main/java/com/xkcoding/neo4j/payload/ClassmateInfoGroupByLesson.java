package com.xkcoding.neo4j.payload;

import com.xkcoding.neo4j.model.Student;
import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * <p>
 * Classmate relationships grouped by course
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 19:18
 */
@Data
@QueryResult
public class ClassmateInfoGroupByLesson {
    /**
     * Course name
     */
    private String lessonName;

    /**
     * Student information
     */
    private List<Student> students;
}
