package com.xkcoding.dynamic.datasource.config;

import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * <p>
 * Generic mapper custom mapper file
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 16:23
 */
@RegisterMapper
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
