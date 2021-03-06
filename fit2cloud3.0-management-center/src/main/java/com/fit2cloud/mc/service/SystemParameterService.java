package com.fit2cloud.mc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiUserGetByMobileRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiUserGetByMobileResponse;
import com.fit2cloud.commons.server.base.domain.FileStore;
import com.fit2cloud.commons.server.base.domain.SystemParameter;
import com.fit2cloud.commons.server.base.domain.SystemParameterExample;
import com.fit2cloud.commons.server.base.mapper.FileStoreMapper;
import com.fit2cloud.commons.server.base.mapper.SystemParameterMapper;
import com.fit2cloud.commons.server.constants.ParamConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.NotificationBasicResponse;
import com.fit2cloud.commons.server.service.DingtalkService;
import com.fit2cloud.commons.server.service.WechatService;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.EncryptUtils;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.dto.SystemParameterDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.*;

/**
 * Author: chunxing
 * Date: 2018/6/21  ??????6:45
 * Description:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SystemParameterService {

    @Resource
    private SystemParameterMapper parameterMapper;
    @Resource
    private Environment environment;
    @Resource
    private FileStoreMapper fileStoreMapper;
    @Resource
    private RestTemplate restTemplate;

    public String getValue(String key) {
        SystemParameter systemParameter = parameterMapper.selectByPrimaryKey(key);
        if (systemParameter == null) {
            return null;
        }
        return systemParameter.getParamValue();
    }

    public List<SystemParameter> getParamList(String type) {
        SystemParameterExample example = new SystemParameterExample();
        example.createCriteria().andParamKeyLike(type + "%");
        return parameterMapper.selectByExample(example);
    }

    public Object keyCloakInfo(String type) {
        String address = environment.getProperty(ParamConstants.KeyCloak.AUTH_SERVER_URL.getValue());
        List<SystemParameter> paramList = this.getParamList(type);
        for (SystemParameter parameter : paramList) {
            if (StringUtils.equalsIgnoreCase(ParamConstants.Type.PASSWORD.getValue(), parameter.getType())) {
                parameter.setParamValue(EncryptUtils.aesDecrypt(parameter.getParamValue()).toString());
            }
        }
        paramList.sort(Comparator.comparing(SystemParameter::getParamKey).reversed());
        SystemParameter parameter = new SystemParameter();
        parameter.setParamKey(ParamConstants.KeyCloak.ADDRESS.getValue());
        parameter.setParamValue(address);
        paramList.add(parameter);
        return paramList;
    }

    public void editKeyCloakInfo(List<SystemParameter> parameters) {
        for (SystemParameter parameter : parameters) {
            if (StringUtils.equalsIgnoreCase(ParamConstants.Type.PASSWORD.getValue(), parameter.getType())) {
                String string = EncryptUtils.aesEncrypt(parameter.getParamValue()).toString();
                parameter.setParamValue(string);
            }
            parameterMapper.updateByPrimaryKey(parameter);
        }
    }

    public Object uiInfo(String type) throws InvocationTargetException, IllegalAccessException {
        List<SystemParameter> paramList = this.getParamList(type);
        List<SystemParameterDTO> dtoList = new ArrayList<>();
        for (SystemParameter systemParameter : paramList) {
            SystemParameterDTO systemParameterDTO = new SystemParameterDTO();
            BeanUtils.copyBean(systemParameterDTO, systemParameter);
            if (systemParameter.getType().equalsIgnoreCase("file")) {
                FileStore fileStore = fileStoreMapper.selectByPrimaryKey(systemParameterDTO.getParamValue());
                if (fileStore != null) {
                    systemParameterDTO.setFileName(fileStore.getName());
                }
            }
            dtoList.add(systemParameterDTO);
        }
        dtoList.sort(Comparator.comparingInt(SystemParameter::getSort));
        return dtoList;
    }

    public Object messageInfo(String type) {
        List<SystemParameter> paramList = new ArrayList<>();
        switch (type) {
            case "mail":
                paramList = this.getParamList(ParamConstants.Classify.MAIL.getValue());
                break;
            case "wechat":
                paramList = this.getParamList(ParamConstants.Classify.WECHAT.getValue());
                break;
            case "dingtalk":
                paramList = this.getParamList(ParamConstants.Classify.DINGTALK.getValue());
                break;
            default:
                break;
        }
        if (CollectionUtils.isEmpty(paramList)) {
            paramList = new ArrayList<>();
            if (type.equalsIgnoreCase("mail")) {
                ParamConstants.MAIL[] values = ParamConstants.MAIL.values();
                for (ParamConstants.MAIL value : values) {
                    SystemParameter systemParameter = new SystemParameter();
                    if (value.equals(ParamConstants.MAIL.PASSWORD)) {
                        systemParameter.setType(ParamConstants.Type.PASSWORD.getValue());
                    } else {
                        systemParameter.setType(ParamConstants.Type.TEXT.getValue());
                    }
                    systemParameter.setParamKey(value.getKey());
                    systemParameter.setSort(value.getValue());
                    paramList.add(systemParameter);
                }
            }
            if (type.equalsIgnoreCase(ParamConstants.Classify.WECHAT.getValue())) {
                ParamConstants.WECHAT[] values = ParamConstants.WECHAT.values();
                for (ParamConstants.WECHAT value : values) {
                    SystemParameter systemParameter = new SystemParameter();
                    systemParameter.setType(ParamConstants.Type.TEXT.getValue());
                    systemParameter.setParamKey(value.getKey());
                    systemParameter.setSort(value.getValue());
                    paramList.add(systemParameter);
                }
            }
            if (type.equalsIgnoreCase(ParamConstants.Classify.DINGTALK.getValue())) {
                ParamConstants.DINGTALK[] values = ParamConstants.DINGTALK.values();
                for (ParamConstants.DINGTALK value : values) {
                    SystemParameter systemParameter = new SystemParameter();
                    systemParameter.setType(ParamConstants.Type.TEXT.getValue());
                    systemParameter.setParamKey(value.getKey());
                    systemParameter.setSort(value.getValue());
                    paramList.add(systemParameter);
                }
            }
        } else {
            paramList.stream().filter(param -> param.getParamKey().equals(ParamConstants.MAIL.PASSWORD.getKey())).forEach(param -> {
                String string = EncryptUtils.aesDecrypt(param.getParamValue()).toString();
                param.setParamValue(string);
            });
        }
        paramList.sort(Comparator.comparingInt(SystemParameter::getSort));
        return paramList;
    }

    public void editMessageInfo(List<SystemParameter> parameters, String type) {
        List<SystemParameter> paramList = new ArrayList<>();
        switch (type) {
            case "mail":
                paramList = this.getParamList(ParamConstants.Classify.MAIL.getValue());
                break;
            case "wechat":
                paramList = this.getParamList(ParamConstants.Classify.WECHAT.getValue());
                break;
            case "dingtalk":
                paramList = this.getParamList(ParamConstants.Classify.DINGTALK.getValue());
                break;
            default:
                break;
        }
        boolean empty = paramList.size() < 2;
        parameters.forEach(parameter -> {
            if (parameter.getParamKey().equals(ParamConstants.MAIL.PASSWORD.getKey())) {
                String string = EncryptUtils.aesEncrypt(StringUtils.isNotBlank(parameter.getParamValue()) ? parameter.getParamValue() : "").toString();
                parameter.setParamValue(string);
            }
            if (empty) {
                parameterMapper.insert(parameter);
            } else {
                parameterMapper.updateByPrimaryKey(parameter);
            }
        });
    }

    public void editMailInfoAble(SystemParameter systemParameter) {
        List<SystemParameter> paramList = this.getParamList(ParamConstants.Classify.MAIL.getValue());
        if (paramList.size() > 1) {
            parameterMapper.updateByPrimaryKey(systemParameter);
        } else {
            F2CException.throwException(Translator.get("i18n_ex_ui_param"));
        }
    }

    public void testConnection(HashMap<String, String> hashMap) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setDefaultEncoding("UTF-8");
        javaMailSender.setHost(hashMap.get(ParamConstants.MAIL.SERVER.getKey()));
        javaMailSender.setPort(Integer.valueOf(hashMap.get(ParamConstants.MAIL.PORT.getKey())));
        javaMailSender.setUsername(hashMap.get(ParamConstants.MAIL.ACCOUNT.getKey()));
        javaMailSender.setPassword(hashMap.get(ParamConstants.MAIL.PASSWORD.getKey()));
        Properties props = new Properties();
        boolean isAnon = Boolean.parseBoolean(hashMap.get(ParamConstants.MAIL.ANON.getKey()));
        if (isAnon) {
            props.put("mail.smtp.auth", "false");
            javaMailSender.setUsername(null);
            javaMailSender.setPassword(null);
        } else {
            props.put("mail.smtp.auth", "true");
        }
        if (BooleanUtils.toBoolean(hashMap.get(ParamConstants.MAIL.SSL.getKey()))) {
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        if (BooleanUtils.toBoolean(hashMap.get(ParamConstants.MAIL.TLS.getKey()))) {
            props.put("mail.smtp.starttls.enable", "true");
        }
        props.put("mail.smtp.timeout", "30000");
        props.put("mail.smtp.connectiontimeout", "30000");
        javaMailSender.setJavaMailProperties(props);
        try {
            javaMailSender.testConnection();
        } catch (MessagingException e) {
            F2CException.throwException(e.getMessage());
        }
    }

    public void testWechat(HashMap<String, String> hashMap) {
        String cropId = hashMap.get(ParamConstants.WECHAT.CROPID.getKey());
        String secret = hashMap.get(ParamConstants.WECHAT.SECRET.getKey());
        Map<String, String> params = new HashMap<>();
        params.put("corpid", cropId);
        params.put("corpsecret", secret);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={corpid}&corpsecret={corpsecret}", String.class, params);
        if (responseEntity.getStatusCodeValue() != 200) {
            F2CException.throwException("request failed.");
        }
        String body = responseEntity.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        if (jsonObject.getIntValue("errcode") != 0) {
            F2CException.throwException(jsonObject.getString("errmsg"));
        }
        String token = jsonObject.getString("access_token");
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token;
        String agentId = hashMap.get(ParamConstants.WECHAT.AGENTID.getKey());
        long l = 0;
        try {
            l = Long.parseLong(agentId);
        } catch (Exception e) {
            F2CException.throwException("agentId is not valid.");
        }
        Map<String, Object> paramsStr = new HashMap<>();
        Map<String, String> contentStr = new HashMap<>();
        contentStr.put("content", "????????????CloudExplorer????????????????????????");
        paramsStr.put("msgtype", "text");
        paramsStr.put("agentid", l);
        paramsStr.put("text", contentStr);
        paramsStr.put("touser", hashMap.get(ParamConstants.WECHAT.TESTUSER.getKey()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(paramsStr, headers);

        ResponseEntity<NotificationBasicResponse> responseResponseEntity = restTemplate.postForEntity(url, request, NotificationBasicResponse.class);
        if (responseResponseEntity.getBody().getErrcode() != 0) {
            F2CException.throwException(responseResponseEntity.getBody().getErrmsg());
        }
    }

    public void testDingtalk(HashMap<String, String> hashMap) throws Exception {
        String ak = hashMap.get(ParamConstants.DINGTALK.APPKEY.getKey());
        String sk = hashMap.get(ParamConstants.DINGTALK.APPSECRET.getKey());
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey(ak);
        request.setAppsecret(sk);
        request.setHttpMethod("GET");
        OapiGettokenResponse response = client.execute(request);
        if (response.getErrcode() != 0) {
            F2CException.throwException(response.getErrmsg());
        }
        String token = response.getAccessToken();

        DingTalkClient client1 = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get_by_mobile");
        OapiUserGetByMobileRequest request1 = new OapiUserGetByMobileRequest();
        request1.setMobile(hashMap.get(ParamConstants.DINGTALK.TESTUSER.getKey()));
        OapiUserGetByMobileResponse execute = client1.execute(request1, token);
        if (execute.getErrcode() != 0) {
            F2CException.throwException(execute.getErrmsg());
        }
        String userId = execute.getUserid();
        String agentId = hashMap.get(ParamConstants.DINGTALK.AGENTID.getKey());

        DingTalkClient client2 = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request request2 = new OapiMessageCorpconversationAsyncsendV2Request();
        request2.setUseridList(userId);
        request2.setAgentId(Long.valueOf(agentId));
        request2.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
        msg.getText().setContent("????????????CloudExplorer????????????????????????");
        request2.setMsg(msg);
        OapiMessageCorpconversationAsyncsendV2Response execute1 = client2.execute(request2, token);
        if (execute1.getErrcode() != 0) {
            F2CException.throwException(execute1.getErrmsg());
        }
    }

    public void saveValue(SystemParameter systemParameter) {
        if (parameterMapper.updateByPrimaryKeySelective(systemParameter) == 0) {
            parameterMapper.insertSelective(systemParameter);
        }
    }

    public void editUiInfo(MultipartFile[] files, String parameter) throws IOException {
        List<SystemParameterDTO> systemParameters = JSON.parseArray(parameter, SystemParameterDTO.class);
        for (MultipartFile multipartFile : files) {
            if (!multipartFile.isEmpty()) {
                //???????????????????????????
                try (InputStream input = multipartFile.getInputStream()) {
                    try {
                        // It's an image (only BMP, GIF, JPG and PNG are recognized).
                        ImageIO.read(input).toString();
                    } catch (Exception e) {
                        F2CException.throwException("Uploaded images do not meet the image format requirements");
                        return;
                    }
                }
                String multipartFileName = multipartFile.getOriginalFilename();
                String[] split = Objects.requireNonNull(multipartFileName).split(",");
                systemParameters.stream().filter(systemParameterDTO -> systemParameterDTO.getParamKey().equalsIgnoreCase(split[1])).forEach(systemParameterDTO -> {
                    systemParameterDTO.setFileName(split[0]);
                    systemParameterDTO.setFile(multipartFile);
                });
            }
        }
        for (SystemParameterDTO systemParameter : systemParameters) {
            MultipartFile file = systemParameter.getFile();
            if (systemParameter.getType().equalsIgnoreCase("file")) {
                if (StringUtils.isBlank(systemParameter.getFileName())) {
                    fileStoreMapper.deleteByPrimaryKey(systemParameter.getParamValue());
                }
                if (file != null) {
                    fileStoreMapper.deleteByPrimaryKey(systemParameter.getParamValue());
                    String uuid = UUIDUtil.newUUID();
                    FileStore fileStore = new FileStore();
                    fileStore.setId(uuid);
                    fileStore.setName(systemParameter.getFileName());
                    fileStore.setFile(file.getBytes());
                    fileStore.setSize(file.getSize());
                    fileStore.setCreateTime(Instant.now().toEpochMilli());
                    fileStoreMapper.insert(fileStore);
                    systemParameter.setParamValue(uuid);
                }
                if (file == null && systemParameter.getFileName() == null) {
                    systemParameter.setParamValue(null);
                }
            }
            parameterMapper.deleteByPrimaryKey(systemParameter.getParamKey());
            parameterMapper.insert(systemParameter);
        }

    }
}
