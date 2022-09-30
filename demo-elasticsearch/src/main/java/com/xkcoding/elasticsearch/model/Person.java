package com.xkcoding.elasticsearch.model;

import com.xkcoding.elasticsearch.constants.EsConsts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * <p>
 * User entity class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-20 17:29
 */
@Document(indexName = EsConsts.INDEX_NAME, type = EsConsts.TYPE_NAME, shards = 1, replicas = 0)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    /**
     * Primary key
     */
    @Id
    private Long id;

    /**
     * First name
     */
    @Field(type = FieldType.Keyword)
    private String name;

    /**
     * Country
     */
    @Field(type = FieldType.Keyword)
    private String country;

    /**
     * Age
     */
    @Field(type = FieldType.Integer)
    private Integer age;

    /**
     * Birthday
     */
    @Field(type = FieldType.Date)
    private Date birthday;

    /**
     * Introduction
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String remark;
}
