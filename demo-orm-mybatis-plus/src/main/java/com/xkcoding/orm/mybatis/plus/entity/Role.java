package com.xkcoding.orm.mybatis.plus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * Role entity class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-14 14:04
 */
@Data
@TableName("orm_role")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Role extends Model<Role> {
    /**
     * Primary key
     */
    private Long id;

    /**
     * Role name
     */
    private String name;

    /**
     * Primary key value, ActiveRecord mode this must have, otherwise xxById method will be invalidated!
     * Even if RoleMapper is not used with ActiveRecord, the RoleMapper interface must be created
     */
    @Override
    protected Serializable pkVal() {

        return this.id;
    }
}
