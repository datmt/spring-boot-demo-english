package com.xkcoding.multi.datasource.mybatis.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * User entity class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-21 14:19
 */
@Data
@TableName("multi_user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {
    private static final long serialVersionUID = -1923859222295750467L;

    /**
     * Primary key
     */
    @TableId(type = IdType.ID_WORKER)
    private Long id;

    /**
     * Name
     */
    private String name;

    /**
     * Age
     */
    private Integer age;
}
