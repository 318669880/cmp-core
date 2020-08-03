package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.CloudServerCredential;
import com.fit2cloud.commons.server.base.domain.CloudServerCredentialExample;
import com.fit2cloud.commons.server.base.mapper.CloudServerCredentialMapper;
import com.fit2cloud.commons.utils.UUIDUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class CloudServerCredentialService {

    @Resource
    private CloudServerCredentialMapper cloudServerCredentialMapper;

    public void updateCloudServerCredential(List<CloudServerCredential> serverCredentialList, String cloudServerId) {
        CloudServerCredentialExample cloudServerCredentialExample = new CloudServerCredentialExample();
        cloudServerCredentialExample.createCriteria().andCloudServerIdEqualTo(cloudServerId);
        cloudServerCredentialMapper.deleteByExample(cloudServerCredentialExample);

        if (CollectionUtils.isEmpty(serverCredentialList)) {
            return;
        }
        long createTime = System.currentTimeMillis();
        serverCredentialList.forEach(cloudServerCredential -> {
            cloudServerCredential.setId(UUIDUtil.newUUID());
            cloudServerCredential.setCloudServerId(cloudServerId);
            cloudServerCredential.setCreateTime(createTime);
            cloudServerCredentialMapper.insertSelective(cloudServerCredential);
        });
    }


    public List<CloudServerCredential> getCloudServerCredentialList(String cloudServerId) {
        CloudServerCredentialExample cloudServerCredentialExample = new CloudServerCredentialExample();
        cloudServerCredentialExample.createCriteria().andCloudServerIdEqualTo(cloudServerId);
        return cloudServerCredentialMapper.selectByExampleWithBLOBs(cloudServerCredentialExample);
    }


}
