package com.xkcoding.neo4j.model;

import com.xkcoding.neo4j.config.CustomIdStrategy;
import com.xkcoding.neo4j.constants.NeoConsts;
import lombok.*;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * <p>
 * Course nodes
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 14:55
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
@Builder
@NodeEntity
public class Lesson {
    /**
     * Primary key, custom primary key strategy, generated using UUID
     */
    @Id
    @GeneratedValue(strategy = CustomIdStrategy.class)
    private String id;

    /**
     * Course name
     */
    @NonNull
    private String name;

    /**
     * Teaching teacher
     */
    @Relationship(NeoConsts.R_TEACHER_OF_LESSON)
    @NonNull
    private Teacher teacher;
}
