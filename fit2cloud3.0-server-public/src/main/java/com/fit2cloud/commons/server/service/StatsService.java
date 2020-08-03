package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.Module;
import com.fit2cloud.commons.server.constants.ModuleConstants;
import com.fit2cloud.commons.utils.CommonThreadPool;
import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class StatsService {

    @Resource
    private ModuleService moduleService;
    @Resource
    private DiscoveryClient discoveryClient;
    @Resource
    private CommonThreadPool commonThreadPool;

    @Resource
    private RestTemplate restTemplate;

    public List<Map<String, String>> healthCheck(HttpServletRequest request, HttpServletResponse response) {

        List<Map<String, String>> mapList = new ArrayList<>();
        List<Module> moduleList = moduleService.getSystemEnableModuleList();
        List<String> runningServices = discoveryClient.getServices();

        for (Module module : moduleList) {
            Map<String, String> map = new HashMap<>();
            if (runningServices.contains(module.getId())) {
                map.put(module.getId(), ModuleConstants.RunningStatus.running.name());
            } else {
                map.put(module.getId(), ModuleConstants.RunningStatus.stopped.name());
                if (StringUtils.isNotBlank(request.getParameter("requiredStatus"))) {
                    try {
                        response.setStatus(Integer.valueOf(request.getParameter("requiredStatus")));
                    } catch (Exception e) {
                        response.setStatus(503);
                    }
                } else {
                    response.setStatus(503);
                }
            }

            mapList.add(map);
        }
        return mapList;
    }

    //called from management-center
    public void validateModuleConnection(String serviceName, String server, int port) {
        commonThreadPool.scheduleTask(() -> {
            try {
                String url = "http://" + server + ":" + port + "/anonymous/web-public/login";
                restTemplate.getForEntity(url, String.class);
                url = "http://" + server + ":" + port + "/anonymous/status?requiredStatus=200";
                restTemplate.getForEntity(url, String.class);
                LogUtil.debug("Service registration verify: " + serviceName);
            } catch (Exception e) {
                LogUtil.debug("Service registration verify error: " + serviceName + ", " + LogUtil.getExceptionDetailsToStr(e));
            }
        }, 10, TimeUnit.SECONDS);
    }
}
