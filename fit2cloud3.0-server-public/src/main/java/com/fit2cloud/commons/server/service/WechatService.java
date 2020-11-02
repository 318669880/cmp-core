package com.fit2cloud.commons.server.service;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.SystemParameter;
import com.fit2cloud.commons.server.base.mapper.SystemParameterMapper;
import com.fit2cloud.commons.server.constants.ParamConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.model.NotificationBasicResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class WechatService {

    @Resource
    private SystemParameterMapper systemParameterMapper;
    @Resource
    private RestTemplate restTemplate;

    private static final String ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={corpid}&corpsecret={corpsecret}";
    private static final String MESSAGE_SEND_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send";

    public String getAccessToken() {
        String cropId = getNotNullValue(ParamConstants.WECHAT.CROPID.getKey());
        String secret = getNotNullValue(ParamConstants.WECHAT.SECRET.getKey());
        Map<String, String> params = new HashMap<>();
        params.put("corpid", cropId);
        params.put("corpsecret", secret);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(ACCESS_TOKEN_URL, String.class, params);
        if (responseEntity.getStatusCodeValue() != 200) {
            F2CException.throwException("request failed.");
        }
        String body = responseEntity.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        if (jsonObject.getIntValue("errcode") != 0) {
            F2CException.throwException(jsonObject.getString("errmsg"));
        }
        return jsonObject.getString("access_token");
    }

    public NotificationBasicResponse sendTextMessageToUser(String content, String... to) {
        if (StringUtils.isEmpty(content) || ArrayUtils.isEmpty(to)) {
            F2CException.throwException("required params can not be empty.");
        }
        String accessToken = getAccessToken();
        String url = MESSAGE_SEND_URL + "?access_token=" + accessToken;
        String agentId = getNotNullValue(ParamConstants.WECHAT.AGENTID.getKey());
        long l = 0;
        try {
            l = Long.parseLong(agentId);
        } catch (Exception e) {
            F2CException.throwException("agentId is not valid.");
        }
        Map<String, Object> params = new HashMap<>();
        Map<String, String> contentStr = new HashMap<>();
        contentStr.put("content", content);
        params.put("msgtype", "text");
        params.put("agentid", l);
        params.put("text", contentStr);
        params.put("touser", buildReceiver(to));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(params, headers);

        ResponseEntity<NotificationBasicResponse> responseResponseEntity = restTemplate.postForEntity(url, request, NotificationBasicResponse.class);
        return responseResponseEntity.getBody();
    }

    private String getValue(String key) {
        SystemParameter systemParameter = systemParameterMapper.selectByPrimaryKey(key);
        if (systemParameter != null) {
            return systemParameter.getParamValue();
        }
        return null;
    }

    private String getNotNullValue(String key) {
        SystemParameter systemParameter = systemParameterMapper.selectByPrimaryKey(key);
        if (systemParameter == null) {
            throw new RuntimeException(key + " not set");
        }
        String value = systemParameter.getParamValue();
        if (StringUtils.isBlank(value)) {
            throw new RuntimeException(key + " not set");
        }
        return value;
    }

    private String buildReceiver(String... list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String l : list) {
            stringBuilder.append(l).append("|");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }
}
