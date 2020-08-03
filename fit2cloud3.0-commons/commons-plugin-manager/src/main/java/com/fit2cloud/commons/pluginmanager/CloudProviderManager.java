package com.fit2cloud.commons.pluginmanager;

import com.fit2cloud.sdk.AbstractCloudProvider;
import com.fit2cloud.sdk.F2CPlugin;
import com.fit2cloud.sdk.constants.Language;
import org.apache.commons.collections4.MapUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public final class CloudProviderManager extends BaseManager {

    private String targetFilePath;

    public JarFileClassLoader getJarFileClassLoader() {
        return jarFileClassLoader;
    }

    private JarFileClassLoader jarFileClassLoader;
    private List<Object> cloudProviders = new ArrayList<>();
    private Map<String, Map<String, String>> i18nMap = new HashMap<>();


    public CloudProviderManager(String targetFilePath, String basePackage) {
        super(F2CPlugin.class, basePackage);
        this.targetFilePath = targetFilePath;
    }

    private void scanFileToPath(String filePath, int innerLevel) {
        File file = new File(filePath);
        // get the folder list
        File[] array = file.listFiles();
        Optional.ofNullable(array).ifPresent(files -> {
            for (File tmp : array) {
                String tmpPath = tmp.getPath();
                int level = innerLevel - 1;
                if (level > 0) {
                    if (tmp.isDirectory()) {
                        scanFileToPath(tmpPath, level);
                    }
                }

                if (tmp.getName().endsWith(".jar")) {
                    try {
                        jarFileClassLoader.addFile(tmp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void init() {
        try {
            Thread currentThread = Thread.currentThread();
            ClassLoader sysCloader = currentThread.getContextClassLoader();
            jarFileClassLoader = new JarFileClassLoader(new URL[]{new File(targetFilePath).toURI().toURL()}, sysCloader);
            currentThread.setContextClassLoader(jarFileClassLoader);
            scanFileToPath(targetFilePath, 2);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        super.init();
        Collection plugins = getPlugins();
        this.cloudProviders.addAll(plugins);
        // 加载国际化文件
        for (Object p : plugins) {
            try {
                if (p instanceof AbstractCloudProvider) {
                    Map<String, Map<String, String>> result = ((AbstractCloudProvider) p).getI18nMap();
                    if (MapUtils.isEmpty(result)) {
                        continue;
                    }
                    for (Language lang : Language.values()) {
                        i18nMap.computeIfAbsent(lang.name().toLowerCase(), k -> new HashMap<>());
                        i18nMap.get(lang.name().toLowerCase()).putAll(result.get(lang.name()));
                    }
                }
            } catch (Exception e) {
                System.out.println("Load i18n failed. plugin: " + ((AbstractCloudProvider) p).getName());
                e.printStackTrace();
            }

        }
    }

    public <T> T getCloudProvider(String name) {
        for (Object cp : cloudProviders) {
            try {
                Method getName = cp.getClass().getMethod("getName");
                Object invoke = getName.invoke(cp);
                if (invoke != null && invoke.toString().equals(name)) {
                    return (T) cp;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Map<String, Map<String, String>> getI18nMap() {
        return i18nMap;
    }

}


