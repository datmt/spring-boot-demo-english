package com.xkcoding.mongodb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * <p>
 * Article entity class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-28 16:21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    /**
     * Article id
     */
    @Id
    private Long id;

    /**
     * Article title
     */
    private String title;

    /**
     * Article content
     */
    private String content;

    /**
     * Creation time
     */
    private Date createTime;

    /**
     * Update time
     */
    private Date updateTime;

    /**
     * Number of likes
     */
    private Long thumbUp;

    /**
     * Number of visitors
     */
    private Long visits;

}
