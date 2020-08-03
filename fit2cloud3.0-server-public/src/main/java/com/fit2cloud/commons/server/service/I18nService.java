package com.fit2cloud.commons.server.service;


import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.server.base.domain.I18nExample;
import com.fit2cloud.commons.server.base.domain.I18nWithBLOBs;
import com.fit2cloud.commons.server.base.mapper.I18nMapper;
import com.fit2cloud.commons.server.i18n.I18nManager;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import com.fit2cloud.commons.utils.LogUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.alibaba.fastjson.JSON.parseObject;

@Service
public class I18nService {

    private static Set<String> moduleSet = new CopyOnWriteArraySet<>();

    private static Map<String, Map<String, String>> i18nMap = new ConcurrentHashMap<>();
    private static Map<String, Map<String, String>> webI18nMap = new ConcurrentHashMap<>();

    @Resource
    private I18nMapper i18nMapper;
    @Resource
    private DiscoveryClient discoveryClient;
    @Resource
    private ModuleService moduleService;
    @Value("${spring.application.name}")
    private String moduleId;

    public static Map<String, String> lang(String lang) {
        CommonBeanFactory.getBean(I18nService.class).lang();
        return i18nMap.get(lang);
    }

    public static Map<String, String> langWeb(String lang) {
        CommonBeanFactory.getBean(I18nService.class).lang();
        return webI18nMap.get(lang);
    }

    private void lang() {

        List<String> services = new ArrayList<>();

        if (GlobalConfigurations.isReleaseMode()) {
            services.addAll(discoveryClient.getServices());
        } else {
            moduleService.getSystemModuleList().forEach(module -> services.add(module.getId()));
        }
        List<String> moduleIdList = new ArrayList<>();

        for (String moduleId : services) {
            if (!moduleSet.contains(moduleId)) {
                moduleIdList.add(moduleId);
            }
        }

        if (CollectionUtils.isEmpty(moduleIdList)) {
            return;
        }

        I18nExample example = new I18nExample();
        example.createCriteria().andIdIn(moduleIdList);
        List<I18nWithBLOBs> langs = i18nMapper.selectByExampleWithBLOBs(example);
        for (I18nWithBLOBs lang : langs) {
            merge(webI18nMap, parseObject(lang.getWebBundles(), Map.class));
            merge(i18nMap, parseObject(lang.getWebBundles(), Map.class));
            merge(i18nMap, parseObject(lang.getServerBundles(), Map.class));
        }
        moduleSet.addAll(moduleIdList);
    }

    private void merge(Map<String, Map<String, String>> old, Map<String, Map<String, String>> langs) {
        if (CollectionUtils.isEmpty(langs)) {
            return;
        }
        for (String lang : langs.keySet()) {
            Map<String, String> map = old.get(lang);
            if (CollectionUtils.isEmpty(map)) {
                old.put(lang, langs.get(lang));
                continue;
            }
            map.putAll(langs.get(lang));
        }
    }

    public void initI18n() {
        try {
            I18nWithBLOBs i18n = new I18nWithBLOBs();
            i18n.setId(moduleId);
            i18n.setWebBundles(JSON.toJSONString(I18nManager.getWebI18nMap()));
            i18n.setServerBundles(JSON.toJSONString(I18nManager.getServerI18nMap()));
            i18n.setUpdateTime(System.currentTimeMillis());

            I18nWithBLOBs ml = i18nMapper.selectByPrimaryKey(moduleId);
            if (ml == null) {
                i18nMapper.insertSelective(i18n);
            } else {
                i18nMapper.updateByPrimaryKeyWithBLOBs(i18n);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error("add module lang error:" + e.getMessage(), e);
        }

    }
}
