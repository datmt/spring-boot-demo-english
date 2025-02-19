package com.xkcoding.neo4j.config;

import cn.hutool.core.util.IdUtil;
import org.neo4j.ogm.id.IdStrategy;

/**
 * <p>
 * Custom primary key policy
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 14:40
 */
public class CustomIdStrategy implements IdStrategy {
    @Override
    public Object generateId(Object o) {
        return IdUtil.fastUUID();
    }
}
