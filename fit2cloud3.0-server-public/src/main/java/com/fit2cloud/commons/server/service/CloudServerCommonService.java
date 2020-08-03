package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.CloudServer;
import com.fit2cloud.commons.server.base.domain.CloudServerExample;
import com.fit2cloud.commons.server.base.mapper.CloudServerMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtCloudServerMapper;
import com.fit2cloud.commons.server.constants.IpTypeConstants;
import com.fit2cloud.commons.server.constants.ResourceOperation;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.CloudServerDTO;
import com.fit2cloud.commons.server.model.CloudServerManageInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class CloudServerCommonService {

    @Resource
    private CloudServerMapper cloudServerMapper;
    @Resource
    private ExtCloudServerMapper extCloudServerMapper;
    @Resource
    private CloudServerCredentialService cloudServerCredentialService;

    public List<CloudServerDTO> selectCloudServerList(Map<String, Object> params) {
        return extCloudServerMapper.selectCloudServers(params);

    }

    public CloudServer get(String cloudServerId) {
        return cloudServerMapper.selectByPrimaryKey(cloudServerId);
    }

    public void updateCloudServerStatus(String instanceUuidId, String instanceStatus) {
        CloudServer cloudServer = new CloudServer();
        cloudServer.setInstanceStatus(instanceStatus);
        CloudServerExample cloudServerExample = new CloudServerExample();
        cloudServerExample.createCriteria().andInstanceUuidEqualTo(instanceUuidId);
        this.cloudServerMapper.updateByExampleSelective(cloudServer, cloudServerExample);
    }

    public void updateCloudServerOwner(String workspaceId, String serverId) {
        CloudServer cloudServer = cloudServerMapper.selectByPrimaryKey(serverId);
        cloudServer.setWorkspaceId(workspaceId);
        cloudServerMapper.updateByPrimaryKeySelective(cloudServer);
        OperationLogService.log(cloudServer, Translator.toI18nKey("i18n_workspace_grant"), Translator.toI18nKey("i18n_workspace_grant"));
    }

    /**
     * 更新云主机的管理信息
     *
     * @param id
     * @param info
     */
    public void updateCloudServerManageMentInfo(String id, CloudServerManageInfoDTO info) {
        CloudServer cloudServer = new CloudServer();
        String ipType = info.getIpType();
        cloudServer.setId(id);
        cloudServer.setOs(info.getOs());
        cloudServer.setOsVersion(info.getOsVersion());
        cloudServer.setManagementPort(info.getManagementPort());
        cloudServer.setIpType(ipType);
        if (StringUtils.equalsIgnoreCase(ipType, IpTypeConstants.IpType.ipv4.name())) {
            cloudServer.setManagementIp(info.getManagementIp());
        } else if (StringUtils.equalsIgnoreCase(ipType, IpTypeConstants.IpType.ipv6.name())) {
            cloudServer.setManagementIpv6(info.getManagementIpv6());
        } else if (StringUtils.equalsIgnoreCase(ipType, IpTypeConstants.IpType.DualStack.name())) {
            cloudServer.setManagementIp(info.getManagementIp());
            cloudServer.setManagementIpv6(info.getManagementIpv6());
        }
        cloudServerMapper.updateByPrimaryKeySelective(cloudServer);
        cloudServerCredentialService.updateCloudServerCredential(info.getCredentialList(), id);
    }
}
