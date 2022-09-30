package com.xkcoding.websocket.model.server;

import cn.hutool.core.util.NumberUtil;

/**
 * <p>
 * CPU-related entities
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-14 16:09
 */
public class Cpu {
    /**
     * Number of cores
     */
    private int cpuNum;

    /**
     * Total CPU usage
     */
    private double total;

    /**
     * CPU system usage
     */
    private double sys;

    /**
     * CPU user usage
     */
    private double used;

    /**
     * CPU current wait rate
     */
    private double wait;

    /**
     * CPU current idle rate
     */
    private double free;

    public int getCpuNum() {
        return cpuNum;
    }

    public void setCpuNum(int cpuNum) {
        this.cpuNum = cpuNum;
    }

    public double getTotal() {
        return NumberUtil.round(NumberUtil.mul(total, 100), 2).doubleValue();
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getSys() {
        return NumberUtil.round(NumberUtil.mul(sys / total, 100), 2).doubleValue();
    }

    public void setSys(double sys) {
        this.sys = sys;
    }

    public double getUsed() {
        return NumberUtil.round(NumberUtil.mul(used / total, 100), 2).doubleValue();
    }

    public void setUsed(double used) {
        this.used = used;
    }

    public double getWait() {
        return NumberUtil.round(NumberUtil.mul(wait / total, 100), 2).doubleValue();
    }

    public void setWait(double wait) {
        this.wait = wait;
    }

    public double getFree() {
        return NumberUtil.round(NumberUtil.mul(free / total, 100), 2).doubleValue();
    }

    public void setFree(double free) {
        this.free = free;
    }
}
