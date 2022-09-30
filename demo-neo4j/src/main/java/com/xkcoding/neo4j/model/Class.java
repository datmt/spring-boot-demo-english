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
 * Class node
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 14:44
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
@Builder
@NodeEntity
public class Class {
    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = CustomIdStrategy.class)
    private String id;

    /**
     * Class name
     */
    @NonNull
    private String name;

    /**
     * Class teacher
     */
    @Relationship(NeoConsts.R_BOSS_OF_CLASS)
    @NonNull
    private Teacher boss;
}
