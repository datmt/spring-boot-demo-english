package com.xkcoding.dynamic.datasource.controller;

import com.xkcoding.dynamic.datasource.annotation.DefaultDatasource;
import com.xkcoding.dynamic.datasource.datasource.DatasourceConfigCache;
import com.xkcoding.dynamic.datasource.mapper.DatasourceConfigMapper;
import com.xkcoding.dynamic.datasource.model.DatasourceConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * Data source configuration Controller
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 17:31
 */
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DatasourceConfigController {
    private final DatasourceConfigMapper configMapper;

    /**
     * Save
     */
    @PostMapping("/config")
    @DefaultDatasource
    public DatasourceConfig insertConfig(@RequestBody DatasourceConfig config) {
        configMapper.insertUseGeneratedKeys(config);
        DatasourceConfigCache.INSTANCE.addConfig(config.getId(), config);
        return config;
    }

    /**
     * Save
     */
    @DeleteMapping("/config/{id}")
    @DefaultDatasource
    public void removeConfig(@PathVariable Long id) {
        configMapper.deleteByPrimaryKey(id);
        DatasourceConfigCache.INSTANCE.removeConfig(id);
    }
}
