package com.xkcoding.websocket.model.server;

/**
 * <p>
 * System-related information entities
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-14 16:10
 */
public class Sys {
    /**
     * Server name
     */
    private String computerName;

    /**
     * Server Ip
     */
    private String computerIp;

    /**
     * Project path
     */
    private String userDir;

    /**
     * Operating system
     */
    private String osName;

    /**
     * System architecture
     */
    private String osArch;

    public String getComputerName() {
        return computerName;
    }

    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    public String getComputerIp() {
        return computerIp;
    }

    public void setComputerIp(String computerIp) {
        this.computerIp = computerIp;
    }

    public String getUserDir() {
        return userDir;
    }

    public void setUserDir(String userDir) {
        this.userDir = userDir;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }
}
