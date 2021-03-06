package com.fit2cloud.commons.server.process;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.FlowNotificationConfigMapper;
import com.fit2cloud.commons.server.base.mapper.FlowNotificationInboxMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtFlowMapper;
import com.fit2cloud.commons.server.constants.ProcessConstants;
import com.fit2cloud.commons.server.model.UserNotificationSettingDTO;
import com.fit2cloud.commons.server.service.*;
import com.fit2cloud.commons.server.utils.ProcessText;
import com.fit2cloud.commons.utils.CommonThreadPool;
import com.fit2cloud.commons.utils.LogUtil;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessMessageService {

    @Value("${spring.application.name:null}")
    private String moduleId;

    @Resource
    private CommonThreadPool processThreadPool;

    @Resource
    private ExtFlowMapper extFlowMapper;

    @Resource
    @Lazy
    private ProcessModelService processModelService;

    @Resource
    private ProcessService processService;

    @Resource
    private FlowNotificationConfigMapper flowNotificationConfigMapper;

    @Resource
    private MailService mailService;

    @Resource
    private UserCommonService userCommonService;

    @Resource
    private NotificationService notificationService;
    @Resource
    private DingtalkService dingtalkService;
    @Resource
    private WechatService wechatService;
    @Resource
    private UserNotificationService userNotificationService;

    public List<FlowNotificationConfig> listConfig(String modelId, int step, String activityId) {
        if (step == -1) return listConfig(modelId, step);
        return listConfig(modelId, activityId);
    }

    private List<FlowNotificationConfig> listConfig(String modelId, int step) {
        FlowNotificationConfigExample example = new FlowNotificationConfigExample();
        example.createCriteria().andModelIdEqualTo(modelId).andStepEqualTo(step).andModuleEqualTo(moduleId);
        return flowNotificationConfigMapper.selectByExampleWithBLOBs(example);
    }

    private List<FlowNotificationConfig> listConfig(String modelId, String activityId) {
        FlowNotificationConfigExample example = new FlowNotificationConfigExample();
        example.createCriteria().andModelIdEqualTo(modelId).andActivityIdEqualTo(activityId).andModuleEqualTo(moduleId);
        return flowNotificationConfigMapper.selectByExampleWithBLOBs(example);
    }

    public int addConfig(FlowNotificationConfig config) {
        config.setModule(moduleId);
        return flowNotificationConfigMapper.insert(config);
    }

    public int updateConfig(FlowNotificationConfig config) {
        return flowNotificationConfigMapper.updateByPrimaryKeySelective(config);
    }

    public void copyNotificationConfig(String oldId, String newId) {
        FlowNotificationConfigExample example = new FlowNotificationConfigExample();
        example.createCriteria().andModelIdEqualTo(oldId);
        List<FlowNotificationConfig> list = flowNotificationConfigMapper.selectByExampleWithBLOBs(example);
        list.forEach(flowNotificationConfig -> {
            flowNotificationConfig.setId(null);
            flowNotificationConfig.setModelId(newId);
            flowNotificationConfigMapper.insert(flowNotificationConfig);
        });
    }

    public int deleteConfig(int id) {
        return flowNotificationConfigMapper.deleteByPrimaryKey(id);
    }

    void deleteConfig(String modelId) {
        FlowNotificationConfigExample configExample = new FlowNotificationConfigExample();
        configExample.createCriteria().andModelIdEqualTo(modelId);
        flowNotificationConfigMapper.deleteByExample(configExample);
    }

    @Resource
    private FlowNotificationInboxMapper inboxMapper;

    // ??????????????????
    public void sendTaskMessage(String modelId, FlowTask task, String operation, Map<String, Object> params) {
        sendTaskMessage(modelId, task, operation, params, null);
    }

    public void sendProcessMessage(String modelId, String operation, Map<String, Object> params) {
        sendProcessMessage(modelId, operation, params, null);
    }

    public void sendProcessMessage(String businessKey, ProcessConstants.MessageOperation operation, Map<String, Object> params) {
        sendProcessMessage(businessKey, operation, params, null);
    }

    public void sendTaskMessage(String modelId, FlowTask task, String operation, Map<String, Object> params, Consumer<String> action) {
        notice(modelId, task.getTaskActivity(), ProcessConstants.MessageProcessType.TASK.name(), operation, params, action);
    }

    public void sendProcessMessage(String modelId, String operation, Map<String, Object> params, Consumer<String> action) {
        notice(modelId, null, ProcessConstants.MessageProcessType.PROCESS.name(), operation, params, action);
    }

    public void sendProcessMessage(String businessKey, ProcessConstants.MessageOperation operation, Map<String, Object> params, Consumer<String> action) {
        FlowProcess process = extFlowMapper.getProcessByBusinessKey(businessKey);
        // ????????????????????????????????????????????????????????????????????????process???null
        if (process == null) {
            return;
        }
        processService.getParameters(process, params);
        String modelId = processModelService.getModelIdByDeployId(process.getDeployId());
        notice(modelId, null, ProcessConstants.MessageProcessType.PROCESS.name(), operation.name(), params, action);
    }

    private void notice(String modelId, String activityId, String processType, String operation, Map<String, Object> params, Consumer<String> action) {
        processThreadPool.addTask(() -> {
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("modelId", modelId);
                map.put("activityId", activityId);
                map.put("operation", operation);
                map.put("processType", processType);
                LogUtil.debug("Query mail configuration " + map.toString());
                List<FlowNotificationConfig> configs = extFlowMapper.listNotificationConfig(map);
                LogUtil.debug("Number of mail configurations " + configs.size());
                for (FlowNotificationConfig config : configs) {
                    sendMessage(config, params, action);
                }
            } catch (Exception e) {
                LogUtil.error("Send process mail error:", e);
            }
        });
    }

    private void sendMessage(FlowNotificationConfig config, Map<String, Object> params, Consumer<String> action) {
        String title = null;
        String[] receivers = new String[0];
        String content = null;
        try {
            JSONObject template = JSONObject.parseObject(config.getTemplate());
            title = template.getString("title");
            receivers = ProcessEmail.getReceivers(template, params);

            for (String receiver : receivers) {
                // ??????????????????
                if (action != null) action.accept(receiver);

                // html content to send email
                content = ProcessEmail.getContent(config.getId(), template, params);
                // simple content to push dingtalk or wechat
                String simpleContent = ProcessText.parseText(config, params);
                simpleContent = title + "\n\n" + simpleContent;

                UserNotificationSettingDTO userNotification = userNotificationService.getUserNotification(receiver);
                // ????????????
                if (StringUtils.contains(config.getSmsType(), ProcessConstants.SmsType.ANNOUNCEMENT.name())) {
                    notificationService.sendAnnouncement(title, content, receiver);
                }
                // ????????????
                if (StringUtils.contains(config.getSmsType(), ProcessConstants.SmsType.EMAIL.name())) {
                    LogUtil.info("Send mail???" + receiver);
                    String email = receiver;
                    User user = userCommonService.getUserById(receiver);
                    if (user != null && StringUtils.isNotBlank(user.getEmail())) {
                        email = user.getEmail();
                    }
                    if (!StringUtils.containsIgnoreCase(email, "@")) {
                        continue;
                    }
                    mailService.sendHtmlEmail(title, content, email);
                    saveMailLog(receiver, title, content, config, ProcessConstants.MessageStatus.SUCCESS.name());
                    LogUtil.info("Successfully sent mail???title: " + title);
                }
                // ????????????
                if (StringUtils.contains(config.getSmsType(), ProcessConstants.SmsType.DINGTALK.name()) && StringUtils.isNotEmpty(userNotification.getPhone())) {
                    try {
                        LogUtil.info("Send dingtalk???" + userNotification.getPhone());
                        dingtalkService.sendTextMessageToUser(simpleContent, userNotification.getPhone());
                        saveMailLog(receiver, title, simpleContent, config, ProcessConstants.MessageStatus.SUCCESS.name());
                        LogUtil.info("Successfully sent dingtalk:" + simpleContent);
                    } catch (Exception e) {
                        LogUtil.error("Send Dingtalk error:", e);
                    }
                }
                // ??????????????????
                if (StringUtils.contains(config.getSmsType(), ProcessConstants.SmsType.WECHAT.name()) && StringUtils.isNotEmpty(userNotification.getWechatAccount())) {
                    try {
                        LogUtil.info("Send wechat???" + userNotification.getWechatAccount());
                        wechatService.sendTextMessageToUser(simpleContent, userNotification.getWechatAccount());
                        saveMailLog(receiver, title, simpleContent, config, ProcessConstants.MessageStatus.SUCCESS.name());
                        LogUtil.info("Successfully sent wechat work:" + simpleContent);
                    } catch (Exception e) {
                        LogUtil.error("Send Wechat error:", e);
                    }
                }
            }
        } catch (Exception e) {
            for (String receiver : receivers) {
                saveMailLog(receiver, title, content, config, ProcessConstants.MessageStatus.ERROR.name());
            }
            LogUtil.error("Send Message error:", e);
        }
    }

    private void saveMailLog(String receiver, String title, String content, FlowNotificationConfig config, String status) {
        try {
            FlowNotificationInbox notificationInbox = new FlowNotificationInbox();
            notificationInbox.setReceiver(receiver);
            notificationInbox.setProcessType(config.getProcessType());
            notificationInbox.setSmsType(config.getSmsType());
            notificationInbox.setReceiveTime(System.currentTimeMillis());
            notificationInbox.setStatus(status);
            notificationInbox.setTitle(title);
            notificationInbox.setContent(content);
            inboxMapper.insert(notificationInbox);
        } catch (Exception e) {
            LogUtil.error("Message log save error", e);
        }
    }
}
