package com.xkcoding.websocket.model.server;

import cn.hutool.core.util.NumberUtil;

/**
 * <p>
 * Stored related information entity
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-14 16:09
 */
public class Mem {
    /**
     * Total amount of memory
     */
    private double total;

    /**
     * Used memory
     */
    private double used;

    /**
     * Remaining memory
     */
    private double free;

    public double getTotal() {
        return NumberUtil.div(total, (1024 * 1024 * 1024), 2);
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public double getUsed() {
        return NumberUtil.div(used, (1024 * 1024 * 1024), 2);
    }

    public void setUsed(long used) {
        this.used = used;
    }

    public double getFree() {
        return NumberUtil.div(free, (1024 * 1024 * 1024), 2);
    }

    public void setFree(long free) {
        this.free = free;
    }

    public double getUsage() {
        return NumberUtil.mul(NumberUtil.div(used, total, 4), 100);
    }
}
