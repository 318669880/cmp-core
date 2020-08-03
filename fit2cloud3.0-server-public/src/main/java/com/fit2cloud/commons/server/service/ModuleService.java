package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.Module;
import com.fit2cloud.commons.server.base.domain.ModuleExample;
import com.fit2cloud.commons.server.base.mapper.ModuleMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtModuleMapper;
import com.fit2cloud.commons.server.constants.DashboardColor;
import com.fit2cloud.commons.server.constants.ModuleConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.license.DefaultLicenseService;
import com.fit2cloud.commons.server.model.DashBoardTextDTO;
import com.fit2cloud.license.core.constants.LicenseConstants;
import com.fit2cloud.license.core.model.F2CLicenseResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ModuleService {

    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private DiscoveryClient discoveryClient;
    @Resource
    private ExtModuleMapper extModuleMapper;
    @Resource
    private DefaultLicenseService defaultLicenseService;

    /**
     * 如果权限里没有此模块，则直接停止当前模块
     */
    private boolean authModule(Module module) {

        /**
         * 如果license为空，则不校验
         */
        if (StringUtils.isBlank(module.getLicense())) {
            return true;
        }

        F2CLicenseResponse f2CLicenseResponse = defaultLicenseService.validateLicense();
        //如果校验失败,停掉所有加了license的模块
        if (LicenseConstants.Status.Fail.equals(f2CLicenseResponse.getStatus())) {
            return false;
        }

        LicenseConstants.Edition edition = f2CLicenseResponse.getLicense().getEdition();
        if (LicenseConstants.Edition.Standard.equals(edition)) {
            return module.getLicense().equals(LicenseConstants.Edition.Standard.name());
        }

        return true;
    }

    /**
     * 初始化模块信息
     *
     * @param module 模块信息
     */
    public void initModule(Module module) {

        if (StringUtils.isBlank(module.getId()) || StringUtils.isBlank(module.getName())) {
            return;
        }

        //boolean auth = authModule(module);
        module.setAuth(true);
        if (moduleMapper.selectByPrimaryKey(module.getId()) != null) {
            module.setActive(null);
            module.setStatus(null);
            moduleMapper.updateByPrimaryKeySelective(module);
        } else {
            module.setActive(true);
            module.setStatus(ModuleConstants.RunningStatus.running.name());
            moduleMapper.insertSelective(module);
        }
    }

    /**
     * 根据ID获取 module
     *
     * @param moduleId
     * @return
     */
    public Module getModuleById(String moduleId) {
        return moduleMapper.selectByPrimaryKey(moduleId);
    }

    /**
     * 获取所有链接模块
     *
     * @return List<Module>
     */
    public List<Module> getLinkModuleList() {
        ModuleExample moduleExample = new ModuleExample();
        moduleExample.createCriteria().andTypeEqualTo(ModuleConstants.Type.link.name());
        return moduleMapper.selectByExample(moduleExample);
    }

    /**
     * 根据角色列表获取启用的链接模块
     *
     * @param roleList roleList
     * @return List<Module>
     */
    public List<Module> getLinkEnableModuleListByRoleList(List<String> roleList) {
        if (CollectionUtils.isEmpty(roleList)) {
            return new ArrayList<>();
        }
        return extModuleMapper.getLinkEnableModuleListByRoleList(roleList);
    }

    /**
     * 根据角色列表获取启用的链接模块
     *
     * @return List<Module>
     */
    public List<Module> getLinkEnableModuleList() {

        ModuleExample moduleExample = new ModuleExample();
        moduleExample.createCriteria().andTypeEqualTo(ModuleConstants.Type.link.name())
                .andActiveEqualTo(true).andAuthEqualTo(true);
        return moduleMapper.selectByExample(moduleExample);
    }

    /**
     * 获取注册的模块，包含扩展模块、标准模块（包含非启用的模块）,已授权
     *
     * @return List<Module>
     */
    public List<Module> getSystemModuleList() {
        ModuleExample moduleExample = new ModuleExample();
        moduleExample.createCriteria().andTypeNotEqualTo(ModuleConstants.Type.link.name())
                .andAuthEqualTo(true);
        return moduleMapper.selectByExample(moduleExample);
    }

    /**
     * 获取注册的模块，包含扩展模块、标准模块(启用中的模块),已授权
     *
     * @return List<Module>
     */
    public List<Module> getSystemEnableModuleList() {
        ModuleExample moduleExample = new ModuleExample();
        moduleExample.createCriteria().andTypeNotEqualTo(ModuleConstants.Type.link.name())
                .andActiveEqualTo(true).andAuthEqualTo(true);
        List<Module> moduleList = moduleMapper.selectByExample(moduleExample);
        moduleList.sort(Comparator.comparing(Module::getSort));
        return moduleList;
    }


    /**
     * 获取所有模块，包含未授权的
     *
     * @return
     */
    public List<Module> getModuleList() {
        List<Module> moduleList = extModuleMapper.getModuleList();
        moduleList.sort(Comparator.comparing(Module::getSort));
        //to do
        convertModuleStatus(moduleList);
        return moduleList;
    }

    /**
     * 启用和禁止模块
     *
     * @param moduleId
     * @param active
     */
    public void activeModule(String moduleId, boolean active) {
        if ("dashboard".equals(moduleId) || "management-center".equals(moduleId) || "gateway".equals(moduleId)) {
            F2CException.throwException(Translator.get("i18n_ex_module_disable_enable"));
        }

        Module module = new Module();
        module.setId(moduleId);
        module.setActive(active);
        moduleMapper.updateByPrimaryKeySelective(module);
    }

    /**
     * convert 模块状态
     *
     * @param moduleList
     */
    private void convertModuleStatus(List<Module> moduleList) {
        List<String> services = discoveryClient.getServices();
        for (Module module : moduleList) {
            if (services.contains(module.getId()) || module.getType().equals(ModuleConstants.Type.link.name())) {
                module.setStatus(ModuleConstants.RunningStatus.running.name());
            } else {
                module.setStatus(ModuleConstants.RunningStatus.stopped.name());
            }
        }
    }


    public void addModule(Module module) {
        if (StringUtils.isBlank(module.getId())) {
            F2CException.throwException(Translator.get("i18n_ex_module_id_cant_null"));
        }

        if (moduleMapper.selectByPrimaryKey(module.getId()) != null) {
            F2CException.throwException(Translator.get("i18n_ex_module_id_exist"));
        }
        module.setType(ModuleConstants.Type.link.name());
        moduleMapper.insertSelective(module);
    }

    public void deleteModule(String id) {
        if ("dashboard".equals(id) || "management-center".equals(id) || "gateway".equals(id)) {
            F2CException.throwException(Translator.get("i18n_ex_module_forbidden_delete"));
        }

        moduleMapper.deleteByPrimaryKey(id);
    }

    public void updateModule(Module module) {
        if (StringUtils.isBlank(module.getId())) {
            F2CException.throwException(Translator.get("i18n_ex_module_id_cant_null"));
        }

        Module old = moduleMapper.selectByPrimaryKey(module.getId());
        if (old == null) {
            F2CException.throwException(Translator.get("i18n_ex_module_not_exist"));
        }
        if (!ModuleConstants.Type.link.name().equals(old.getType())) {
            F2CException.throwException(Translator.get("i18n_ex_module_no_link_dont_update"));
        }

        moduleMapper.updateByPrimaryKey(module);
    }

    /**
     * 获取系统模块授权，启动的,注册到注册中心的serviceID
     *
     * @return List<String>
     */
    public List<String> getAuthAndEnableServiceList() {

        List<String> resultList = new ArrayList<>();

        List<String> services = discoveryClient.getServices();

        List<Module> moduleList = this.getSystemEnableModuleList();

        if (!services.contains("management-center")) {
            for (Module module : moduleList) {
                resultList.add(module.getId());
            }
        } else {
            for (Module module : moduleList) {
                if (services.contains(module.getId())) {
                    resultList.add(module.getId());
                }
            }
        }
        return resultList;
    }


    public ArrayList<DashBoardTextDTO> getModuleToDashboard() {

        int runningCount = 0, stoppedCount = 0;
        ArrayList<DashBoardTextDTO> dtos = new ArrayList<>();

        List<Module> moduleList = getModuleList();
        for (Module module : moduleList) {
            if (module.getActive() && module.getAuth()) {
                if (ModuleConstants.RunningStatus.running.name().equals(module.getStatus())) {
                    runningCount++;
                } else {
                    stoppedCount++;
                }
            }
        }
        dtos.add(new DashBoardTextDTO(Translator.get("i18n_module_running"), runningCount, DashboardColor.GREEN.getColor()));
        if (stoppedCount > 0) {
            dtos.add(new DashBoardTextDTO(Translator.get("i18n_module_stopped"), stoppedCount, DashboardColor.RED.getColor()));
        } else {
            dtos.add(new DashBoardTextDTO(Translator.get("i18n_module_stopped"), stoppedCount));
        }
        return dtos;
    }

    public List<Module> syncModuleStatus() {
        List<Module> moduleList = getSystemEnableModuleList();
        List<String> services = discoveryClient.getServices();
        for (Module module : moduleList) {
            if (services.contains(module.getId())) {
                module.setStatus(ModuleConstants.RunningStatus.running.name());
            } else {
                module.setStatus(ModuleConstants.RunningStatus.stopped.name());
            }
        }
        return moduleList;
    }

}
