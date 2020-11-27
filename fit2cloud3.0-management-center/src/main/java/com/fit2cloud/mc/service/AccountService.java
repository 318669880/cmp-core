package com.fit2cloud.mc.service;

import com.fit2cloud.commons.pluginmanager.CloudProviderManager;
import com.fit2cloud.commons.server.base.domain.CloudAccount;
import com.fit2cloud.commons.server.base.domain.CloudAccountExample;
import com.fit2cloud.commons.server.base.domain.Plugin;
import com.fit2cloud.commons.server.base.domain.PluginExample;
import com.fit2cloud.commons.server.base.mapper.CloudAccountMapper;
import com.fit2cloud.commons.server.base.mapper.PluginMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtCloudAccountMapper;
import com.fit2cloud.commons.server.constants.CloudAccountConstants;
import com.fit2cloud.commons.server.constants.RedisConstants;
import com.fit2cloud.commons.server.constants.ResourceOperation;
import com.fit2cloud.commons.server.constants.ResourceTypeConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.CloudAccountDTO;
import com.fit2cloud.commons.server.redis.subscribe.RedisMessagePublisher;
import com.fit2cloud.commons.server.service.CloudAccountService;
import com.fit2cloud.commons.server.service.OperationLogService;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.dto.request.CreateCloudAccountRequest;
import com.fit2cloud.mc.dto.request.UpdateCloudAccountRequest;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class AccountService extends CloudAccountService {
    @Resource
    private CloudAccountMapper cloudAccountMapper;
    @Resource
    private CloudProviderManager cloudProviderManager;
    @Resource
    private RedisMessagePublisher redisMessagePublisher;
    @Resource
    private PluginMapper pluginMapper;

    @Resource
    private ExtCloudAccountMapper extCloudAccountMapper;


    public CloudAccountDTO addAccount(CreateCloudAccountRequest request) {
        //参数校验
        if (StringUtils.isEmpty(request.getCredential())
                || StringUtils.isEmpty(request.getName()) || StringUtils.isEmpty(request.getPluginName())) {
            F2CException.throwException(Translator.get("i18n_ex_cloud_account_name_or_plugin"));
        }

        //校验云插件是否存在
        PluginExample pluginExample = new PluginExample();
        pluginExample.createCriteria().andNameEqualTo(request.getPluginName());

        List<Plugin> plugins = pluginMapper.selectByExample(pluginExample);
        if (CollectionUtils.isEmpty(plugins)) {
            F2CException.throwException(Translator.get("i18n_ex_cloud_account_no_exist_plugin"));
        }

        //云账号名称不能重复
        CloudAccountExample cloudAccountExample = new CloudAccountExample();
        cloudAccountExample.createCriteria().andNameEqualTo(request.getName());
        List<CloudAccount> accountList = cloudAccountMapper.selectByExample(cloudAccountExample);
        if (!CollectionUtils.isEmpty(accountList)) {
            F2CException.throwException(Translator.get("i18n_ex_cloud_account_name_duplicate"));
        }

        CloudAccount cloudAccount = new CloudAccount();

        BeanUtils.copyBean(cloudAccount, request);
        cloudAccount.setCreateTime(System.currentTimeMillis());
        cloudAccount.setId(UUIDUtil.newUUID());
        cloudAccountMapper.insertSelective(cloudAccount);
        OperationLogService.log(null, cloudAccount.getId(), cloudAccount.getName(), ResourceTypeConstants.CLOUD_ACCOUNT.name(), ResourceOperation.CREATE, null);
        return getCloudAccountDTOById(cloudAccount.getId());
    }

    private CloudAccountDTO getCloudAccountDTOById(String id) {
        List<CloudAccountDTO> accountList = extCloudAccountMapper.getAccountList(ImmutableMap.of("id", id));
        if (!CollectionUtils.isEmpty(accountList) && accountList.size() == 1) {
            return accountList.get(0);
        }

        return null;
    }

    private boolean validateAccount(CloudAccount cloudAccount) {
        try {
            Object cloudProvider = cloudProviderManager.getCloudProvider(cloudAccount.getPluginName());
            if (cloudProvider == null) {
                throw new Exception("CloudProvider not found by name: " + cloudAccount.getPluginName());
            }
            return (boolean) MethodUtils.invokeMethod(cloudProvider, "validateCredential", cloudAccount.getCredential());
        } catch (Exception e) {
            Throwable t = e;
            if (e instanceof InvocationTargetException) {
                t = ((InvocationTargetException) e).getTargetException();
            }
            LogUtil.error(String.format("Exception in verifying cloud account, cloud account: [%s], plugin: [%s], error information:%s", cloudAccount.getName(), cloudAccount.getPluginName(), t.getMessage()), t);
            return false;
        }
    }

    public CloudAccountDTO editAccount(UpdateCloudAccountRequest request) {
        //参数校验
        if (StringUtils.isEmpty(request.getCredential())
                || StringUtils.isEmpty(request.getId())) {
            F2CException.throwException(Translator.get("i18n_ex_cloud_account_id_or_plugin"));
        }


        //云账号名称不能重复
        CloudAccountExample cloudAccountExample = new CloudAccountExample();
        cloudAccountExample.createCriteria().andNameEqualTo(request.getName()).andIdNotEqualTo(request.getId());
        List<CloudAccount> accountList = cloudAccountMapper.selectByExample(cloudAccountExample);
        if (!CollectionUtils.isEmpty(accountList)) {
            F2CException.throwException(Translator.get("i18n_ex_cloud_account_name_duplicate"));
        }

        if (cloudAccountMapper.selectByPrimaryKey(request.getId()) == null) {
            F2CException.throwException(Translator.get("i18n_ex_cloud_account_no_exist_id"));
        }

        CloudAccount cloudAccount = new CloudAccount();
        BeanUtils.copyBean(cloudAccount, request);
        cloudAccountMapper.updateByPrimaryKeySelective(cloudAccount);
        //检验账号已更新状态
        OperationLogService.log(null, cloudAccount.getId(), cloudAccount.getName(), ResourceTypeConstants.CLOUD_ACCOUNT.name(), ResourceOperation.UPDATE, null);

        return getCloudAccountDTOById(cloudAccount.getId());
    }

    public boolean validate(String id) {
        CloudAccount cloudAccount = cloudAccountMapper.selectByPrimaryKey(id);
        //检验账号的有效性
        boolean valid = validateAccount(cloudAccount);
        if (valid) {
            cloudAccount.setStatus(CloudAccountConstants.Status.VALID.name());
        } else {
            cloudAccount.setStatus(CloudAccountConstants.Status.INVALID.name());
        }
        cloudAccountMapper.updateByPrimaryKeySelective(cloudAccount);
        return valid;
    }

    public void delete(List<String> idList) {
        if (!CollectionUtils.isEmpty(idList)) {
            CloudAccountExample example = new CloudAccountExample();
            example.createCriteria().andIdIn(idList);
            List<CloudAccount> cloudAccounts = cloudAccountMapper.selectByExample(example);
            cloudAccounts.forEach(account -> {
                account.setStatus(CloudAccountConstants.Status.DELETED.name());
                cloudAccountMapper.updateByPrimaryKeySelective(account);
                redisMessagePublisher.publish(RedisConstants.Topic.CLOUD_ACCOUNT, account);
            });
        }
    }

    public void delete(String accountId) {
        CloudAccount cloudAccount = cloudAccountMapper.selectByPrimaryKey(accountId);
        if (cloudAccount != null) {
            cloudAccount.setStatus(CloudAccountConstants.Status.DELETED.name());
            redisMessagePublisher.publish(RedisConstants.Topic.CLOUD_ACCOUNT, cloudAccount);
        }
    }

    public void sync(List<String> idList) {
        if (!CollectionUtils.isEmpty(idList)) {
            syncResource(idList.toArray(new String[0]));
        }
    }

    public void syncResource(String... ids) {
        Arrays.stream(ids).forEach(id -> {
            CloudAccount account = cloudAccountMapper.selectByPrimaryKey(id);
            sync(account);
        });
    }

    private void sync(CloudAccount account) {

        if (CloudAccountConstants.SyncStatus.sync.name().equals(account.getSyncStatus())) {
            return;
        }
        //校验账号是否有效
        if (!validate(account.getId())) {
            CloudAccount tmpCloudAccount = new CloudAccount();
            tmpCloudAccount.setId(account.getId());
            tmpCloudAccount.setSyncStatus(CloudAccountConstants.SyncStatus.error.name());
            tmpCloudAccount.setUpdateTime(System.currentTimeMillis());
            cloudAccountMapper.updateByPrimaryKeySelective(tmpCloudAccount);
            return;
        }
        OperationLogService.log(null, account.getId(), account.getName(), ResourceTypeConstants.CLOUD_ACCOUNT.name(), ResourceOperation.SYNC, null);
        account.setSyncStatus(CloudAccountConstants.SyncStatus.pending.name());
        account.setStatus(null);
        cloudAccountMapper.updateByPrimaryKeySelective(account);

        redisMessagePublisher.publish(RedisConstants.Topic.CLOUD_ACCOUNT, account);
    }
}
