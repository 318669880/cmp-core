package com.fit2cloud.commons.server.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.Module;
import com.fit2cloud.commons.server.constants.ModuleConstants;
import com.fit2cloud.commons.server.model.Menu;
import com.fit2cloud.commons.server.model.Permission;
import com.fit2cloud.commons.server.model.PermissionResource;
import com.fit2cloud.commons.server.service.ModuleService;
import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ServerInfo {

    @Resource
    private Environment environment;

    private Module module = new Module();

    private List<Menu> menuList = new ArrayList<>();

    private List<Permission> permissionList = new ArrayList<>();

    private List<PermissionResource> permissionResourceList = new ArrayList<>();

    public Module getModule() {
        return module;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public List<Permission> getPermissionList() {
        return permissionList;
    }

    public List<PermissionResource> getPermissionResourceList() {
        return permissionResourceList;
    }

    @PostConstruct
    public void initServerInfo() {
        //初始化菜单
        initMenu();
        //初始化权限
        initPermission();
        //初始化模块
        initModule();
    }

    private void initMenu() {
        InputStream inputStream = null;
        try {
            PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            if (!patternResolver.getResource("menu.json").exists()) {
                LogUtil.info("menu.json not found in this module");
            } else {
                inputStream = patternResolver.getResource("menu.json").getInputStream();
                String json = IOUtils.toString(new BufferedReader(new InputStreamReader(inputStream, UTF_8)));
                JSONObject object = JSON.parseObject(json);
                menuList.addAll(JSON.parseArray(object.getString("menu"), Menu.class));
                menuList.forEach(menu -> {
                    menu.setModuleId(environment.getProperty("spring.application.name"));
                    if (!StringUtils.isBlank(menu.getId())) {
                        menu.setId(menu.getModuleId() + menu.getId());
                    }
                });
                filterMenus();
            }
        } catch (Exception e) {
            LogUtil.error("parse menu.json error:" + e.getMessage());
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }


    private void initPermission() {
        InputStream inputStream = null;
        try {
            PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            if (!patternResolver.getResource("permission.json").exists()) {
                LogUtil.info("permission.json");
            } else {
                inputStream = patternResolver.getResource("permission.json").getInputStream();
                String json = IOUtils.toString(new BufferedReader(new InputStreamReader(inputStream, UTF_8)));
                JSONObject object = JSON.parseObject(json);
                permissionList.addAll(JSON.parseArray(object.getString("permissions"), Permission.class));
                filterPermissions();
                permissionResourceList.addAll(JSON.parseArray(object.getString("resources"), PermissionResource.class));
                filterPermissionResources();
            }
        } catch (Exception e) {
            LogUtil.error("parse permission.json error:" + e.getMessage());
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    private void filterMenus() {
        if (isKubernetes()) {
            for (Menu menu : menuList) {
                if (StringUtils.equalsIgnoreCase("setting", menu.getId())) {
                    if (CollectionUtils.isNotEmpty(menu.getChildren())) {
                        List<Menu> menus = new ArrayList<>();
                        for (Menu child : menu.getChildren()) {
                            if (!StringUtils.equalsIgnoreCase("sys", child.getName())) {
                                menus.add(child);
                            }
                        }
                        menu.setChildren(menus);
                    }
                }
            }
        }
    }

    private void filterPermissions() {
        if (isKubernetes()) {
            permissionList = permissionList.stream()
                    .filter(permission -> !StringUtils.endsWithIgnoreCase(permission.getResourceId(), "SYS_STATS_READ"))
                    .collect(Collectors.toList());
        }
    }

    private void filterPermissionResources() {
        if (isKubernetes()) {
            permissionResourceList = permissionResourceList.stream()
                    .filter(permission -> !StringUtils.endsWithIgnoreCase(permission.getId(), "SYS_STATS_READ"))
                    .collect(Collectors.toList());
        }
    }

    private boolean isKubernetes() {
        return environment.getProperty("KUBERNETES_PORT") != null && StringUtils.endsWithIgnoreCase(environment.getProperty("spring.application.name"), "management-center");
    }


    private void initModule() {
        module.setId(environment.getProperty("spring.application.name"));
        module.setName(environment.getProperty("module.name"));
        module.setPort(Long.parseLong(environment.getProperty("server.port")));
        module.setIcon(environment.getProperty("module.icon", "cloud_queue"));
        module.setSort(Integer.parseInt(environment.getProperty("module.order", "100")));
        module.setType(environment.getProperty("module.type", ModuleConstants.Type.extension.name()));
        module.setSummary(environment.getProperty("module.summary", ""));
        module.setLicense(environment.getProperty("module.license", ""));
        module.setVersion(environment.getProperty("module.version", ""));
    }


    /***
     * 入库 Module 信息
     * @param event
     */

    @EventListener
    public void initModuleDatabase(ContextRefreshedEvent event) {
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) event.getApplicationContext();
        ModuleService moduleService = applicationContext.getBean("moduleService", ModuleService.class);
        ServerInfo serverInfo = applicationContext.getBean("serverInfo", ServerInfo.class);
        moduleService.initModule(serverInfo.getModule());
//        if (BooleanUtils.isFalse(module.getAuth()) && GlobalConfigurations.isReleaseMode()) {
//            String msg = "当前模块license校验失败，即将停止....";
//            LogUtil.error(msg);
//            System.err.println(msg);
//            applicationContext.close();
//        }
    }
}
