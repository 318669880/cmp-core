package com.fit2cloud.mc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.ExtraUser;
import com.fit2cloud.commons.server.base.domain.ExtraUserExample;
import com.fit2cloud.commons.server.base.mapper.ExtraUserMapper;
import com.fit2cloud.commons.server.constants.ParamConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.utils.CommonThreadPool;
import com.fit2cloud.commons.utils.EncryptUtils;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.common.constants.UserConstants;
import com.fit2cloud.mc.dao.ext.ExtExtraUserMapper;
import com.fit2cloud.mc.dao.ext.ExtUserMapper;
import com.fit2cloud.mc.dto.RoleInfo;
import com.fit2cloud.mc.dto.UserOperateDTO;
import com.fit2cloud.mc.dto.keycloakToken;
import com.fit2cloud.mc.dto.keycloakUser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ExtraUserService {

    @Resource(name = "remoteRestTemplate")
    private RestTemplate remoteRestTemplate;
    @Resource
    private ExtUserMapper extUserMapper;
    @Resource
    private ExtraUserMapper extraUserMapper;
    @Resource
    private Environment environment;
    @Resource
    private SystemParameterService systemParameterService;
    @Resource
    private ExtExtraUserMapper extExtraUserMapper;
    @Resource
    private CommonThreadPool commonThreadPool;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DefaultRedisScript<Boolean> distributedLockScript;
    @Resource
    private UserService userService;

    /**
     * https://www.keycloak.org/docs/latest/server_development/index.html
     * https://www.keycloak.org/docs-api/4.0/rest-api/index.html
     */
    public void syncExtraUser(Boolean isReload) {
        String username = systemParameterService.getValue(ParamConstants.KeyCloak.USERNAME.getValue());
        String aesPassword = systemParameterService.getValue(ParamConstants.KeyCloak.PASSWORD.getValue());
        if (StringUtils.isBlank(username) || StringUtils.isBlank(aesPassword)) {
            F2CException.throwException(Translator.get("i18n_ex_keycloak_sync"));
        }
        if (isReload) {
            sync(true, username, aesPassword);
        } else {
            Boolean execute = stringRedisTemplate.execute(distributedLockScript, Collections.singletonList("syncExtraUser"), "lock", String.valueOf(20));
            if (execute) {
                sync(isReload, username, aesPassword);
            }
        }

    }

    public void sync(Boolean isReload, String username, String aesPassword) {
        commonThreadPool.addTask(() -> {
            try {
                LogUtil.info("Start synchronizing users");
                AtomicInteger sum = new AtomicInteger();
                String password = EncryptUtils.aesDecrypt(aesPassword).toString();
                String serverAddress = environment.getProperty(ParamConstants.KeyCloak.ADDRESS.getValue());
                String realm = environment.getProperty(ParamConstants.KeyCloak.REALM.getValue());

                //todo 可以先让 keycloak sync users,再获取 user，keycloak 也可以设置定时同步

                //send request
                HttpHeaders userHeaders = new HttpHeaders();
                userHeaders.setAccept(new ArrayList<>(Collections.singleton(MediaType.APPLICATION_JSON)));
                userHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
                List<keycloakUser> keycloakUsers = new ArrayList<>();
                int first = 0;
                int max = 200;
                String userUrl = String.format("%s%s%s%s", serverAddress, "/admin/realms/", realm, "/users");
                List<keycloakUser> page;
                do {
                    page = new ArrayList<>();
                    String token = this.getToken(serverAddress, username, password);
                    userHeaders.set("Authorization", String.format("%s%s", "bearer ", token));
                    HttpEntity<Object> userEntity = new HttpEntity<>(userHeaders);
                    ResponseEntity<String> exchange = remoteRestTemplate.exchange(userUrl.concat("?first=" + first + "&max=" + max), HttpMethod.GET, userEntity, String.class);
                    page = JSON.parseArray(exchange.getBody(), keycloakUser.class);
                    if (CollectionUtils.isNotEmpty(page)) {
                        keycloakUsers.addAll(page);
                    }
                    LogUtil.debug(page.size() + String.format(" users obtained from this page %s/%s", first, max));
                    first = first + max;
                } while (CollectionUtils.isNotEmpty(page));

                LogUtil.info(keycloakUsers.size() + " users fetched from keycloak.");

                Set<String> ids = new HashSet<>();
                ids.addAll(extUserMapper.getIds());

                if (!isReload) {
                    List<ExtraUser> extraUsers = extraUserMapper.selectByExample(null);
                    extraUsers.forEach(extraUser -> ids.add(extraUser.getName()));
                }
                ExtraUser extraUser = new ExtraUser();
                Objects.requireNonNull(keycloakUsers).forEach(keycloakUser -> {
                    if (!StringUtils.isBlank(keycloakUser.getUsername()) && !ids.contains(keycloakUser.getUsername())) {
                        try {
                            extraUser.setId(UUIDUtil.newUUID());
                            extraUser.setEmail(keycloakUser.getEmail());
                            extraUser.setName(keycloakUser.getUsername());

                            if (StringUtils.isNotBlank(StringUtils.join(keycloakUser.getLastName(), keycloakUser.getFirstName()))) {
                                extraUser.setDisplayName(StringUtils.join(keycloakUser.getLastName(), keycloakUser.getFirstName()));
                            } else {
                                extraUser.setDisplayName(keycloakUser.getUsername());
                            }
                            extraUser.setPhone(extraUser.getPhone());
                            extraUser.setSyncTime(Instant.now().toEpochMilli());
                            extraUser.setType(UserConstants.Source.EXTRA.getValue());
                            extraUserMapper.insert(extraUser);
                            sum.getAndIncrement();
                            ids.add(keycloakUser.getUsername());
                        } catch (Exception e) {
                            LogUtil.error("Insert user failed: " + JSONObject.toJSONString(keycloakUser), e);
                        }
                    }
                });
                LogUtil.info("End sync user," + sum + " records in total");
            } catch (Exception e) {
                LogUtil.error("Synchronization user exception", e);
            }
        });
    }

    private String getToken(String serverAddress, String username, String password) {
        //get Token
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setAccept(new ArrayList<>(Collections.singleton(MediaType.APPLICATION_JSON)));
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> mvm = new LinkedMultiValueMap<>();
        mvm.add("client_id", "admin-cli");
        mvm.add("username", username);
        mvm.add("password", password);
        mvm.add("grant_type", "password");
        HttpEntity<Object> formEntity = new HttpEntity<>(mvm, tokenHeaders);
        String tokenUrl = String.format("%s%s", serverAddress, "/realms/master/protocol/openid-connect/token");
        ResponseEntity<keycloakToken> keycloakTokenEntity = remoteRestTemplate.postForEntity(tokenUrl, formEntity, keycloakToken.class);
        return Objects.requireNonNull(keycloakTokenEntity.getBody()).getAccessToken();
    }

    public List<ExtraUser> paging(Map<String, Object> map) {
        return extExtraUserMapper.paging(map);
    }

    public void reloadSync() {
        Boolean execute = stringRedisTemplate.execute(distributedLockScript, Collections.singletonList("syncExtraUser"), "lock", String.valueOf(20));
        if (execute) {
            extraUserMapper.deleteByExample(null);
            this.syncExtraUser(true);
        }
    }


    public void importUser(List<RoleInfo> roleInfoList, List<String> ids) {
        ExtraUserExample extraUserExample = new ExtraUserExample();
        extraUserExample.createCriteria().andIdIn(ids);
        List<ExtraUser> extraUsers = extraUserMapper.selectByExample(extraUserExample);
        boolean termination = false;
        int index = 0;
        String msg = Translator.get("i18n_ex_import_user");
        try {
            for (ExtraUser extraUser : extraUsers) {
                UserOperateDTO userOperateDto = new UserOperateDTO();
                userOperateDto.setEmail(extraUser.getEmail());
                userOperateDto.setName(extraUser.getDisplayName());
                userOperateDto.setCreateTime(Instant.now().toEpochMilli());
                userOperateDto.setPassword(UUIDUtil.newUUID());
                userOperateDto.setId(extraUser.getName());
                userOperateDto.setActive(true);
                userOperateDto.setPhone(extraUser.getPhone());
                userOperateDto.setSource(UserConstants.Source.EXTRA.getValue());
                userOperateDto.setCreateTime(Instant.now().toEpochMilli());
                userOperateDto.setRoleInfoList(roleInfoList);
                userService.insert(userOperateDto);
                extraUserMapper.deleteByPrimaryKey(extraUser.getId());
                index++;
            }
        } catch (Exception e) {
            termination = true;
            msg = String.format(msg, index, e.getMessage());
        }

        if (termination) {
            F2CException.throwException(msg);
        }
    }
}
