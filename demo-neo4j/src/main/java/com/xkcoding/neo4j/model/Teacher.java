package com.xkcoding.neo4j.model;

import com.xkcoding.neo4j.config.CustomIdStrategy;
import lombok.*;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * <p>
 * Teacher node
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 14:54
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
@Builder
@NodeEntity
public class Teacher {
    /**
     * Primary key, custom primary key strategy, generated using UUID
     */
    @Id
    @GeneratedValue(strategy = CustomIdStrategy.class)
    private String id;

    /**
     * Teacher's name
     */
    @NonNull
    private String name;
}
