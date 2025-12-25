package org.f14a.tachibana;

import org.f14a.fatin2.event.command.GroupCommandEvent;
import org.f14a.fatin2.event.command.OnCommand;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Set;

public class EventListener {
    private static final Set<Long> infoAllowedGroups = Set.of(893213294L);

    @OnCommand(command = "info", description = "显示系统信息", usage = "info")
    public void onInfo(GroupCommandEvent event) {
        if (!infoAllowedGroups.contains(event.getGroupId())) {
            return;
        }
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        StringBuilder sb = new StringBuilder();
        sb.append("操作系统: ")
                .append(System.getProperty("os.name")).append(" ")
                .append(System.getProperty("os.version")).append(" ")
                .append("os.arch").append("\n");

        sb.append("Java: ")
                .append(System.getProperty("java.version")).append(" ")
                .append(System.getProperty("java.vendor")).append(" ")
                .append(System.getProperty("java.vm.name")).append("\n");


        sb.append("CPU 核心数: ").append(os.getAvailableProcessors()).append("\n");

        sb.append("CPU 负载: ").append(String.format(
                "%.2f%%", os.getSystemLoadAverage() * 100 / os.getAvailableProcessors()
        )).append("\n");

        sb.append("内存使用: ").append("\n");
        sb.append("Physical: ").append(String.format(
                "%d MiB / %d MiB used.",
                bytesToMiB(Runtime.getRuntime().freeMemory()),
                bytesToMiB(Runtime.getRuntime().totalMemory())
        )).append("\n");
        sb.append("Swap: ").append(String.format(
                "%d MiB / %d MiB used.",
                bytesToMiB(Runtime.getRuntime().freeMemory()),
                bytesToMiB(Runtime.getRuntime().totalMemory())
        )).append("\n");

    }

    private long bytesToMiB(long bytes) {
        return bytes / 1024 / 1024;
    }
}
