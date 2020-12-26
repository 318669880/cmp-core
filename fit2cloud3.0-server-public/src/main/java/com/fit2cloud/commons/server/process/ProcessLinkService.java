package com.fit2cloud.commons.server.process;

import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.*;
import com.fit2cloud.commons.server.base.mapper.ext.ExtFlowLinkValueScopeMapper;
import com.fit2cloud.commons.server.constants.ProcessConstants;
import com.fit2cloud.commons.server.constants.ResourceTypeConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.LinkValueDTO;
import com.fit2cloud.commons.server.model.OrgTreeNode;
import com.fit2cloud.commons.server.model.TreeNode;
import com.fit2cloud.commons.server.service.OperationLogService;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fit2cloud.commons.server.constants.ResourceTypeConstants.ORGANIZATION;
import static com.fit2cloud.commons.server.constants.ResourceTypeConstants.WORKSPACE;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessLinkService {

    @Value("${spring.application.name:null}")
    private String moduleId;
    @Resource
    private FlowLinkValueScopeMapper flowLinkValueScopeMapper;
    @Resource
    private FlowLinkMapper flowLinkMapper;
    @Resource
    private FlowLinkValueMapper flowLinkValueMapper;
    @Resource
    private UserCommonService userCommonService;
    @Resource
    private OrganizationMapper organizationMapper;
    @Resource
    private ExtFlowLinkValueScopeMapper extFlowLinkValueScopeMapper;

    public List<FlowLink> listProcessLinks(Map<String, String> request) {
        FlowLinkExample example = new FlowLinkExample();
        example.createCriteria().andModuleEqualTo(moduleId);
        if(request != null && request.get("sql") != null){
            example.setOrderByClause(request.get("sql"));
        }
        return flowLinkMapper.selectByExample(example);
    }

    public int deleteLink(String linkId) {
        FlowLink flowLink = flowLinkMapper.selectByPrimaryKey(linkId);
        if(flowLink == null){
            return 0;
        }
        List<FlowLinkValue>  flowLinkValues = listProcessLinkValues(flowLink.getLinkKey());
        flowLinkValues.forEach(flowLinkValue ->
            deleteLinkValue(flowLinkValue.getId())
        );
        return flowLinkMapper.deleteByPrimaryKey(linkId);
    }

    public FlowLink findFlowLinkByLinkKey(String linkKey){
        FlowLinkExample example = new FlowLinkExample();
        example.createCriteria().andModuleEqualTo(moduleId).andLinkKeyEqualTo(linkKey);
        List<FlowLink>  flowLinks =flowLinkMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(flowLinks)){
            return flowLinks.get(0);
        }else {
            return null;
        }
    }
    public int addLink(FlowLink link) {
        FlowLinkExample example = new FlowLinkExample();
        example.createCriteria().andModuleEqualTo(moduleId).andLinkKeyEqualTo(link.getLinkKey());
        List<FlowLink> flowLinks = flowLinkMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(flowLinks)) {
            throw new RuntimeException(Translator.get("i18n_link_id_exist"));
        }
        link.setModule(moduleId);
        link.setLinkId(UUIDUtil.newUUID());
        link.setCreateTime(System.currentTimeMillis());
        link.setEnable(true);
        return flowLinkMapper.insertSelective(link);
    }

    public int updateLink(FlowLink link) {
        link.setCreateTime(System.currentTimeMillis());
        return flowLinkMapper.updateByPrimaryKeySelective(link);
    }


    public List<FlowLinkValue> listProcessLinkValues(String linkKey) {
        FlowLinkValueExample example = new FlowLinkValueExample();
        example.createCriteria().andModuleEqualTo(moduleId).andLinkKeyEqualTo(linkKey);
        return flowLinkValueMapper.selectByExample(example);
    }

    public List<FlowLinkValue> listProcessLinkValuesByRole(String processRoleKey) {
        FlowLinkValueExample example = new FlowLinkValueExample();
        example.createCriteria().andModuleEqualTo(moduleId).andAssigneeTypeEqualTo(ProcessConstants.AssigneeType.PROCESS_ROLE.name()).andAssigneeValueEqualTo(processRoleKey);
        return flowLinkValueMapper.selectByExample(example);
    }

    public int deleteLinkValue(String linkValueId) {
        deleteLinkValueScopes(linkValueId);
        return flowLinkValueMapper.deleteByPrimaryKey(linkValueId);
    }

    public void addLinkValue(LinkValueDTO linkValue) {
        FlowLinkValueExample example = new FlowLinkValueExample();
        example.createCriteria().andModuleEqualTo(moduleId).andLinkKeyEqualTo(linkValue.getLinkKey()).andLinkValueEqualTo(linkValue.getLinkValue());
        List<FlowLinkValue> linkValues = flowLinkValueMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(linkValues)) {
            throw new RuntimeException(Translator.get("i18n_link_value_exist"));
        }
        example.clear();
        example.createCriteria().andModuleEqualTo(moduleId).andLinkKeyEqualTo(linkValue.getLinkKey()).andLinkValuePriorityEqualTo(linkValue.getLinkValuePriority());
        List<FlowLinkValue> linkValuePrioritys = flowLinkValueMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(linkValuePrioritys)) {
            throw new RuntimeException(Translator.get("i18n_link_value_priority_exist"));
        }
        linkValue.setModule(moduleId);
        linkValue.setId(UUIDUtil.newUUID());
        flowLinkValueMapper.insertSelective(linkValue);
        updateLinkValueWorkspaceTree(linkValue.getId(), linkValue);
    }

    public int updateLinkValue(FlowLinkValue flowLinkValue) {
        FlowLinkValueExample example = new FlowLinkValueExample();
        example.createCriteria().andModuleEqualTo(moduleId).andLinkKeyEqualTo(flowLinkValue.getLinkKey()).andLinkValuePriorityEqualTo(flowLinkValue.getLinkValuePriority())
        .andIdNotEqualTo(flowLinkValue.getId());
        List<FlowLinkValue> linkValuePrioritys = flowLinkValueMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(linkValuePrioritys)) {
            throw new RuntimeException(Translator.get("i18n_link_value_priority_exist"));
        }
        return flowLinkValueMapper.updateByPrimaryKeySelective(flowLinkValue);
    }

    public List<TreeNode> getLinkValueWorkspaceTree(String id) {
        List<FlowLinkValueScope> flowLinkValueScopes = getLinkValueWorkspaces(id);
        return getWorkspaceTree(flowLinkValueScopes);
    }

    private void deleteLinkValueScopes(String flowLinkValueId) {
        FlowLinkValueScopeExample flowLinkValueScopeExample = new FlowLinkValueScopeExample();
        flowLinkValueScopeExample.createCriteria().andLinkValueIdEqualTo(flowLinkValueId);
        flowLinkValueScopeMapper.deleteByExample(flowLinkValueScopeExample);
    }

    public void updateLinkValueWorkspaceTree(String flowLinkValueId, LinkValueDTO linkValueDTO) {
        FlowLinkValue flowLinkValue = flowLinkValueMapper.selectByPrimaryKey(flowLinkValueId);
        if (flowLinkValue == null) {
            F2CException.throwException(Translator.get("i18n_link_item_not_exist") + flowLinkValueId);
        }
        flowLinkValue.setPermissionMode(linkValueDTO.getPermissionMode());
        updateLinkValue(flowLinkValue);
        deleteLinkValueScopes(flowLinkValueId);
        if (org.springframework.util.CollectionUtils.isEmpty(linkValueDTO.getTreeNodes())) {
            return;
        }


        List<FlowLinkValueScope> flowLinkValueScopes = new Vector<>();
        List<TreeNode> treeNodes = linkValueDTO.getTreeNodes();

        while (!CollectionUtils.isEmpty(treeNodes)){
            List<TreeNode> tempLists = new ArrayList<>();
            treeNodes.parallelStream().forEach(treeNode -> {
                if (treeNode.isChecked()) {
                    flowLinkValueScopes.add(new FlowLinkValueScope() {{
                        setId(UUIDUtil.newUUID());
                        setWorkspaceId(treeNode.getId());
                        setLinkValueId(flowLinkValueId);
                        setType(treeNode.getType().toUpperCase());
                    }});
                }
                if (!org.springframework.util.CollectionUtils.isEmpty(treeNode.getChildren())){
                    tempLists.addAll(treeNode.getChildren());
                }
            });
            treeNodes = tempLists;
        }

        flowLinkValueScopes.forEach(flowLinkValueScope -> flowLinkValueScope.setModule(moduleId));
        extFlowLinkValueScopeMapper.batchInsert(flowLinkValueScopes);
    }


    public List<FlowLinkValueScope> getLinkValueWorkspaces(String linkValueId) {
        FlowLinkValueScopeExample flowLinkValueScopeExample = new FlowLinkValueScopeExample();
        flowLinkValueScopeExample.createCriteria().andLinkValueIdEqualTo(linkValueId).andModuleEqualTo(moduleId);
        return flowLinkValueScopeMapper.selectByExample(flowLinkValueScopeExample);
    }

    private List<TreeNode> getWorkspaceTree(List<FlowLinkValueScope> FlowLinkValueScopes) {
        Map<String, Map<String, List<FlowLinkValueScope>>> FlowLinkValueScopeMap = FlowLinkValueScopes.parallelStream()
                .collect(Collectors.groupingBy(FlowLinkValueScope::getType, Collectors.groupingBy(FlowLinkValueScope::getWorkspaceId)));

        return getWorkspaceTree(
                (organizationId) -> FlowLinkValueScopeMap.containsKey(ORGANIZATION.toString()) && FlowLinkValueScopeMap.get(ORGANIZATION.toString()).containsKey(organizationId),
                (workspaceId) -> FlowLinkValueScopeMap.containsKey(WORKSPACE.toString()) && FlowLinkValueScopeMap.get(WORKSPACE.toString()).containsKey(workspaceId));
    }

    //organizationCheckFunc: 参数是组织ID，返回该组织是否被选中
    //workspaceCheckFunc: 参数是工作空间ID，返回该工作空间是否被选中
    List<TreeNode> getWorkspaceTree(Function<String, Boolean> organizationCheckFunc, Function<String, Boolean> workspaceCheckFunc) {
        List<OrgTreeNode> orgTreeNodes = userCommonService.orgTreeSelect(null, false);
        List<TreeNode> result = orgTreeNodes.stream().map(node -> buildTreeNode(node, organizationCheckFunc, workspaceCheckFunc)).collect(Collectors.toList());

        return result.parallelStream().sorted(Comparator.comparing(TreeNode::getName)).collect(Collectors.toList());
    }

    private TreeNode buildTreeNode(OrgTreeNode orgTreeNode , Function<String, Boolean> organizationCheckFunc, Function<String, Boolean> workspaceCheckFunc){
        TreeNode treeNode = new TreeNode();
        treeNode.setId(orgTreeNode.getNodeId());
        treeNode.setName(orgTreeNode.getNodeName());

        if (StringUtils.equals(orgTreeNode.getNodeType(), "wks")){
            treeNode.setChecked(workspaceCheckFunc.apply(treeNode.getId()));
            treeNode.setType(ResourceTypeConstants.WORKSPACE.name());
        }
        if (StringUtils.equals(orgTreeNode.getNodeType(), "org")){
            treeNode.setChecked(organizationCheckFunc.apply(treeNode.getId()));
            treeNode.setType(ResourceTypeConstants.ORGANIZATION.name());
        }
        if (!org.springframework.util.CollectionUtils.isEmpty(orgTreeNode.getChildNodes())){
            treeNode.setChildren(orgTreeNode.getChildNodes().stream().map(node -> buildTreeNode(node, organizationCheckFunc, workspaceCheckFunc)).collect(Collectors.toList()));
        }
        return treeNode;
    }

}
