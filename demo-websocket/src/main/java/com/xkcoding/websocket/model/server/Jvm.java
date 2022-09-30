package com.xkcoding.websocket.model.server;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;

import java.lang.management.ManagementFactory;
import java.util.Date;

/**
 * <p>
 * JVM-related entities
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-14 16:09
 */
public class Jvm {
    /**
     * Total memory currently occupied by the JVM (M)
     */
    private double total;

    /**
     * Maximum total available memory in the JVM (M)
     */
    private double max;

    /**
     * JVM free memory (M)
     */
    private double free;

    /**
     * JDK version
     */
    private String version;

    /**
     * JDK path
     */
    private String home;

    /**
     * JDK startup time
     */
    private String startTime;

    /**
     * JDK running time
     */
    private String runTime;

    public double getTotal() {
        return NumberUtil.div(total, (1024 * 1024), 2);
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getMax() {
        return NumberUtil.div(max, (1024 * 1024), 2);
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getFree() {
        return NumberUtil.div(free, (1024 * 1024), 2);
    }

    public void setFree(double free) {
        this.free = free;
    }

    public double getUsed() {
        return NumberUtil.div(total - free, (1024 * 1024), 2);
    }

    public double getUsage() {
        return NumberUtil.mul(NumberUtil.div(total - free, total, 4), 100);
    }

    /**
     * Get the JDK name
     */
    public String getName() {
        return ManagementFactory.getRuntimeMXBean().getVmName();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime() {
        return DateUtil.formatDateTime(new Date(ManagementFactory.getRuntimeMXBean().getStartTime()));
    }


    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public String getRunTime() {
        long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        return DateUtil.formatBetween(DateUtil.current(false) - startTime);
    }
}
