package com.xkcoding.codegen.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import com.xkcoding.codegen.common.PageResult;
import com.xkcoding.codegen.entity.GenConfig;
import com.xkcoding.codegen.entity.TableRequest;
import com.xkcoding.codegen.service.CodeGenService;
import com.xkcoding.codegen.utils.CodeGenUtil;
import com.xkcoding.codegen.utils.DbUtil;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 * Code generator
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-03-22 10:15
 */
@Service
@AllArgsConstructor
public class CodeGenServiceImpl implements CodeGenService {
    private final String TABLE_SQL_TEMPLATE = "select table_name tableName, engine, table_comment tableComment, create_time createTime from information_schema.tables where table_schema = (select database()) %s order by create_time desc";

    private final String COLUMN_SQL_TEMPLATE = "select column_name columnName, data_type dataType, column_comment columnComment, column_key columnKey, extra from information_schema.columns where table_name = ? and table_schema = (select database()) order by ordinal_position";

    private final String COUNT_SQL_TEMPLATE = "select count(1) from (%s)tmp";

    private final String PAGE_SQL_TEMPLATE = " limit ?,?";

    /**
     * Paginated query table information
     *
     * @param request request parameters
     * @return Table name paging information
     */
    @Override
    @SneakyThrows
    public PageResult<Entity> listTables(TableRequest request) {
        HikariDataSource dataSource = DbUtil.buildFromTableRequest(request);
        Db db = new Db(dataSource);

        Page page = new Page(request.getCurrentPage(), request.getPageSize());
        int start = page.getStartPosition();
        int pageSize = page.getPageSize();

        String paramSql = StrUtil.EMPTY;
        if (StrUtil.isNotBlank(request.getTableName())) {
            paramSql = "and table_name like concat('%', ?, '%')";
        }
        String sql = String.format(TABLE_SQL_TEMPLATE, paramSql);
        String countSql = String.format(COUNT_SQL_TEMPLATE, sql);

        List<Entity> query;
        BigDecimal count;
        if (StrUtil.isNotBlank(request.getTableName())) {
            query = db.query(sql + PAGE_SQL_TEMPLATE, request.getTableName(), start, pageSize);
            count = (BigDecimal) db.queryNumber(countSql, request.getTableName());
        } else {
            query = db.query(sql + PAGE_SQL_TEMPLATE, start, pageSize);
            count = (BigDecimal) db.queryNumber(countSql);
        }

        PageResult<Entity> pageResult = new PageResult<>(count.longValue(), page.getPageNumber(), page.getPageSize(), query);

        dataSource.close();
        return pageResult;
    }

    /**
     * Generate code
     *
     * @param genConfig build configuration
     * @return Code compression file
     */
    @Override
    public byte[] generatorCode(GenConfig genConfig) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        Query table information
        Entity table = queryTable(genConfig.getRequest());
        Query column information
        List<Entity> columns = queryColumns(genConfig.getRequest());
        Generate the code
        CodeGenUtil.generatorCode(genConfig, table, columns, zip);
        IoUtil.close(zip);
        return outputStream.toByteArray();
    }

    @SneakyThrows
    private Entity queryTable(TableRequest request) {
        HikariDataSource dataSource = DbUtil.buildFromTableRequest(request);
        Db db = new Db(dataSource);

        String paramSql = StrUtil.EMPTY;
        if (StrUtil.isNotBlank(request.getTableName())) {
            paramSql = "and table_name = ?";
        }
        String sql = String.format(TABLE_SQL_TEMPLATE, paramSql);
        Entity entity = db.queryOne(sql, request.getTableName());

        dataSource.close();
        return entity;
    }

    @SneakyThrows
    private List<Entity> queryColumns(TableRequest request) {
        HikariDataSource dataSource = DbUtil.buildFromTableRequest(request);
        Db db = new Db(dataSource);

        List<Entity> query = db.query(COLUMN_SQL_TEMPLATE, request.getTableName());

        dataSource.close();
        return query;
    }

}
