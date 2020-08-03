package com.fit2cloud.mc.service;

import com.fit2cloud.commons.pluginmanager.CloudProviderManager;
import com.fit2cloud.commons.server.base.domain.PluginWithBLOBs;
import com.fit2cloud.commons.server.base.mapper.PluginMapper;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.dao.ext.ExtPluginMapper;
import com.fit2cloud.mc.dto.PluginInfoDTO;
import com.fit2cloud.sdk.model.PluginInfo;
import com.google.gson.Gson;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PluginService {

    @Resource
    private PluginMapper pluginMapper;

    @Resource
    private CloudProviderManager cloudProviderManager;

    @Resource
    private ExtPluginMapper extPluginMapper;

    public List<PluginWithBLOBs> getAllPlugin() {
        return pluginMapper.selectByExampleWithBLOBs(null);
    }

    public Object getCredential(String pluginName) {
        try {
            Object cloudProvider = cloudProviderManager.getCloudProvider(pluginName);
            if (cloudProvider == null) {
                F2CException.throwException("Plugin not found.");
            }
            Method method = cloudProvider.getClass().getMethod("getCredentialPageTemplate");
            return method.invoke(cloudProvider);
        } catch (Exception e) {
            LogUtil.error("Error getting credential parameters: " + pluginName, e);
            F2CException.throwException(Translator.get("i18n_ex_plugin_get"));
            return "";
        }
    }

    public List<PluginInfoDTO> getPluginList(Map<String, Object> param) {
        List<PluginWithBLOBs>  pluginWithBLOBs = extPluginMapper.getPluginList(param);
        return setPluginInfo(pluginWithBLOBs);
    }

    private List<PluginInfoDTO> setPluginInfo(List<PluginWithBLOBs>  pluginWithBLOBs){
        List<PluginInfoDTO> pluginInfoDTOS = new ArrayList<>();
        for (PluginWithBLOBs pluginWithBLOB : pluginWithBLOBs) {
            PluginInfoDTO pluginInfoDTO = new PluginInfoDTO();
            pluginInfoDTO = BeanUtils.copyBean(pluginInfoDTO, pluginWithBLOB);
            Object cloudProvider = cloudProviderManager.getCloudProvider(pluginInfoDTO.getName());
            if (cloudProvider != null) {
                try {
                    Method method = cloudProvider.getClass().getMethod("getPluginInfo");
                    Object object = method.invoke(cloudProvider);
                    if(object != null){
                        PluginInfo  pluginInfo = new Gson().fromJson(object.toString(), PluginInfo.class);
                        pluginInfoDTO.setPlatformVersion(pluginInfo.getPlatformVersion());
                        pluginInfoDTO.setPluginVersion(pluginInfo.getPluginVersion());
                        pluginInfoDTO.setBuildTime(pluginInfo.getBuildTime());
                        pluginInfoDTO.setDocumentUrl(pluginInfo.getDocumentUrl());
                    }
                }catch (Exception ignore){}
            }
            pluginInfoDTOS.add(pluginInfoDTO);
        }
        return pluginInfoDTOS;
    }
}
