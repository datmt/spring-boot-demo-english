package com.xkcoding.orm.jdbctemplate.dao.base;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.xkcoding.orm.jdbctemplate.annotation.Column;
import com.xkcoding.orm.jdbctemplate.annotation.Ignore;
import com.xkcoding.orm.jdbctemplate.annotation.Pk;
import com.xkcoding.orm.jdbctemplate.annotation.Table;
import com.xkcoding.orm.jdbctemplate.constant.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * Dao base class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-15 11:28
 */
@Slf4j
public class BaseDao<T, P> {
    private JdbcTemplate jdbcTemplate;
    private Class<T> clazz;

    @SuppressWarnings(value = "unchecked")
    public BaseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Generic insertion, self-incrementing column requires the addition of {@link Pk} annotations
     *
     * @param t object
     * @param ignoreNull ignoreNull ignores null values
     * @return number of rows of the operation
     */
    protected Integer insert(T t, Boolean ignoreNull) {
        String table = getTableName(t);

        List<Field> filterField = getField(t, ignoreNull);

        List<String> columnList = getColumns(filterField);

        String columns = StrUtil.join(Const.SEPARATOR_COMMA, columnList);

        Constructs a placeholder
        String params = StrUtil.repeatAndJoin("?", columnList.size(), Const.SEPARATOR_COMMA);

        Construct the value
        Object[] values = filterField.stream().map(field -> ReflectUtil.getFieldValue(t, field)).toArray();

        String sql = StrUtil.format("INSERT INTO {table} ({columns}) VALUES ({params})", Dict.create().set("table", table).set("columns", columns).set("params", params));
        log.debug("【执行SQL】SQL：{}", sql);
        log.debug("【执行SQL】参数：{}", JSONUtil.toJsonStr(values));
        return jdbcTemplate.update(sql, values);
    }

    /**
     * Universal is deleted based on primary key
     *
     * @param pk primary key
     * @return Affects the number of rows
     */
    protected Integer deleteById(P pk) {
        String tableName = getTableName();
        String sql = StrUtil.format("DELETE FROM {table} where id = ?", Dict.create().set("table", tableName));
        log.debug("【执行SQL】SQL：{}", sql);
        log.debug("【执行SQL】参数：{}", JSONUtil.toJsonStr(pk));
        return jdbcTemplate.update(sql, pk);
    }

    /**
     * Generally updated according to the primary key, self-incremented columns need to add {@link Pk} annotations
     *
     * @param t object
     * @param pk primary key
     * @param ignoreNull ignoreNull ignores null values
     * @return number of rows of the operation
     */
    protected Integer updateById(T t, P pk, Boolean ignoreNull) {
        String tableName = getTableName(t);

        List<Field> filterField = getField(t, ignoreNull);

        List<String> columnList = getColumns(filterField);

        List<String> columns = columnList.stream().map(s -> StrUtil.appendIfMissing(s, " = ?")).collect(Collectors.toList());
        String params = StrUtil.join(Const.SEPARATOR_COMMA, columns);

        Construct the value
        List<Object> valueList = filterField.stream().map(field -> ReflectUtil.getFieldValue(t, field)).collect(Collectors.toList());
        valueList.add(pk);

        Object[] values = ArrayUtil.toArray(valueList, Object.class);

        String sql = StrUtil.format("UPDATE {table} SET {params} where id = ?", Dict.create().set("table", tableName).set("params", params));
        log.debug("【执行SQL】SQL：{}", sql);
        log.debug("【执行SQL】参数：{}", JSONUtil.toJsonStr(values));
        return jdbcTemplate.update(sql, values);
    }

    /**
     * Universal query for single records based on primary key
     *
     * @param pk primary key
     * @return Single record
     */
    public T findOneById(P pk) {
        String tableName = getTableName();
        String sql = StrUtil.format("SELECT * FROM {table} where id = ?", Dict.create().set("table", tableName));
        RowMapper<T> rowMapper = new BeanPropertyRowMapper<>(clazz);
        log.debug("【执行SQL】SQL：{}", sql);
        log.debug("【执行SQL】参数：{}", JSONUtil.toJsonStr(pk));
        return jdbcTemplate.queryForObject(sql, new Object[]{pk}, rowMapper);
    }

    /**
     * Query based on object
     *
     * @param t query criteria
     * @return Object list
     */
    public List<T> findByExample(T t) {
        String tableName = getTableName(t);
        List<Field> filterField = getField(t, true);
        List<String> columnList = getColumns(filterField);

        List<String> columns = columnList.stream().map(s -> " and " + s + " = ? ").collect(Collectors.toList());

        String where = StrUtil.join(" ", columns);
        Construct the value
        Object[] values = filterField.stream().map(field -> ReflectUtil.getFieldValue(t, field)).toArray();

        String sql = StrUtil.format("SELECT * FROM {table} where 1=1 {where}", Dict.create().set("table", tableName).set("where", StrUtil.isBlank(where) ? "" : where));
        log.debug("【执行SQL】SQL：{}", sql);
        log.debug("【执行SQL】参数：{}", JSONUtil.toJsonStr(values));
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, values);
        List<T> ret = CollUtil.newArrayList();
        maps.forEach(map -> ret.add(BeanUtil.fillBeanWithMap(map, ReflectUtil.newInstance(clazz), true, false)));
        return ret;
    }

    /**
     * Get the table name
     *
     * @param t object
     * @return Table name
     */
    private String getTableName(T t) {
        Table tableAnnotation = t.getClass().getAnnotation(Table.class);
        if (ObjectUtil.isNotNull(tableAnnotation)) {
            return StrUtil.format("`{}`", tableAnnotation.name());
        } else {
            return StrUtil.format("`{}`", t.getClass().getName().toLowerCase());
        }
    }

    /**
     * Get the table name
     *
     * @return Table name
     */
    private String getTableName() {
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        if (ObjectUtil.isNotNull(tableAnnotation)) {
            return StrUtil.format("`{}`", tableAnnotation.name());
        } else {
            return StrUtil.format("`{}`", clazz.getName().toLowerCase());
        }
    }

    /**
     * Get columns
     *
     * @param fieldList field list
     * @return column information list
     */
    private List<String> getColumns(List<Field> fieldList) {
        Construct the column
        List<String> columnList = CollUtil.newArrayList();
        for (Field field : fieldList) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            String columnName;
            if (ObjectUtil.isNotNull(columnAnnotation)) {
                columnName = columnAnnotation.name();
            } else {
                columnName = field.getName();
            }
            columnList.add(StrUtil.format("`{}`", columnName));
        }
        return columnList;
    }

    /**
     * Get a list of fields {@code filter fields that do not exist in the database, as well as self-incrementing columns}
     *
     * @param t object
     * @param ignoreNull ignoreNull ignores null values
     * @return Field list
     */
    private List<Field> getField(T t, Boolean ignoreNull) {
        Gets all fields, including the fields in the parent class
        Field[] fields = ReflectUtil.getFields(t.getClass());

        Filter fields that do not exist in the database, as well as self-incrementing columns
        List<Field> filterField;
        Stream<Field> fieldStream = CollUtil.toList(fields).stream().filter(field -> ObjectUtil.isNull(field.getAnnotation(Ignore.class)) || ObjectUtil.isNull(field.getAnnotation(Pk.class)));

        Whether to filter fields with a null field value
        if (ignoreNull) {
            filterField = fieldStream.filter(field -> ObjectUtil.isNotNull(ReflectUtil.getFieldValue(t, field))).collect(Collectors.toList());
        } else {
            filterField = fieldStream.collect(Collectors.toList());
        }
        return filterField;
    }

}
