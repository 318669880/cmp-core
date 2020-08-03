package com.fit2cloud.commons.server.i18n;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fit2cloud.commons.pluginmanager.CloudProviderManager;
import com.fit2cloud.commons.server.constants.Lang;
import com.fit2cloud.commons.server.service.I18nService;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class I18nManager implements ApplicationRunner {

    private static final String webFormat = "web.%s.json";

    private static final HashSet<String> REMOVER_KEYS = new HashSet<>(Arrays.asList("id", "root", "system"));

    private static Map<String, Map<String, String>> i18nMap = new HashMap<>();
    //web
    private static Map<String, Map<String, String>> webI18nMap = new HashMap<>();
    //server
    private static Map<String, Map<String, String>> serverI18nMap = new HashMap<>();

    private List<String> dirs;

    public I18nManager(List<String> dirs) {
        this.dirs = dirs;
    }

    public static Map<String, Map<String, String>> getI18nMap() {
        return i18nMap;
    }

    public static Map<String, Map<String, String>> getWebI18nMap() {
        return webI18nMap;
    }

    public static Map<String, Map<String, String>> getServerI18nMap() {
        return serverI18nMap;
    }

    private static Resource[] getResources(String dir, String suffix) throws IOException {
        Resource[] result = new Resource[0];
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        if (!patternResolver.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + dir).exists()) {
            return result;
        }
        Resource[] resources = patternResolver.getResources(ResourceUtils.CLASSPATH_URL_PREFIX + dir + "*");
        for (Resource resource : resources) {
            if (StringUtils.endsWithIgnoreCase(resource.getFilename(), suffix)) {
                result = ArrayUtils.add(result, resource);
            }
        }
        return result;
    }

    private void init() {
        try {
            for (Lang lang : Lang.values()) {
                Resource[] resources = new Resource[0];
                String i18nKey = lang.getDesc().toLowerCase();
                for (String dir : dirs) {
                    resources = ArrayUtils.addAll(resources, getResources(dir, i18nKey + ".json"));
                }
                for (Resource resource : resources) {
                    if (resource.exists()) {
                        try (InputStream inputStream = resource.getInputStream()) {
                            String fileContent = IOUtils.toString(inputStream);
                            Map<String, String> langMap = JSON.parseObject(fileContent, new TypeReference<HashMap<String, String>>() {
                            });
                            i18nMap.computeIfAbsent(i18nKey, k -> new HashMap<>());
                            i18nMap.get(i18nKey).putAll(langMap);
                            if (StringUtils.containsIgnoreCase(resource.getFilename(), String.format(webFormat, i18nKey))) {
                                webI18nMap.computeIfAbsent(i18nKey, k -> new HashMap<>());
                                webI18nMap.get(i18nKey).putAll(langMap);
                                continue;
                            }
                            serverI18nMap.computeIfAbsent(i18nKey, k -> new HashMap<>());
                            serverI18nMap.get(i18nKey).putAll(langMap);
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.error("failed to load resource: " + resource.getURI());
                        }
                    }
                }
                // 插件国际化加载
                try {
                    CloudProviderManager cloudProviderManager = CommonBeanFactory.getBean(CloudProviderManager.class);
                    if (cloudProviderManager != null) {
                        Map<String, Map<String, String>> result = cloudProviderManager.getI18nMap();
                        if (MapUtils.isNotEmpty(result) && result.containsKey(i18nKey)) {
                            i18nMap.computeIfAbsent(i18nKey, k -> new HashMap<>());
                            i18nMap.get(i18nKey).putAll(result.get(i18nKey));
                            serverI18nMap.computeIfAbsent(i18nKey, k -> new HashMap<>());
                            serverI18nMap.get(i18nKey).putAll(result.get(i18nKey));
                        }
                    }
                } catch (Exception e) {
                    LogUtil.warn("failed to load plugin i18n.");
                }
            }

            mergeMaps(Lang.zh_TW, Lang.zh_CN, Lang.en_US, Lang.zh_TW);
            mergeMaps(Lang.zh_CN, Lang.en_US, Lang.zh_CN);
            mergeMaps(Lang.en_US, Lang.zh_CN, Lang.en_US);
        } catch (Exception e) {
            LogUtil.error("failed to load i18n.", e);
        }
    }

    // 可能有些语言json不完整，为了避免显示未翻译的key，需要处理一下优先级，覆盖不完整的字典
    protected void mergeMaps(Lang lang, Lang... candidates) {
        Map<String, String> _i18nMap = new HashMap<>();
        Map<String, String> _webI18nMap = new HashMap<>();
        Map<String, String> _serverI18nMap = new HashMap<>();

        for (Lang candidate : candidates) {
            if (MapUtils.isNotEmpty(i18nMap.get(candidate.getDesc().toLowerCase()))) {
                _i18nMap.putAll(i18nMap.get(candidate.getDesc().toLowerCase()));
            }
        }
        i18nMap.put(lang.getDesc().toLowerCase(), _i18nMap);

        for (Lang candidate : candidates) {
            if (MapUtils.isNotEmpty(webI18nMap.get(candidate.getDesc().toLowerCase()))) {
                _webI18nMap.putAll(webI18nMap.get(candidate.getDesc().toLowerCase()));
            }
        }
        webI18nMap.put(lang.getDesc().toLowerCase(), _webI18nMap);

        for (Lang candidate : candidates) {
            if (MapUtils.isNotEmpty(serverI18nMap.get(candidate.getDesc().toLowerCase()))) {
                _serverI18nMap.putAll(serverI18nMap.get(candidate.getDesc().toLowerCase()));
            }
        }
        serverI18nMap.put(lang.getDesc().toLowerCase(), _serverI18nMap);
    }

    /**
     * 国际化配置初始化
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        init();
        removerKeys();
        CommonBeanFactory.getBean(I18nService.class).initI18n();
    }

    private void removerKeys() {
        List<Map<String, Map<String, String>>> maps = Arrays.asList(i18nMap, webI18nMap, serverI18nMap);
        for (Map<String, Map<String, String>> map : maps) {
            for (String lang : map.keySet()) {
                Map<String, String> i18nMap = map.get(lang);
                Iterator<String> iterator = i18nMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String i18nKey = iterator.next();
                    for (String removerKey : REMOVER_KEYS) {
                        if (StringUtils.equalsIgnoreCase(i18nKey, removerKey)) {
                            iterator.remove();
                            i18nMap.remove(i18nKey);
                        }
                    }
                }
            }
        }
    }
}
