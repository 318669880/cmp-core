package com.fit2cloud.mc.job;

import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.dao.McSysStatsMapper;
import com.fit2cloud.mc.model.McSysStats;
import com.fit2cloud.mc.model.McSysStatsExample;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SyncEurekaServer implements ApplicationRunner {

    private static final String KUBERNETES_ENVIRONMENT = "KUBERNETES_PORT";

    private static final String DEFAULT_ZONE_PROPERTIES = "eureka.client.service-url.defaultZone";

    private static final String MEMBER_PREFIX = "eureka.server.";

    private static String _localIp = StringUtils.EMPTY;

    public static boolean IS_KUBERNETES = false;

    @Resource
    private Environment environment;

    @Resource
    private McSysStatsMapper mcSysStatsMapper;

    @Resource
    private EurekaClientConfigBean eurekaClientConfigBean;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        init();
    }

    @Scheduled(fixedDelay = 15000)
    public void syncEurekaServer() {
        if (!IS_KUBERNETES) {
            return;
        }
        String localIp = registerSelf();
        List<String> members = getMembers();
        if (StringUtils.isNotBlank(localIp)) {
            members.remove(localIp);
        }
        List<String> serverUrls = convert2ARecordUrl(members);
        if (LogUtil.getLogger().isTraceEnabled()) {
            LogUtil.getLogger().trace("serverUrls: " + serverUrls);
        }
        eurekaClientConfigBean.getServiceUrl().put(EurekaClientConfigBean.DEFAULT_ZONE, StringUtils.join(serverUrls, ","));
        if (LogUtil.getLogger().isTraceEnabled()) {
            LogUtil.getLogger().trace("getServiceUrl: " + eurekaClientConfigBean.getServiceUrl());
        }
    }

    public void init() {
        if (!isKubernetes()) {
            LogUtil.info("Not Kubernetes Deployment.");
            return;
        }
        LogUtil.info("syncEurekaServer start.");
        String localIp = registerSelf();
        LogUtil.info("Self registered: " + localIp);
    }

    private String registerSelf() {
        String localIp = getLocalIp();
        if (StringUtils.isBlank(localIp)) {
            return localIp;
        }
        String primaryKey = MEMBER_PREFIX + localIp;
        if (mcSysStatsMapper.selectByPrimaryKey(primaryKey) != null) {
            return localIp;
        }
        McSysStats mcSysStats = new McSysStats();
        mcSysStats.setId(primaryKey);
        mcSysStats.setStatKey(localIp);
        mcSysStats.setUpdateTime(0L);
        mcSysStats.setStats(JSON.toJSONString(mcSysStats));
        mcSysStatsMapper.insert(mcSysStats);
        return localIp;
    }

    private List<String> convert2ARecordUrl(List<String> members) {
        String defaultUrl = environment.getProperty(DEFAULT_ZONE_PROPERTIES);
        List<String> result = new ArrayList<>();
        try {
            String host = new URL(defaultUrl).getHost();
            members.forEach(ip -> result.add(StringUtils.replaceFirst(defaultUrl, host, ip)));
        } catch (Exception e) {
            // do nothing
        }
        if (CollectionUtils.isEmpty(result)) {
            result.add(defaultUrl);
        }
        return result;
    }

    private List<String> getMembers() {
        List<String> result = new ArrayList<>();
        try {
            McSysStatsExample mcSysStatsExample = new McSysStatsExample();
            mcSysStatsExample.createCriteria().andIdLike(MEMBER_PREFIX + "%");
            List<McSysStats> mcSysStatsList = mcSysStatsMapper.selectByExample(mcSysStatsExample);
            mcSysStatsList.forEach(mcSysStats -> {
                String memberIp = mcSysStats.getStatKey();
                try {
                    if (InetAddress.getByName(memberIp).isReachable(3000)) {
                        result.add(memberIp);
                    } else {
                        mcSysStatsMapper.deleteByPrimaryKey(MEMBER_PREFIX + memberIp);
                    }
                } catch (Exception ignore) {
                }
            });
        } catch (Exception ignore) {
        }
        return result;
    }

    private boolean isKubernetes() {
        Map<String, String> map = System.getenv();
        LogUtil.debug("环境变量: " + map);
        IS_KUBERNETES = map.keySet().stream().anyMatch(key -> StringUtils.equals(key, KUBERNETES_ENVIRONMENT));
        return IS_KUBERNETES;
    }


    private String getLocalIp() {
        if (StringUtils.isNotBlank(_localIp)) {
            return _localIp;
        }
        try {
            InetAddress address = InetAddress.getLocalHost();
            _localIp = address.getHostAddress();
        } catch (Exception e) {
            LogUtil.error("failed to getLocalIp", e);
        }
        return _localIp;
    }


}
