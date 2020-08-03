package com.fit2cloud.commons.server.service;

import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.MenuPreferenceMapper;
import com.fit2cloud.commons.server.base.mapper.RolePermissionMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtUserRoleMapper;
import com.fit2cloud.commons.server.constants.ModuleConstants;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.constants.WebConstants;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.*;
import com.fit2cloud.commons.server.module.ServerInfo;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.commons.utils.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class MenuService {

    private final static String EUREKA_MODULE_URL = "/module/menus";
    private final static String EUREKA_PERMISSION_URL = "/module/permission/";

    @Resource
    private ServerInfo serverInfo;
    @Resource
    private ModuleService moduleService;
    @Resource
    private RolePermissionMapper rolePermissionMapper;
    @Resource
    private MicroService microService;
    @Resource
    private MenuPreferenceMapper menuPreferenceMapper;
    @Resource
    private ExtUserRoleMapper extUserRoleMapper;


    public Set<String> getPermissionIdList(List<String> roleList) {

        return getPermissions(roleList).stream()
                .map(Permission::getId)
                .distinct()
                .collect(Collectors.toSet());
    }

    public List<Permission> getPermissions(List<String> roleList) {
        List<Permission> permissionList = new ArrayList<>();

        for (String role : roleList) {
            for (RoleConstants.Id constant : RoleConstants.Id.values()) {
                if (constant.name().equalsIgnoreCase(role)) {
                    permissionList.addAll(serverInfo.getPermissionList().stream()
                            .filter(permission -> permission.getParentRoles().contains(constant.name()))
                            .collect(Collectors.toList()));
                }

                RolePermissionExample rolePermissionExample = new RolePermissionExample();
                rolePermissionExample.createCriteria().andRoleIdEqualTo(role)
                        .andModuleIdEqualTo(serverInfo.getModule().getId());
                List<String> ids = rolePermissionMapper.selectByExample(rolePermissionExample).stream()
                        .map(RolePermission::getPermissionId)
                        .collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(ids)) {
                    serverInfo.getPermissionList().forEach(
                            permission -> {
                                if (ids.contains(permission.getId())) {
                                    permissionList.add(permission);
                                }
                            }
                    );
                }
            }
        }

        return permissionList;
    }

    private List<Permission> getPermissionsBySession() {
        Session session = SecurityUtils.getSubject().getSession();
        List<Permission> permissions = (List<Permission>) session.getAttribute(serverInfo.getModule().getId() + "PERMISSION");
        if (permissions == null) {
            permissions = getPermissions(SessionUtils.getUser().getRoleIdList());
        }
        return permissions;
    }

    private void setMenuAndPermission(ModuleDTO module) {

        List<Permission> permissions = getPermissionsBySession();

        List<Menu> menuList = JSON.parseArray(JSON.toJSONString(serverInfo.getMenuList()), Menu.class);

        List<Menu> menus = menuList.stream().filter(menu -> {
            if (CollectionUtils.isEmpty(menu.getChildren())) {
                return hasPermission(menu, permissions);
            } else {
                List<Menu> children = new ArrayList<>();

                menu.getChildren().forEach(child -> {
                    if (hasPermission(child, permissions)) {
                        children.add(child);
                    }
                });

                if (CollectionUtils.isEmpty(children)) {
                    return false;
                } else {
                    menu.setChildren(children);
                    return true;
                }

            }
        }).collect(Collectors.toList());

        menuSort(menus);
        module.setMenus(menus);
        module.setPermissions(permissions.stream()
                .map(Permission::getId)
                .distinct()
                .collect(Collectors.toSet()));
    }

    private void menuSort(List<Menu> menuList) {
        menuList.forEach(menu -> {
            if (!CollectionUtils.isEmpty(menu.getChildren())) {
                menu.getChildren().sort(Comparator.comparing(Menu::getOrder));
            }
        });
        menuList.sort(Comparator.comparing(Menu::getOrder));
    }

    private boolean hasPermission(Menu menu, List<Permission> permissionList) {
        if (CollectionUtils.isEmpty(menu.getRequiredPermissions())) {
            return false;
        }

        List<String> permissionIds = permissionList.stream().map(Permission::getId).collect(Collectors.toList());

        List<MenuPermission> menuPermissions = menu.getRequiredPermissions();

        for (MenuPermission menuPermission : menuPermissions) {
            if (StringUtils.isBlank(menuPermission.getRole()) ||
                    StringUtils.isBlank(menuPermission.getLogical()) ||
                    CollectionUtils.isEmpty(menuPermission.getPermissions()) ||
                    !menuPermission.getRole().equalsIgnoreCase(SessionUtils.getUser().getParentRoleId())) {
                continue;
            }

            if (anyMatchMenuPermission(menuPermission, permissionIds)) {
                return true;
            }
        }

        return false;
    }

    private boolean anyMatchMenuPermission(MenuPermission menuPermission, List<String> permissions) {

        boolean anyMatch = false;
        switch (menuPermission.getLogical()) {
            case "OR":
                anyMatch = menuPermission.getPermissions().stream().anyMatch(permissions::contains);
                break;
            case "AND":
                anyMatch = permissions.containsAll(menuPermission.getPermissions());
                break;
        }

        return anyMatch;
    }


    public ModuleDTO getModule() {

        ModuleDTO module = new ModuleDTO();
        BeanUtils.copyBean(module, moduleService.getModuleById(serverInfo.getModule().getId()));
        setMenuAndPermission(module);

        return module;
    }


    public Map<String, Object> getModules() {

        Map<String, Object> map = new HashMap<>();

        List<ModuleDTO> resultList = new ArrayList<>();
        if (!"other".equalsIgnoreCase(SessionUtils.getUser().getParentRoleId())) {
            //判断当前模块是否启用,是否授权
            List<String> services = moduleService.getAuthAndEnableServiceList();
            services.remove("gateway");
            if (GlobalConfigurations.isReleaseMode()) {
                List<ResultHolder> resultHolders = microService.getForResultHolder(services, EUREKA_MODULE_URL, 10);
                for (ResultHolder resultHolder : resultHolders) {
                    if (resultHolder.isSuccess()) {
                        ModuleDTO moduleDTO = JSON.parseObject(JSON.toJSONString(resultHolder.getData()), ModuleDTO.class);
                        if (!CollectionUtils.isEmpty(moduleDTO.getMenus())) {
                            resultList.add(moduleDTO);
                        }
                    }
                }
            } else {
                resultList.add(getModule());
            }

            for (ModuleDTO moduleDTO : resultList) {
                for (Menu menu : moduleDTO.getMenus()) {
                    if (!StringUtils.isEmpty(menu.getUrl())) {
                        menu.setUrl(menuUrlConvert(moduleDTO, menu.getUrl(), GlobalConfigurations.isReleaseMode()));
                        menu.setTemplateUrl(menu.getTemplateUrl() + "?_t=" + WebConstants.timestamp);
                    }
                    if (!CollectionUtils.isEmpty(menu.getChildren())) {
                        menu.getChildren().forEach(menu1 -> {
                                    menu1.setUrl(menuUrlConvert(moduleDTO, menu1.getUrl(), GlobalConfigurations.isReleaseMode()));
                                    menu1.setTemplateUrl(menu1.getTemplateUrl() + "?_t=" + WebConstants.timestamp);
                                }
                        );
                    }
                }
            }
        }
        resultList.sort(Comparator.comparing(ModuleDTO::getSort));
        getLinkModuleMenus(resultList);
        List<Menu> menuList = addTopMenu(resultList);
        menuList.sort(Comparator.comparing(Menu::getOrder));
        map.put("tops", menuList);
        map.put("modules", resultList);
        return map;
    }

    private List<Menu> addTopMenu(List<ModuleDTO> resultList) {
        List<Menu> menuList = new ArrayList<>();
        List<MenuPreference> menuPreferences = menuPreference();
        if (CollectionUtils.isEmpty(menuPreferences)) return menuList;
        Map<String, Integer> map = new HashMap<>();
        menuPreferences.forEach(menuPreference -> map.put(menuPreference.getMenuId(), menuPreference.getSort()));

        for (ModuleDTO moduleDTO : resultList) {
            for (Menu menu : moduleDTO.getMenus()) {
                if (map.get(menu.getId()) != null) {
                    menu.setTop(true);
                    Menu menu1 = new Menu();
                    BeanUtils.copyBean(menu1, menu);
                    menu1.setId(menu.getId());
                    menu1.setOrder(map.get(menu1.getId()));
                    menu1.setTitle(moduleDTO.getName() + "：" + menu.getTitle());
                    menuList.add(menu1);
                }
            }
        }

        return menuList;
    }

    private List<MenuPreference> menuPreference() {
        MenuPreferenceExample menuPreferenceExample = new MenuPreferenceExample();
        menuPreferenceExample.createCriteria().andUserIdEqualTo(SessionUtils.getUser().getId());
        return menuPreferenceMapper.selectByExample(menuPreferenceExample);
    }

    private void getLinkModuleMenus(List<ModuleDTO> resultList) {

        List<String> roleIdList = new ArrayList<>();
        roleIdList.addAll(SessionUtils.getRoleIdList());

        if (!"other".equalsIgnoreCase(SessionUtils.getUser().getParentRoleId())) {
            roleIdList.addAll(extUserRoleMapper.getCustomRolesByUserId(SessionUtils.getUser().getId()));
        }


        if (CollectionUtils.isEmpty(roleIdList)) return;

        //获取当前用户的自定义角色


        //非继承模块
        List<Module> moduleList = moduleService.getLinkEnableModuleListByRoleList(roleIdList);
        if (CollectionUtils.isEmpty(moduleList)) return;

        ModuleDTO moduleDTO = new ModuleDTO();
        moduleDTO.setId(ModuleConstants.Type.link.name());
        moduleDTO.setName(Translator.get("i18n_module_external_link"));
        moduleDTO.setIcon("insert_link");
        moduleDTO.setType(ModuleConstants.Type.link.name());
        List<Menu> menuList = new ArrayList<>();
        for (Module module : moduleList) {
            Menu menu = new Menu();
            menu.setId(module.getId());
            menu.setModuleId(moduleDTO.getId());
            menu.setIcon(module.getIcon());
            menu.setName(module.getName());
            menu.setTitle(module.getName());
            menu.setUrl(module.getModuleUrl());
            menu.setOpen(module.getOpen());
            menuList.add(menu);
        }
        moduleDTO.setMenus(menuList);
        resultList.add(moduleDTO);
    }

    private String menuUrlConvert(Module module, String url, boolean release) {
        StringBuilder buffer = new StringBuilder("/");
        if (release) {
            buffer.append(module.getId());
        }
        buffer.append("/?banner=false#!");

        if (!StringUtils.isEmpty(url)) {
            if (!url.startsWith("/")) {
                buffer.append("/");
            }
            buffer.append(url);
        }
        return buffer.toString().replace("//", "/");

    }

    public TreeNode getModulePermissionByRoleId(String roleId) {

        Module module = serverInfo.getModule();

        TreeNode root = new TreeNode(module.getId(), module.getName());

        Map<String, List<Permission>> listMap = new HashMap<>();

        serverInfo.getPermissionList().stream()
                .filter(permission -> permission.getParentRoles().contains(roleId))
                .forEach(permission -> {
                    if (!listMap.containsKey(permission.getResourceId())) {
                        listMap.put(permission.getResourceId(), new ArrayList<>());
                    }
                    listMap.get(permission.getResourceId()).add(permission);
                });

        convertTreeNode(listMap, root);

        return root;
    }

    private void convertTreeNode(Map<String, List<Permission>> listMap, TreeNode root) {

        List<String> rootKeys = new ArrayList<>();
        Map<String, List<String>> keyMap = new HashMap<>();

        convertKey(listMap.keySet(), rootKeys, keyMap);
        Map<String, TreeNode> treeNodeMap = convertPermissionMap(listMap);

        rootKeys.forEach(key -> {
            TreeNode treeNode = treeNodeMap.get(key);
            addSubTreeNode(keyMap, treeNode, treeNodeMap);
            root.getChildren().add(treeNode);

        });

    }

    private void addSubTreeNode(Map<String, List<String>> keyMap, TreeNode treeNode, Map<String, TreeNode> treeNodeMap) {
        List<String> subKeys = keyMap.get(treeNode.getId());
        if (!CollectionUtils.isEmpty(subKeys)) {
            subKeys.forEach(subKey -> {
                TreeNode subTreeNode = treeNodeMap.get(subKey);
                treeNode.getChildren().add(subTreeNode);
                addSubTreeNode(keyMap, subTreeNode, treeNodeMap);
            });
        }
    }

    private Map<String, TreeNode> convertPermissionMap(Map<String, List<Permission>> listMap) {
        Map<String, TreeNode> treeNodeMap = new HashMap<>();
        listMap.forEach((resourceId, permissionList) -> treeNodeMap.put(resourceId, permissionToTreeNode(resourceId, permissionList)));
        return treeNodeMap;
    }

    private TreeNode permissionToTreeNode(String resourceId, List<Permission> permissionList) {
        TreeNode treeNode = new TreeNode(resourceId, getResourceName(resourceId));

        if (CollectionUtils.isEmpty(permissionList)) {
            return treeNode;
        }

        permissionList.forEach(permission -> {
            TreeNode subTreeNode = new TreeNode(permission.getId(), permission.getName());
            treeNode.getChildren().add(subTreeNode);
        });

        return treeNode;
    }

    private String getResourceName(String resourceId) {
        for (PermissionResource permissionResource : serverInfo.getPermissionResourceList()) {
            if (resourceId.equalsIgnoreCase(permissionResource.getId())) {
                return permissionResource.getName();
            }
        }

        return "";
    }

    private void convertKey(Set<String> keys, List<String> rootKeys, Map<String, List<String>> keyMap) {

        List<String> list = keys.stream().sorted().collect(Collectors.toList());

        list.forEach(key -> {
            int index = key.lastIndexOf(":");
            if (index == -1) {
                keyMap.put(key, new ArrayList<>());
                rootKeys.add(key);
            } else {
                String parentKey = key.substring(0, index);
                if (keyMap.containsKey(parentKey)) {
                    keyMap.get(parentKey).add(key);
                } else {
                    keyMap.put(key, new ArrayList<>());
                    rootKeys.add(key);
                }
            }
        });
    }


    public List<TreeNode> getPermissionByRoleId(String roleId) {

        List<TreeNode> resultList = new ArrayList<>();

        List<String> services = moduleService.getAuthAndEnableServiceList();
        services.remove("gateway");

        //获取角色ID是否是不继承 系统内部的
        if (!"other".equalsIgnoreCase(roleId)) {

            List<ResultHolder> resultHolders = microService.getForResultHolder(services, EUREKA_PERMISSION_URL + roleId);

            for (ResultHolder resultHolder : resultHolders) {
                if (resultHolder.isSuccess()) {
                    TreeNode node = JSON.parseObject(JSON.toJSONString(resultHolder.getData()), TreeNode.class);
                    if (node != null && !CollectionUtils.isEmpty(node.getChildren())) {
                        resultList.add(node);
                    }
                }
            }
        }

        resultList.addAll(getLinkModuleTreeNodes());

        return resultList;
    }

    private List<TreeNode> getLinkModuleTreeNodes() {

        List<TreeNode> nodeList = new ArrayList<>();

        List<Module> moduleList = moduleService.getLinkEnableModuleList();

        for (Module module : moduleList) {
            TreeNode node = new TreeNode();
            node.setId(module.getId());
            node.setName(module.getName());
            node.setOrder(module.getSort());
            nodeList.add(node);
        }

        return nodeList;

    }

    /**
     * 操作菜单推荐偏好
     *
     * @param menuPreference
     */
    public void opMenuPreference(MenuPreference menuPreference) {
        //如果已经存在，取消，否则添加
        synchronized (SessionUtils.getUser().getId().intern()) {
            MenuPreferenceExample menuPreferenceExample = new MenuPreferenceExample();
            menuPreferenceExample.createCriteria().andMenuIdEqualTo(menuPreference.getMenuId())
                    .andModuleIdEqualTo(menuPreference.getModuleId()).andUserIdEqualTo(SessionUtils.getUser().getId());
            List<MenuPreference> menuPreferences = menuPreferenceMapper.selectByExample(menuPreferenceExample);

            if (CollectionUtils.isEmpty(menuPreferences)) {
                //添加
                menuPreference.setId(UUIDUtil.newUUID());
                menuPreference.setUserId(SessionUtils.getUser().getId());
                menuPreferenceMapper.insertSelective(menuPreference);
            } else {
                menuPreferenceMapper.deleteByExample(menuPreferenceExample);
            }
        }
    }

    public void sortMenuPreference(List<MenuPreference> menuPreferences) {
        for (MenuPreference menuPreference : menuPreferences) {
            MenuPreferenceExample menuPreferenceExample = new MenuPreferenceExample();
            menuPreferenceExample.createCriteria().andMenuIdEqualTo(menuPreference.getMenuId())
                    .andModuleIdEqualTo(menuPreference.getModuleId()).andUserIdEqualTo(SessionUtils.getUser().getId());
            menuPreferenceMapper.updateByExampleSelective(menuPreference, menuPreferenceExample);
        }
    }
}
