package org.f14a.tachibana;

import org.f14a.fatin2.event.command.GroupCommandEvent;
import org.f14a.fatin2.event.command.OnCommand;
import org.f14a.fatin2.event.session.Coroutines;
import org.f14a.fatin2.api.MessageGenerator;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.util.Set;

public class EventListener {
    private static final Set<Long> infoAllowedGroups = Set.of(893213294L);

    @Coroutines
    @OnCommand(command = "info", description = "显示系统信息", usage = "info")
    public void onInfo(GroupCommandEvent event) throws InterruptedException {
        if (!infoAllowedGroups.contains(event.getGroupId())) {
            return;
        }
        StringBuilder sb = new StringBuilder();

        // OS and JVM
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();

        sb.append("操作系统: ")
                .append(os).append(", ")
                .append(os.getFamily()).append("\n");

        sb.append("Java: ")
                .append(System.getProperty("java.version")).append(", ")
                .append(System.getProperty("java.vendor")).append(", ")
                .append(System.getProperty("java.vm.name")).append("\n");
        sb.append("\n");

        // CPU
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor cpu = hal.getProcessor();
        int logicalCores = cpu.getLogicalProcessorCount();
        sb.append("CPU 核心数: ").append(logicalCores).append("\n");

        // CPU usage: need get data twice
        long[] prevTicks = cpu.getSystemCpuLoadTicks();
        Thread.sleep(1000);
        double cpuUsage = cpu.getSystemCpuLoadBetweenTicks(prevTicks);

        sb.append("CPU 负载: ").append(String.format(
                "%.2f%%", cpuUsage * 100
        )).append("\n");

        // Memory
        GlobalMemory mem = hal.getMemory();
        long totalRam = mem.getTotal();
        long availRam = mem.getAvailable();
        long usedRam = totalRam - availRam;

        sb.append("内存使用: ").append("\n");
        sb.append("Physical: ").append(String.format(
                "%d MiB / %d MiB, %.2f%% used.",
                bytesToMiB(usedRam),
                bytesToMiB(totalRam),
                ratio(usedRam, totalRam) * 100
        )).append("\n");

        long totalSwap = mem.getVirtualMemory().getSwapTotal();
        long usedSwap = mem.getVirtualMemory().getSwapUsed();

        sb.append("Swap: ").append(String.format(
                "%d MiB / %d MiB, %.2f%% used.",
                bytesToMiB(usedSwap),
                bytesToMiB(totalSwap),
                ratio(usedSwap, totalSwap) * 100
        )).append("\n");

        event.send(MessageGenerator.text(sb.toString()));
    }

    private long bytesToMiB(long bytes) {
        return bytes / 1024 / 1024;
    }
    private double ratio(long used, long total) {
        if (total <= 0) {
            return 0.;
        }
        return (double) used / total;
    }
}
