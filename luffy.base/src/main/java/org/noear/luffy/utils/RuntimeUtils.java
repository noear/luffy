package org.noear.luffy.utils;

import java.lang.management.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RuntimeUtils {
    private static RuntimeStatus status = new RuntimeStatus();


    public static RuntimeStatus getStatus() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        Runtime runtime = Runtime.getRuntime();

        status.memoryTotal = (byteToM(runtime.totalMemory()));
        status.memoryUsed = (byteToM(runtime.totalMemory() - runtime.freeMemory()));


        List<Map<String,Object>> memoryPools = new ArrayList<>();

        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        if(pools != null && !pools.isEmpty()) {
            for (final MemoryPoolMXBean pool : pools) {
                MemoryUsage usage = pool.getUsage();
                Map<String, Object> map = new LinkedHashMap<>();

                map.put("name", pool.getName());
                map.put("max", byteToM(usage.getMax()));
                map.put("init", byteToM(usage.getInit()));
                map.put("used", byteToM(usage.getUsed()));

                memoryPools.add(map);
            }
        }

        status.memoryPools = memoryPools;


        status.timeElapsed = (runtimeMXBean.getUptime());

        status.threadCount = (threadMXBean.getThreadCount());
        status.threadPeakCount = (threadMXBean.getPeakThreadCount());
        status.threadDaemonCount = (threadMXBean.getDaemonThreadCount());

        if(status.pid == null) {
            //这些只需要取一次
            //
            status.memoryMax = (byteToM(runtime.maxMemory()));

            status.timeStart = new Datetime(runtimeMXBean.getStartTime()).toString();

            status.pid = runtimeMXBean.getName();
            status.os = (System.getProperty("os.name"));
            status.vm = (System.getProperty("java.vm.vendor")
                    + " / " + System.getProperty("java.vm.name")
                    + " / " + System.getProperty("java.vm.version"));
        }


        return status;
    }

    public static long byteToM(long bytes) {
        long kb = (bytes / 1024 / 1024);
        return kb;
    }
}
