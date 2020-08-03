package com.fit2cloud.mc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.Module;
import com.fit2cloud.commons.server.constants.F2CMetricName;
import com.fit2cloud.commons.server.constants.MetricSource;
import com.fit2cloud.commons.server.constants.ModuleConstants;
import com.fit2cloud.commons.server.model.MetricData;
import com.fit2cloud.commons.server.model.MetricDataRequest;
import com.fit2cloud.commons.server.model.MetricRequest;
import com.fit2cloud.commons.server.service.MetricQueryService;
import com.fit2cloud.commons.server.service.ModuleService;
import com.fit2cloud.commons.server.service.pushgateway.CloudResourceMetricPusher;
import com.fit2cloud.commons.server.service.pushgateway.domain.MetricEntity;
import com.fit2cloud.commons.server.utils.MetricUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.common.constants.McSysStatsConstants;
import com.fit2cloud.mc.dao.McSysStatsMapper;
import com.fit2cloud.mc.dto.McSysStatsDTO;
import com.fit2cloud.mc.model.McSysStats;
import com.fit2cloud.mc.model.McSysStatsExample;
import io.prometheus.client.CollectorRegistry;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SysStatsService {

    private final static String sysStatsCpuInfo = "/host-root/proc/cpuinfo";
    private final static String sysStatsCpuStat = "/host-root/proc/stat";
    private final static String sysStatsMem = "/host-root/proc/meminfo";
    private final static String sysStatsIp = "/host-root/proc/net/fib_trie";

    @Resource
    private McSysStatsMapper mcSysStatsMapper;

    @Resource
    private ModuleService moduleService;

    @Resource
    private CloudResourceMetricPusher cloudResourceMetricPusher;

    @Resource
    private MetricQueryService metricQueryService;

    public void SyncStatsHostInfoJob(String hostname) {

        try {
            Map<String, Object> map = new HashMap<>();
            //获取CPU 核数
            map.put("core", grep("processor", sysStatsCpuInfo).size());

            //获取MEM
            long memTotal = Long.parseLong(grep("MemTotal", sysStatsMem).get(0).replaceAll("[^0-9]", ""));
            int mem = Integer.parseInt(new BigDecimal(memTotal * 1.0 / 1024 / 1024).setScale(0, BigDecimal.ROUND_HALF_UP).toString());

            map.put("mem", mem);
            //获取IP
            List<String> ips = getIps();
            map.put("ip", JSON.toJSON(ips));

            //DISK
            Double disk = getDiskSize();
            map.put("disk", disk);
            //模块状态

            map.put("modules", moduleService.syncModuleStatus());

            McSysStatsExample mcSysStatsExample = new McSysStatsExample();
            mcSysStatsExample.createCriteria().andStatKeyEqualTo(hostname);
            mcSysStatsMapper.deleteByExample(mcSysStatsExample);
            McSysStats mcSysStats = new McSysStats();
            mcSysStats.setId(UUIDUtil.newUUID());
            mcSysStats.setStatKey(hostname);
            mcSysStats.setStats(JSON.toJSONString(map));
            mcSysStats.setUpdateTime(System.currentTimeMillis());
            mcSysStatsMapper.insert(mcSysStats);
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
    }


    private List<String> getIps() {
        List<String> ips = new ArrayList<>();
        try {
            String preString = "|--";
            String regEx = "127|\\.0[^0-9.]*$|\\.255[^0-9.]*$";
            List<String> lines = FileUtils.readLines(new File(sysStatsIp));
            Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
            for (String line : lines) {
                if (line.contains(preString)) {
                    Matcher matcher = pattern.matcher(line);
                    if (!matcher.find()) {
                        String ip = line.replace(preString, "").trim();
                        ips.add(ip);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.error("get IP error，" + e.getMessage());
        }

        return ips;
    }

    private Double getDiskSize() throws IOException {
        List<String> lines = getDiskLines();
        double sizeCount = getDisk(lines, 0);
        return Math.ceil(sizeCount);
    }

    private double getDisk(List<String> lines, int index) {
        double sizeCount = 0.0;
        String rex = "\\S[0-9][^ ].+";
        Pattern pattern = Pattern.compile(rex, Pattern.CASE_INSENSITIVE);
        List<String> dirs = Arrays.asList("/", "/host-root/home", "/host-root/boot");
        for (String line : lines) {
            String[] split = line.split("%");
            if (split.length == 1 || dirs.contains(split[1].trim())) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String sizes = matcher.group().replaceAll("\\s+", " ");
                    String[] strs = sizes.split(" ");
                    if (strs.length > 1) {
                        sizeCount += Double.valueOf(strs[index].trim()) / 1024 / 1024;
                    }
                }
            }
        }
        return sizeCount;
    }

    private List<String> getDiskLines() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process exec = runtime.exec("df");
        return IOUtils.readLines(exec.getInputStream());
    }


    public void SyncStatsHostMetricJob(String hostname) {
        long l = System.currentTimeMillis();
        CollectorRegistry registry = new CollectorRegistry();
        cloudResourceMetricPusher.registry(cpuUsage(hostname, l), registry);
        cloudResourceMetricPusher.registry(memUsage(hostname, l), registry);
        cloudResourceMetricPusher.registry(diskUsage(hostname, l), registry);
        try {
            cloudResourceMetricPusher.push(hostname, registry);
        } catch (IOException e) {
            LogUtil.error(e.getMessage());
        }

    }

    /**
     * 磁盘使用率
     *
     * @param hostname
     * @param l
     * @return
     */
    public MetricEntity diskUsage(String hostname, long l) {
        MetricEntity metricEntity = new MetricEntity();
        metricEntity.setResourceId(hostname);
        metricEntity.getLabelMap().put("metricKey", F2CMetricName.SYS_DISK_USAGE.getName());
        metricEntity.setMetricName(F2CMetricName.SYS_DISK_USAGE.getName());
        metricEntity.getLabelMap().putAll(MetricUtils.getSyncDate(l));
        try {
            List<String> lines = getDiskLines();
            double sizeCount = getDisk(lines, 0);
            double sizeUse = getDisk(lines, 1);
            String format = String.format("%.2f", sizeUse / sizeCount * 100);
            metricEntity.setValue(Double.valueOf(format));
        } catch (Exception e) {
            LogUtil.error("Error getting disk usage：" + e.getMessage());
            metricEntity.setResourceId(null);
        }
        return metricEntity;
    }


    /**
     * 内存使用率
     *
     * @param hostname
     * @param l
     * @return
     */
    private MetricEntity memUsage(String hostname, long l) {

        MetricEntity metricEntity = new MetricEntity();
        metricEntity.setResourceId(hostname);
        metricEntity.getLabelMap().put("metricKey", F2CMetricName.SYS_MEMORY_USAGE.getName());
        metricEntity.setMetricName(F2CMetricName.SYS_MEMORY_USAGE.getName());
        metricEntity.getLabelMap().putAll(MetricUtils.getSyncDate(l));
        try {
            long memTotal = Long.parseLong(grep("MemTotal", sysStatsMem).get(0).replaceAll("[^0-9]", ""));
            long memFree = Long.parseLong(grep("MemFree", sysStatsMem).get(0).replaceAll("[^0-9]", ""));
            long buffers = Long.parseLong(grep("Buffers", sysStatsMem).get(0).replaceAll("[^0-9]", ""));
            long cached = Long.parseLong(grep("Cached", sysStatsMem).get(0).replaceAll("[^0-9]", ""));
            double usage = (memTotal - memFree - buffers - cached) * 1.0 / memTotal * 100;
            metricEntity.setValue(new BigDecimal(usage).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } catch (Exception e) {
            LogUtil.error("Error getting MEM usage：" + e.getMessage());
            metricEntity.setResourceId(null);
        }
        return metricEntity;
    }


    /**
     * 获取CPU使用率
     *
     * @param hostname
     * @param l
     * @return
     */
    private MetricEntity cpuUsage(String hostname, long l) {
        MetricEntity metricEntity = new MetricEntity();
        metricEntity.setResourceId(hostname);
        metricEntity.getLabelMap().put("metricKey", F2CMetricName.SYS_CPU_USAGE.getName());
        metricEntity.getLabelMap().putAll(MetricUtils.getSyncDate(l));
        metricEntity.setMetricName(F2CMetricName.SYS_CPU_USAGE.getName());
        try {
            Map<String, Long> cpuUsageMap1 = cpuUsageInfo();
            Thread.sleep(5 * 1000);
            Map<String, Long> cpuUsageMap2 = cpuUsageInfo();
            double usage = 100 - (cpuUsageMap2.get("free") - cpuUsageMap1.get("free")) * 1.0 / (cpuUsageMap2.get("total") - cpuUsageMap1.get("total")) * 100;
            metricEntity.setValue(new BigDecimal(usage).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        } catch (Exception e) {
            LogUtil.error("Error getting CPU usage：" + e.getMessage());
            metricEntity.setResourceId(null);
        }
        return metricEntity;
    }

    private Map<String, Long> cpuUsageInfo() throws IOException {
        Map<String, Long> map = new HashMap<>();
        List<String> lines = FileUtils.readLines(new File(sysStatsCpuStat));
        StringTokenizer tokenizer = new StringTokenizer(lines.get(0));
        long usage = 0, total = 0, index = 0;
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            if ("cpu".equals(token)) continue;
            long l = Long.parseLong(token);
            if (index == 3) {
                usage += l;
            }
            total += l;
            index++;
        }
        map.put("free", usage);
        map.put("total", total);
        return map;
    }

    private static List<String> grep(String grep, String path) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(grep);
        try {
            List<String> lines = FileUtils.readLines(new File(path));
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    result.add(line);
                }
            }
            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }


    public List<McSysStatsDTO> getAllMcSysStats() {
        List<McSysStatsDTO> result = new ArrayList<>();
        List<McSysStats> statsList = mcSysStatsMapper.selectByExampleWithBLOBs(null);
        for (McSysStats mcSysStats : statsList) {
            McSysStatsDTO dto = new McSysStatsDTO();
            BeanUtils.copyBean(dto, mcSysStats);
            if (System.currentTimeMillis() - mcSysStats.getUpdateTime() < 120000) {
                dto.setStatus(McSysStatsConstants.running.name());
                result.add(dto);
            } else if (System.currentTimeMillis() - mcSysStats.getUpdateTime() < 600000) {
                dto.setStatus(McSysStatsConstants.stopped.name());
                result.add(dto);
            }

        }

        result.sort(Comparator.comparing(McSysStats::getStatKey));
        return result;
    }

    public List<MetricData> getSysMetric(String resourceId) {
        MetricRequest metricRequest = new MetricRequest();
        long end = System.currentTimeMillis();
        metricRequest.setStartTime(end - 60 * 60 * 1000);
        metricRequest.setEndTime(end);
        metricRequest.getMetricDataQueries().add(new MetricDataRequest() {{
            setResourceId(resourceId);
            setMetric(F2CMetricName.SYS_CPU_USAGE);
            setMetricSource(MetricSource.API);
        }});
        metricRequest.getMetricDataQueries().add(new MetricDataRequest() {{
            setResourceId(resourceId);
            setMetric(F2CMetricName.SYS_MEMORY_USAGE);
            setMetricSource(MetricSource.API);
        }});
        metricRequest.getMetricDataQueries().add(new MetricDataRequest() {{
            setResourceId(resourceId);
            setMetric(F2CMetricName.SYS_DISK_USAGE);
            setMetricSource(MetricSource.API);
        }});
        return metricQueryService.queryMetricData(metricRequest);
    }


    public List<Module> getModuleList() {
        List<Module> moduleList = moduleService.getModuleList();
        if (!CollectionUtils.isEmpty(moduleList)) {
            convertModuleStatus(moduleList);
        }
        return moduleList;
    }

    private void convertModuleStatus(List<Module> moduleList) {
        Map<String, List<String>> map = new HashMap<>();
        moduleList.stream()
                .filter(module -> !ModuleConstants.Type.link.name().equals(module.getType()))
                .forEach(module -> map.put(module.getId(), new ArrayList<>()));

        List<McSysStatsDTO> statsDTOList = getAllMcSysStats();
        for (McSysStatsDTO dto : statsDTOList) {
            JSONObject jsonObject = JSON.parseObject(dto.getStats());
            if (McSysStatsConstants.stopped.name().equals(dto.getStatus())) {
                for (String key : map.keySet()) {
                    map.get(key).add(ModuleConstants.RunningStatus.stopped.name());
                }
            } else {
                List<Module> modules = JSON.parseArray(jsonObject.getString("modules"), Module.class);
                for (String key : map.keySet()) {
                    String status = containsModuleId(key, modules);
                    map.get(key).add(StringUtils.isBlank(status) ? ModuleConstants.RunningStatus.stopped.name() : status);
                }
            }
        }

        moduleList.stream()
                .filter(module -> !ModuleConstants.Type.link.name().equals(module.getType()))
                .forEach(module -> {
                    List<String> statusList = map.get(module.getId());
                    if (statusList.contains(ModuleConstants.RunningStatus.stopped.name()) &&
                            statusList.contains(ModuleConstants.RunningStatus.running.name())) {
                        module.setStatus(ModuleConstants.RunningStatus.section_running.name());
                    } else if (statusList.contains(ModuleConstants.RunningStatus.stopped.name())) {
                        module.setStatus(ModuleConstants.RunningStatus.stopped.name());
                    } else {
                        module.setStatus(ModuleConstants.RunningStatus.running.name());
                    }
                });


    }

    private String containsModuleId(String moduleId, List<Module> modules) {

        if (!CollectionUtils.isEmpty(modules)) {
            for (Module module : modules) {
                if (module.getId().equals(moduleId)) {
                    return module.getStatus();
                }
            }
        }
        return null;
    }
}
