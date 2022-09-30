package com.xkcoding.codegen.service;

import cn.hutool.db.Entity;
import com.xkcoding.codegen.common.PageResult;
import com.xkcoding.codegen.entity.GenConfig;
import com.xkcoding.codegen.entity.TableRequest;

/**
 * <p>
 * Code generator
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-03-22 10:15
 */
public interface CodeGenService {
    /**
     * Generate code
     *
     * @param genConfig build configuration
     * @return Code compression file
     */
    byte[] generatorCode(GenConfig genConfig);

    /**
     * Paginated query table information
     *
     * @param request request parameters
     * @return Table name paging information
     */
    PageResult<Entity> listTables(TableRequest request);
}
