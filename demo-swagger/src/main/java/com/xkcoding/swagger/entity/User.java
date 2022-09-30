package com.xkcoding.swagger.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * User entity
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-29 11:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "用户实体", description = "User Entity")
public class User implements Serializable {
    private static final long serialVersionUID = 5057954049311281252L;
    /**
     * Primary key id
     */
    @ApiModelProperty(value = "主键id", required = true)
    private Integer id;
    /**
     * Username
     */
    @ApiModelProperty(value = "用户名", required = true)
    private String name;
    /**
     * Jobs
     */
    @ApiModelProperty(value = "工作岗位", required = true)
    private String job;
}
