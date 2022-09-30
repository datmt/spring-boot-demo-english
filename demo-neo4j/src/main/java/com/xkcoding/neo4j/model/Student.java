package com.xkcoding.neo4j.model;

import com.xkcoding.neo4j.config.CustomIdStrategy;
import com.xkcoding.neo4j.constants.NeoConsts;
import lombok.*;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

/**
 * <p>
 * Student node
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 14:38
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
@Builder
@NodeEntity
public class Student {
    /**
     * Primary key, custom primary key strategy, generated using UUID
     */
    @Id
    @GeneratedValue(strategy = CustomIdStrategy.class)
    private String id;

    /**
     * Student name
     */
    @NonNull
    private String name;

    /**
     * All courses selected by the student
     */
    @Relationship(NeoConsts.R_LESSON_OF_STUDENT)
    @NonNull
    private List<Lesson> lessons;

    /**
     * The student's class
     */
    @Relationship(NeoConsts.R_STUDENT_OF_CLASS)
    @NonNull
    private Class clazz;

}
