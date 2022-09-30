package com.xkcoding.elasticsearch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Person
 *
 * @author fxbin
 * @version v1.0
 * @since 2019-09-15 23:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Serializable {

    private static final long serialVersionUID = 8510634155374943623L;

    /**
     * Primary key
     */
    private Long id;

    /**
     * First name
     */
    private String name;

    /**
     * Country
     */
    private String country;

    /**
     * Age
     */
    private Integer age;

    /**
     * Birthday
     */
    private Date birthday;

    /**
     * Introduction
     */
    private String remark;

}
