package com.xkcoding.orm.mybatis.mapper;

import com.xkcoding.orm.mybatis.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * User Mapper
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 10:54
 */
@Mapper
@Component
public interface UserMapper {

    /**
     * Query all users
     *
     * @return User list
     */
    @Select("SELECT * FROM orm_user")
    List<User> selectAllUser();

    /**
     * Query users based on id
     *
     * @param id primary key id
     * @return user with current id, if not present, {@code null}
     */
    @Select("SELECT * FROM orm_user WHERE id = #{id}")
    User selectUserById(@Param("id") Long id);

    /**
     * Save users
     *
     * @param user user
     * @return Success - {@code 1} Failed - {@code 0}
     */
    int saveUser(@Param("user") User user);

    /**
     * Delete users
     *
     * @param id primary key id
     * @return Success - {@code 1} Failed - {@code 0}
     */
    int deleteById(@Param("id") Long id);

}
