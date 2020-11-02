package com.fit2cloud.commons.server.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiUserGetByMobileRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiUserGetByMobileResponse;
import com.fit2cloud.commons.server.base.domain.SystemParameter;
import com.fit2cloud.commons.server.base.mapper.SystemParameterMapper;
import com.fit2cloud.commons.server.constants.ParamConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.model.NotificationBasicResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DingtalkService {

    @Resource
    private SystemParameterMapper systemParameterMapper;
    private static final String ACCESS_TOKEN_URL = "https://oapi.dingtalk.com/gettoken";
    private static final String MOBILE_URL = "https://oapi.dingtalk.com/user/get_by_mobile";
    private static final String MSG_URL = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2";

    public String getAccessToken() throws Exception {
        String ak = getNotNullValue(ParamConstants.DINGTALK.APPKEY.getKey());
        String sk = getNotNullValue(ParamConstants.DINGTALK.APPSECRET.getKey());
        DefaultDingTalkClient client = new DefaultDingTalkClient(ACCESS_TOKEN_URL);
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey(ak);
        request.setAppsecret(sk);
        request.setHttpMethod("GET");
        OapiGettokenResponse response = client.execute(request);
        return response.getAccessToken();
    }

    public String getUserIdByMobile(String token, String mobile) throws Exception {
        DingTalkClient client = new DefaultDingTalkClient(MOBILE_URL);
        OapiUserGetByMobileRequest request = new OapiUserGetByMobileRequest();
        request.setMobile(mobile);
        OapiUserGetByMobileResponse execute = client.execute(request, token);
        return execute.getUserid();
    }

    public NotificationBasicResponse sendTextMessageToUser(String content, String... to) throws Exception {
        String token = getAccessToken();
        String agentId = getNotNullValue(ParamConstants.DINGTALK.AGENTID.getKey());
        if (ArrayUtils.isEmpty(to)) {
            F2CException.throwException("mobile can not be empty.");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String mobile : to) {
            String userId = getUserIdByMobile(token, mobile);
            stringBuilder.append(userId).append(",");
        }
        String userIdList = stringBuilder.substring(0, stringBuilder.length() - 1);

        DingTalkClient client = new DefaultDingTalkClient(MSG_URL);
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setUseridList(userIdList);
        request.setAgentId(Long.valueOf(agentId));
        request.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
        msg.getText().setContent(content);
        request.setMsg(msg);
        OapiMessageCorpconversationAsyncsendV2Response execute = client.execute(request, token);
        NotificationBasicResponse response = new NotificationBasicResponse();
        response.setErrcode(execute.getErrcode());
        response.setErrmsg(execute.getErrmsg());
        return response;
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
}
