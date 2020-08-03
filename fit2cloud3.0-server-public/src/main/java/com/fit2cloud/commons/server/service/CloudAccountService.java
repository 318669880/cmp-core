package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.CloudAccount;
import com.fit2cloud.commons.server.base.domain.Plugin;
import com.fit2cloud.commons.server.base.domain.PluginExample;
import com.fit2cloud.commons.server.base.mapper.CloudAccountMapper;
import com.fit2cloud.commons.server.base.mapper.PluginMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtCloudAccountMapper;
import com.fit2cloud.commons.server.constants.CloudAccountConstants;
import com.fit2cloud.commons.server.constants.PluginConstants;
import com.fit2cloud.commons.server.model.CloudAccountDTO;
import com.fit2cloud.commons.server.model.CloudAccountImageSyncDTO;
import com.fit2cloud.commons.server.model.CloudAccountNetworkDTO;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class CloudAccountService {
    @Resource
    private CloudAccountMapper cloudAccountMapper;

    @Resource
    private ExtCloudAccountMapper extCloudAccountMapper;

    @Resource
    private PluginMapper pluginMapper;

    public List<CloudAccountDTO> selectAccounts(PluginConstants.Type pluginType) {
        return extCloudAccountMapper.getAccountList(ImmutableMap.of("pluginType", pluginType.name()));
    }

    public List<CloudAccountDTO> selectAccountsByStatus(PluginConstants.Type pluginType, CloudAccountConstants.Status status) {
        ImmutableMap<String, Object> param = ImmutableMap
                .of("pluginType", pluginType.name(), "status", status.name());
        return extCloudAccountMapper.getAccountList(param);
    }

    public CloudAccount selectAccountById(String id) {
        return cloudAccountMapper.selectByPrimaryKey(id);
    }

    public void updateAccount(CloudAccount cloudAccount) {
        cloudAccountMapper.updateByPrimaryKeySelective(cloudAccount);
    }

    public List<CloudAccountDTO> getAccountList(Map<String, Object> param) {
        return extCloudAccountMapper.getAccountList(param);
    }

    public List<CloudAccountImageSyncDTO> getAccountListWithImageSync(Map<String, Object> param) {
        return extCloudAccountMapper.getAccountListWithImageSync(param);
    }

    public List<CloudAccountNetworkDTO> getAccountListNetworkSync(Map<String, Object> param) {
        return extCloudAccountMapper.getAccountListNetwork(param);
    }

    public void deleteAccountById(String accountId) {
        cloudAccountMapper.deleteByPrimaryKey(accountId);
    }

    public Plugin getPluginByAccount(CloudAccount cloudAccount) {
        PluginExample pluginExample = new PluginExample();
        pluginExample.createCriteria().andNameEqualTo(cloudAccount.getPluginName());
        List<Plugin> pluginList = pluginMapper.selectByExample(pluginExample);
        if (CollectionUtils.isEmpty(pluginList)) {
            return null;
        }
        return pluginList.get(0);
    }
}
