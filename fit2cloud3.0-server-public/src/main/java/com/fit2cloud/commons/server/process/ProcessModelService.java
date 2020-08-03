package com.fit2cloud.commons.server.process;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.FlowDeployMapper;
import com.fit2cloud.commons.server.base.mapper.FlowModelMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtFlowMapper;
import com.fit2cloud.commons.server.constants.ProcessConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.process.dto.ActivityDTO;
import com.fit2cloud.commons.server.process.dto.ProcessModelDTO;
import com.fit2cloud.commons.server.process.dto.UserDTO;
import com.fit2cloud.commons.server.utils.SessionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessModelService {

    @Value("${spring.application.name:null}")
    private String moduleId;

    @Resource
    private FlowModelMapper flowModelMapper;

    @Resource
    private FlowDeployMapper flowDeployMapper;

    @Resource
    private ExtFlowMapper extFlowMapper;

    @Resource
    private ProcessEventService processEventService;

    @Resource
    private ProcessMessageService processMessageService;

    @Resource
    private ProcessLinkService processLinkService;

    // 2020-1-21以后可以删除这个功能
    public void fixedHistoryData(String modelId) {
        Map<String, Object> map = new HashMap<>();
        map.put("modelId", modelId);
        map.put("processType", ProcessConstants.MessageProcessType.TASK.name());
        List<FlowNotificationConfig> list = extFlowMapper.listNotificationConfig(map);
        long count = list.stream().filter(config -> StringUtils.isBlank(config.getActivityId())).count();
        if (count > 0) {
            FlowModel flowModel = flowModelMapper.selectByPrimaryKey(modelId);
            ProcessModelDTO model = JSONObject.parseObject(flowModel.getModelContent(), ProcessModelDTO.class);
            model.getActivities().sort(Comparator.comparing(ActivityDTO::getStep));
            list.stream().filter(config -> StringUtils.isBlank(config.getActivityId())).forEach(config -> {
                config.setActivityId(model.getActivities().get(config.getStep()).getActivityId());
                processMessageService.updateConfig(config);
            });
        }
    }

    public List<FlowModel> listModel() {
        FlowModelExample example = new FlowModelExample();
        example.createCriteria().andModuleEqualTo(moduleId);
        return flowModelMapper.selectByExample(example);
    }

    public FlowModel getModelById(String modelId) {
        FlowModel flowModel = flowModelMapper.selectByPrimaryKey(modelId);
        ProcessModelDTO model = JSONObject.parseObject(flowModel.getModelContent(), ProcessModelDTO.class);
        for (ActivityDTO activity : model.getActivities()) {
            if(StringUtils.isEmpty(activity.getLinkType())){
                activity.setLinkType(ProcessConstants.ModelLinkType.CUSTOMIZE.toString());
            }
        }
        flowModel.setModelContent(JSONObject.toJSONString(model));
        return flowModel;
    }

    public List<Map<String, Object>> getModelsByFlowLink(List<String> linkKeys){
        List<Map<String, Object>> flowLinkModels = new ArrayList<>();
        FlowModelExample example = new FlowModelExample();
        example.createCriteria().andModuleEqualTo(moduleId);
        List<FlowModel> flowModels = flowModelMapper.selectByExampleWithBLOBs(example);
        for (String linkKey : linkKeys) {
            Map<String, Object> flowLinkModel = new HashMap<>();
            Integer modelCount = 0;
            String modelNames = "";
            for (FlowModel flowModel : flowModels) {
                ProcessModelDTO processModelDTO = JSONObject.parseObject(flowModel.getModelContent(), ProcessModelDTO.class);
                for (ActivityDTO activity : processModelDTO.getActivities()) {
                    if(StringUtils.isNotEmpty(activity.getLinkType()) && StringUtils.isNotEmpty(activity.getLinkKey()) &&  activity.getLinkKey().equalsIgnoreCase(linkKey)){
                        modelCount++;
                        if(StringUtils.isEmpty(modelNames)){
                            modelNames = Translator.get(flowModel.getModelName());
                        }else {
                            modelNames = modelNames + ", " + Translator.get(flowModel.getModelName());
                        }
                        break;
                    }
                }
            }

            flowLinkModel.put("linkKey", linkKey);
            flowLinkModel.put("modelCount", modelCount);
            flowLinkModel.put("modelNames", modelNames);
            flowLinkModels.add(flowLinkModel);
        }
        return flowLinkModels;
    }

    public int addModel(FlowModel model) {
        FlowModel exist = flowModelMapper.selectByPrimaryKey(model.getModelId());
        if (exist != null) {
            String msg = "Process model ID is already occupied";
            if (!StringUtils.equals(exist.getModule(), moduleId)) {
                msg = "The process model ID has been occupied by other modules, and the occupied module is: " + exist.getModule();
            }
            throw new RuntimeException(msg);
        }
        model.setModelTime(new Date());
        model.setModelVersion(System.currentTimeMillis());
        model.setModelCreator(SessionUtils.getUser().getEmail());
        model.setModule(moduleId);
        return flowModelMapper.insert(model);
    }

    public int updateModel(FlowModel model) {
        model.setModelTime(new Date());
        model.setModelVersion(System.currentTimeMillis());
        model.setModelCreator(SessionUtils.getUser().getEmail());
        model.setDeployId(null);
        return flowModelMapper.updateByPrimaryKeyWithBLOBs(model);
    }

    public int copyModel(String oldId, String newId) {
        FlowModel exist = flowModelMapper.selectByPrimaryKey(newId);
        if (exist != null) {
            F2CException.throwException("Model ID is not repeatable");
        }
        processEventService.copyEvent(oldId, newId);
        processMessageService.copyNotificationConfig(oldId, newId);
        FlowModel form = flowModelMapper.selectByPrimaryKey(oldId);
        form.setModelId(newId);
        form.setModelCreator(SessionUtils.getUser().getEmail());
        form.setModelVersion(System.currentTimeMillis());
        form.setDeployId(null);
        return flowModelMapper.insert(form);
    }

    public int deleteModel(String modelId) {
        processEventService.deleteEvent(modelId);
        processMessageService.deleteConfig(modelId);
        return flowModelMapper.deleteByPrimaryKey(modelId);
    }

    public int publishModel(String modelId) {
        FlowModel model = flowModelMapper.selectByPrimaryKey(modelId);
        if (StringUtils.isNotBlank(model.getDeployId())) {
            throw new RuntimeException("Model has been published.");
        }
        FlowDeploy deploy = deployModel(model);
        model.setDeployId(deploy.getDeployId());
        return flowModelMapper.updateByPrimaryKeySelective(model);
    }

    public FlowDeploy getLastVersionDeploy(String modelId) {
        return extFlowMapper.getLastVersionDeploy(modelId);
    }

    FlowDeploy deployModel(FlowModel model) {
        FlowDeploy deploy = new FlowDeploy();
        deploy.setDeployId(UUID.randomUUID().toString());
        deploy.setDeployContent(model.getModelContent());
        deploy.setDeployTime(System.currentTimeMillis());
        deploy.setDeployVersion(model.getModelVersion());
        deploy.setModelId(model.getModelId());
        flowDeployMapper.insert(deploy);
        return deploy;
    }

    public List<FlowDeploy> listDeployHistory(String modelId) {
        FlowDeployExample example = new FlowDeployExample();
        example.setOrderByClause("deploy_version desc");
        example.createCriteria().andModelIdEqualTo(modelId);
        return flowDeployMapper.selectByExample(example);
    }

    ProcessModelDTO getProcessModel(FlowDeploy deploy) {
        ProcessModelDTO model = JSONObject.parseObject(deploy.getDeployContent(), ProcessModelDTO.class);
        model.getActivities().sort(Comparator.comparing(ActivityDTO::getStep));
        return model;
    }

    String getModelIdByDeployId(String deployId) {
        FlowDeploy deploy = flowDeployMapper.selectByPrimaryKey(deployId);
        if (deploy != null) {
            return deploy.getModelId();
        }
        return null;
    }

    ProcessModelDTO getProcessModel(String processId) {
        FlowDeploy deploy = extFlowMapper.getDeployByProcessId(processId);
        return getProcessModel(deploy);
    }

    public List<UserDTO> listUser(Map<String, String> map) {
        String search = map.get("search");
        if (StringUtils.isNotBlank(search)) {
            search = StringUtils.wrapIfMissing(search, "%");
        }
        return extFlowMapper.listUser(search);
    }

    public List<UserDTO> listRoleUsers(String roleKey) {
        return extFlowMapper.listRoleUser(roleKey);
    }

    public List<Map<String, Object>> getRoleUsed(List<String> roleKeys){
        List<Map<String, Object>> processRoleUsed = new ArrayList<>();
        FlowModelExample example = new FlowModelExample();
        example.createCriteria().andModuleEqualTo(moduleId);
        List<FlowModel> flowModels = flowModelMapper.selectByExampleWithBLOBs(example);

        for (String roleKey : roleKeys) {
            Map<String, Object> roleKeyUsed = new HashMap<>();
            Boolean  linkUsed = false;
            String linkKeys = "";
            String models = "";
            roleKeyUsed.put("roleKey", roleKey);

            List<FlowLinkValue> flowLinkValues = processLinkService.listProcessLinkValuesByRole(roleKey);
            if(CollectionUtils.isNotEmpty(flowLinkValues)){
                for (FlowLinkValue flowLinkValue : flowLinkValues) {
                    if(StringUtils.isEmpty(linkKeys)){
                        linkKeys = flowLinkValue.getLinkKey();
                    }else {
                        linkKeys = linkKeys + ", " + flowLinkValue.getLinkKey();
                    }
                }
                linkUsed = true;
            }
            
            Boolean  modelUsed = false;
            for (FlowModel flowModel : flowModels) {
                ProcessModelDTO processModelDTO = JSONObject.parseObject(flowModel.getModelContent(), ProcessModelDTO.class);
                for (ActivityDTO activity : processModelDTO.getActivities()) {
                    if(StringUtils.isEmpty(activity.getLinkType()) || activity.getLinkType().equalsIgnoreCase(ProcessConstants.ModelLinkType.CUSTOMIZE.toString())){
                        if(activity.getAssigneeType().equalsIgnoreCase(ProcessConstants.AssigneeType.PROCESS_ROLE.name()) && activity.getAssignee().equalsIgnoreCase(roleKey)){
                            modelUsed = true;
                            if(StringUtils.isEmpty(models)){
                                models = Translator.get(flowModel.getModelName());
                            }else {
                                models = models + ", " + Translator.get(flowModel.getModelName());
                            }
                            break;
                        }
                    }
                }
                if(modelUsed){
                    break;
                }
            }
            roleKeyUsed.put("roleKey", roleKey);
            roleKeyUsed.put("used", linkUsed || modelUsed);
            roleKeyUsed.put("models", models);
            roleKeyUsed.put("linkKeys", linkKeys);
            processRoleUsed.add(roleKeyUsed);
        }
        return processRoleUsed;
    }

}
