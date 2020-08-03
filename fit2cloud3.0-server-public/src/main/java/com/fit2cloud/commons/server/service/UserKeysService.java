package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.UserKey;
import com.fit2cloud.commons.server.base.domain.UserKeyExample;
import com.fit2cloud.commons.server.base.mapper.UserKeyMapper;
import com.fit2cloud.commons.server.constants.ApiKeysConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.utils.UUIDUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserKeysService {

    @Resource
    private UserKeyMapper userKeyMapper;

    @Resource
    private UserCommonService userCommonService;

    public List<UserKey> getUserKeysInfo(String userId) {
        UserKeyExample userKeysExample = new UserKeyExample();
        userKeysExample.createCriteria().andUserIdEqualTo(userId);
        userKeysExample.setOrderByClause("create_time");
        return userKeyMapper.selectByExample(userKeysExample);
    }

    public UserKey generateUserKey(String userId) {
        if (userCommonService.getUserById(userId) == null) {
            F2CException.throwException(String.format(Translator.get("i18n_ex_user_not_exist"), userId));
        }
        UserKeyExample userKeysExample = new UserKeyExample();
        userKeysExample.createCriteria().andUserIdEqualTo(userId);
        List<UserKey> userKeysList = userKeyMapper.selectByExample(userKeysExample);

        if (!CollectionUtils.isEmpty(userKeysList) && userKeysList.size() >= 5) {
            F2CException.throwException(Translator.get("i18n_ex_apikey_limit"));
        }

        UserKey userKeys = new UserKey();
        userKeys.setId(UUIDUtil.newUUID());
        userKeys.setUserId(userId);
        userKeys.setStatus(ApiKeysConstants.ACTIVE.name());
        userKeys.setAccessKey(RandomStringUtils.randomAlphanumeric(16));
        userKeys.setSecretKey(RandomStringUtils.randomAlphanumeric(16));
        userKeys.setCreateTime(System.currentTimeMillis());
        userKeyMapper.insert(userKeys);
        return userKeyMapper.selectByPrimaryKey(userKeys.getId());
    }

    public void deleteUserKey(String id) {
        userKeyMapper.deleteByPrimaryKey(id);
    }

    public void activeUserKey(String id) {
        UserKey userKeys = new UserKey();
        userKeys.setId(id);
        userKeys.setStatus(ApiKeysConstants.ACTIVE.name());
        userKeyMapper.updateByPrimaryKeySelective(userKeys);
    }

    public void disabledUserKey(String id) {
        UserKey userKeys = new UserKey();
        userKeys.setId(id);
        userKeys.setStatus(ApiKeysConstants.DISABLED.name());
        userKeyMapper.updateByPrimaryKeySelective(userKeys);
    }

    public UserKey getUserKey(String accessKey) {
        UserKeyExample userKeyExample = new UserKeyExample();
        userKeyExample.createCriteria().andAccessKeyEqualTo(accessKey).andStatusEqualTo(ApiKeysConstants.ACTIVE.name());
        List<UserKey> userKeysList = userKeyMapper.selectByExample(userKeyExample);
        if (!CollectionUtils.isEmpty(userKeysList)) {
            return userKeysList.get(0);
        }
        return null;
    }
}
