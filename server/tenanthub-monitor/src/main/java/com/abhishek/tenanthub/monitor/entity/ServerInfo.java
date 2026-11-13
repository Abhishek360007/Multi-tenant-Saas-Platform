package com.abhishek.tenanthub.monitor.entity;

import cn.hutool.core.date.DatePattern;
import com.abhishek.tenanthub.common.util.ArithUtils;
import com.abhishek.tenanthub.common.util.IpUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/4 10:51
 */

@Getter
@Setter
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ServerInfo implements java.io.Serializable {

    private static final int OSHI_WAIT_SECOND = 1000;

    /**
     * CPU Related information
     */
    @Builder.Default()
    private Cpu cpu = new Cpu();

    /**
     * Memory details
     */
    @Builder.Default()
    private Mem mem = new Mem();

    /**
     * JVM Related information
     */
    @Builder.Default()
    private Jvm jvm = new Jvm();

    /**
     * Server details
     */
    @Builder.Default()
    private Sys sys = new Sys();

    /**
     * Disk details
     */
    @Builder.Default()
    private List<SysFile> sysFiles = new LinkedList<SysFile>();


    public void copyTo() throws Exception {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();

        setCpuInfo(hal.getProcessor());

        setMemInfo(hal.getMemory());

        setSysInfo();

        setJvmInfo();

        setSysFiles(si.getOperatingSystem());
    }

    /**
     * Set CPU details
     */
    private void setCpuInfo(CentralProcessor processor) {
        // CPU Info
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(OSHI_WAIT_SECOND);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softline = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long cSys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long ioWait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + ioWait + irq + softline + steal;
        cpu.setCpuNum(processor.getLogicalProcessorCount());
        cpu.setTotal(totalCpu);
        cpu.setSys(cSys);
        cpu.setUsed(user);
        cpu.setWait(ioWait);
        cpu.setFree(idle);
    }

    /**
     * Set memory details
     */
    private void setMemInfo(GlobalMemory memory) {
        mem.setTotal(memory.getTotal());
        mem.setUsed(memory.getTotal() - memory.getAvailable());
        mem.setFree(memory.getAvailable());
    }

    /**
     * Set server details
     */
    private void setSysInfo() {
        Properties props = System.getProperties();
        sys.setComputerName(IpUtils.getHostName());
        sys.setComputerIp(IpUtils.getHostIp());
        sys.setOsName(props.getProperty("os.name"));
        sys.setOsArch(props.getProperty("os.arch"));
        sys.setUserDir(props.getProperty("user.dir"));
    }

    /**
     * Set JVM details
     */
    private void setJvmInfo() {
        Properties props = System.getProperties();
        jvm.setTotal(Runtime.getRuntime().totalMemory());
        jvm.setMax(Runtime.getRuntime().maxMemory());
        jvm.setFree(Runtime.getRuntime().freeMemory());
        jvm.setVersion(props.getProperty("java.version"));
        jvm.setHome(props.getProperty("java.home"));
    }

    /**
     * Set disk details
     */
    private void setSysFiles(OperatingSystem os) {
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            long used = total - free;
            SysFile sysFile = new SysFile();
            sysFile.setDirName(fs.getMount());
            sysFile.setSysTypeName(fs.getType());
            sysFile.setTypeName(fs.getName());
            sysFile.setTotal(convertFileSize(total));
            sysFile.setFree(convertFileSize(free));
            sysFile.setUsed(convertFileSize(used));
            sysFile.setUsage(ArithUtils.mul(ArithUtils.div(used, total, 4), 100));
            sysFiles.add(sysFile);
        }
    }

    /**
     * Byte conversion
     *
     * @param size Size in bytes
     * @return Converted value
     */
    public String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }


    @Schema(description = "CPU Details")
    @Getter
    @Setter
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Cpu implements java.io.Serializable {
        /**
         * Cores
         */
        @Schema(description = "Cores")
        private int cpuNum;

        /**
         * CPU Total usage
         */
        @Schema(description = "CPU Total usage")
        private double total;

        /**
         * CPU System usage
         */
        @Schema(description = "CPU System usage")
        private double sys;

        /**
         * CPU User usage
         */
        @Schema(description = "CPU User usage")
        private double used;

        /**
         * CPU Current I/O wait
         */
        @Schema(description = "CPU Current I/O wait")
        private double wait;

        /**
         * CPU Current idle
         */
        @Schema(description = "CPU Current idle")
        private double free;


        public int getCpuNum() {
            return cpuNum;
        }

        public void setCpuNum(int cpuNum) {
            this.cpuNum = cpuNum;
        }

        public double getTotal() {
            return ArithUtils.round(ArithUtils.mul(total, 100), 2);
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public double getSys() {
            return ArithUtils.round(ArithUtils.mul(sys / total, 100), 2);
        }

        public void setSys(double sys) {
            this.sys = sys;
        }

        public double getUsed() {
            return ArithUtils.round(ArithUtils.mul(used / total, 100), 2);
        }

        public void setUsed(double used) {
            this.used = used;
        }

        public double getWait() {
            return ArithUtils.round(ArithUtils.mul(wait / total, 100), 2);
        }

        public void setWait(double wait) {
            this.wait = wait;
        }

        public double getFree() {
            return ArithUtils.round(ArithUtils.mul(free / total, 100), 2);
        }

        public void setFree(double free) {
            this.free = free;
        }
    }


    @Schema(description = "JVM Details")
    @Getter
    @Setter
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Jvm implements java.io.Serializable {
        /**
         * Total memory held by the JVM (M)
         */
        @Schema(description = "Total memory held by the JVM (M)")
        private double total;

        /**
         * JVM Maximum available memory (M)
         */
        @Schema(description = "JVM Maximum available memory (M)")
        private double max;

        /**
         * JVM Free memory (M)
         */
        @Schema(description = "JVM Free memory (M)")
        private double free;

        /**
         * JDK Version
         */
        @Getter
        @Schema(description = "JDK Version")
        private String version;

        /**
         * JDK Path
         */
        @Schema(description = "JDK Path")
        private String home;

        public double getTotal() {
            return ArithUtils.div(total, (1024 * 1024), 2);
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public double getMax() {
            return ArithUtils.div(max, (1024 * 1024), 2);
        }

        public void setMax(double max) {
            this.max = max;
        }

        public double getFree() {
            return ArithUtils.div(free, (1024 * 1024), 2);
        }

        public void setFree(double free) {
            this.free = free;
        }

        public double getUsed() {
            return ArithUtils.div(total - free, (1024 * 1024), 2);
        }

        public double getUsage() {
            return ArithUtils.mul(ArithUtils.div(total - free, total, 4), 100);
        }

        /**
         * Get the JDK name
         */
        public String getName() {
            return ManagementFactory.getRuntimeMXBean().getVmName();
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

        /**
         * JDK Started at
         */
        public String getStartTime() {
            long time = ManagementFactory.getRuntimeMXBean().getStartTime();
            return DatePattern.NORM_DATETIME_FORMATTER.format(Instant.ofEpochMilli(time));
        }

        /**
         * JDK Running time
         */
        public String getRunTime() {
            long time = ManagementFactory.getRuntimeMXBean().getStartTime();
            var start = Instant.ofEpochMilli(time);
            var end = Instant.now();
            var d = Duration.between(start, end);
            return String.format("%d  days %d hours %d minutes", d.toDays(), d.toHours(), d.toMinutes());
        }

        /**
         * Runtime arguments
         */
        public String getInputArgs() {
            return ManagementFactory.getRuntimeMXBean().getInputArguments().toString();
        }
    }


    @Schema(description = "Memory details")
    @Getter
    @Setter
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Mem implements java.io.Serializable {
        /**
         * Total memory
         */
        @Schema(description = "Total memory")
        private double total;

        /**
         * Used memory
         */
        @Schema(description = "Used memory")
        private double used;

        /**
         * Free memory
         */
        @Schema(description = "Free memory")
        private double free;

        public double getTotal() {
            return ArithUtils.div(total, (1024 * 1024 * 1024), 2);
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public double getUsed() {
            return ArithUtils.div(used, (1024 * 1024 * 1024), 2);
        }

        public void setUsed(long used) {
            this.used = used;
        }

        public double getFree() {
            return ArithUtils.div(free, (1024 * 1024 * 1024), 2);
        }

        public void setFree(long free) {
            this.free = free;
        }

        public double getUsage() {
            return ArithUtils.mul(ArithUtils.div(used, total, 4), 100);
        }
    }


    @Schema(description = "System details")
    @Getter
    @Setter
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sys implements java.io.Serializable {
        /**
         * Server name
         */
        @Schema(description = "Server name")
        private String computerName;

        /**
         * Server Ip
         */
        @Schema(description = "Server IP")
        private String computerIp;

        /**
         * Project path
         */
        @Schema(description = "Project path")
        private String userDir;

        /**
         * Operating system
         */
        @Schema(description = "Operating system")
        private String osName;

        /**
         * Architecture
         */
        @Schema(description = "Architecture")
        private String osArch;

    }


    @Schema(description = "File system details")
    @Getter
    @Setter
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SysFile implements java.io.Serializable {
        /**
         * Mount path
         */
        @Schema(description = "Mount path")
        private String dirName;

        /**
         * File system type
         */
        @Schema(description = "File system type")
        private String sysTypeName;

        /**
         * File type
         */
        @Schema(description = "File type")
        private String typeName;

        /**
         * Total size
         */
        @Schema(description = "Total size")
        private String total;

        /**
         * Free space
         */
        @Schema(description = "Free space")
        private String free;

        /**
         * Used
         */
        @Schema(description = "Used")
        private String used;

        /**
         * Resource usage
         */
        @Schema(description = "Resource usage")
        private double usage;
    }

}

