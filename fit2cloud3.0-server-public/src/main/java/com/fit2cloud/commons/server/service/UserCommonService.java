package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.*;
import com.fit2cloud.commons.server.base.mapper.ext.ExtUserRoleMapper;
import com.fit2cloud.commons.server.constants.I18nConstants;
import com.fit2cloud.commons.server.constants.ParamConstants;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.constants.WebConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.OrgTreeNode;
import com.fit2cloud.commons.server.model.UserDTO;
import com.fit2cloud.commons.server.model.UserRoleDTO;
import com.fit2cloud.commons.server.model.UserRoleHelpDTO;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.server.utils.WorkspaceUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.EncryptUtils;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.fit2cloud.commons.server.utils.SessionUtils.getRequest;

/**
 * Author: chunxing
 * Date: 2018/5/9  上午10:25
 * Description:
 */
@Service
public class UserCommonService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private ExtUserRoleMapper extUserRoleMapper;
    @Resource
    private OrganizationMapper organizationMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RoleCommonService roleCommonService;
    @Resource
    private ParameterCommonService parameterCommonService;


    public User getUserById(String userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    public List<User> getUserByName(String userName) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andNameEqualTo(userName);
        return userMapper.selectByExample(userExample);
    }

    public String getUserName(String userId) {
        User user = getUserById(userId);
        if (user != null) return user.getName();
        return null;
    }

    // sth maybe userId, userName, Email
    public User getUserBySth(String sth) {
        //if id
        User user = getUserById(sth);
        if (user != null) {
            return user;
        }
        //if email
        UserExample userExample = new UserExample();
        userExample.createCriteria().andEmailEqualTo(sth);
        List<User> userList = userMapper.selectByExample(userExample);
        if (userList.size() > 1) {
            throw new RuntimeException(String.format(Translator.get("i18n_ex_email_duplicate"), sth));
        }
        if (!CollectionUtils.isEmpty(userList)) {
            return userList.get(0);
        }
        //if userName
        userList = getUserByName(sth);
        if (userList.size() > 1) {
            throw new RuntimeException(String.format(Translator.get("i18n_ex_name_duplicate"), sth));
        }
        if (!CollectionUtils.isEmpty(userList)) {
            return userList.get(0);
        }
        return null;
    }

    public void restPassword(Map<String, String> param) {
        if (!"local".equals(SessionUtils.getUser().getSource())) {
            F2CException.throwException(Translator.get("i18n_ex_local_user"));
        }

        String oldPassword = param.get("oldPassword");
        String newPassword = param.get("newPassword");
        if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword)) {
            F2CException.throwException(Translator.get("i18n_ex_password_empty"));
        }
        User user = userMapper.selectByPrimaryKey(SessionUtils.getUser().getId());
        if (user == null) {
            F2CException.throwException(Translator.get("i18n_ex_current_user_not_login"));
        }

        if (!EncryptUtils.md5Encrypt(oldPassword).toString().equalsIgnoreCase(user.getPassword())) {
            F2CException.throwException(Translator.get("i18n_ex_old_password_incorrect"));
        }

        user.setPassword(EncryptUtils.md5Encrypt(newPassword).toString());
        userMapper.updateByPrimaryKey(user);
    }

    public User getUserInfo() {
        return userMapper.selectByPrimaryKey(SessionUtils.getUser().getId());
    }

    public void editUserInfo(User user) {
        if (!SessionUtils.getUser().getId().equals(user.getId())) {
            F2CException.throwException(Translator.get("i18n_ex_not_current_user"));
        }

        if (!"local".equals(SessionUtils.getUser().getSource())) {
            F2CException.throwException(Translator.get("i18n_ex_local_user"));
        }

        UserExample userExample = new UserExample();
        userExample.createCriteria().andEmailEqualTo(user.getEmail());

        List<User> userList = userMapper.selectByExample(userExample);

        if (CollectionUtils.isEmpty(userList)) {
            for (User user1 : userList) {
                if (!user1.getId().equals(user.getId())) {
                    F2CException.throwException(Translator.get("i18n_ex_mail_in_use"));
                }
            }
        }


        User u = new User();
        u.setId(user.getId());
        u.setEmail(user.getEmail());
        u.setName(user.getName());
        u.setPhone(user.getPhone());
        userMapper.updateByPrimaryKeySelective(u);
    }


    public List<UserRoleDTO> getUserRoles(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return new ArrayList<>();
        }
        List<OrgTreeNode> nodes = orgTreeMapper.nodes(false);
        List<UserRoleHelpDTO> helpDTOList = extUserRoleMapper.getUserRoleHelpList(userId);
        //List<UserRoleHelpDTO> helpDTOList = extUserRoleMapper.getRoleTreeHelpList(userId);
        if (roleCommonService.isOrgAdmin()) {
            List<String> sourceIds = WorkspaceUtils.getWorkspaceIdsByOrgIds(SessionUtils.getOrganizationId());
            sourceIds.add(SessionUtils.getOrganizationId());
            return convertUserRoleDTO(helpDTOList.stream()
                    .filter(userRoleHelpDTO -> sourceIds.contains(userRoleHelpDTO.getSourceId()))
                    .collect(Collectors.toList()), nodes);
        }
        return convertUserRoleDTO(helpDTOList, nodes);
    }

    private List<String> cascadeIds(String parentId, List<OrgTreeNode> nodes){
        List<String> result = new ArrayList<>();
        AtomicReference<String> parent = new AtomicReference<>(parentId);
        AtomicReference<OrgTreeNode> currentNode = new AtomicReference<>();
        while (!StringUtils.isEmpty(parent.get())){
            boolean existParent = nodes.stream().anyMatch(node -> {
                currentNode.set(node);
                return StringUtils.equals(parent.get(), node.getNodeId());
            });
            if (existParent){
                result.add(currentNode.get().getNodeId());
                parent.set(currentNode.get().getParentId());
                continue;
            }
            parent.set(null);
        }
        return result;
    }


    private List<UserRoleDTO> convertUserRoleDTO(List<UserRoleHelpDTO> helpDTOList, List<OrgTreeNode> orgTreeNodes) {
        StringBuilder buffer = new StringBuilder();

        Map<String, UserRoleDTO> roleMap = new HashMap<>();

        List<UserRoleDTO> resultList = new ArrayList<>();

        List<UserRoleDTO> otherList = new ArrayList<>();

        Set<String> parentSet = new HashSet<>();

        Map<String,OrgTreeNode> nodesMap = orgTreeNodes.stream().collect(Collectors.toMap(OrgTreeNode::getNodeId , node -> node, (k1,k2) -> k1));

        for (UserRoleHelpDTO helpDTO : helpDTOList) {
            String source_id = helpDTO.getSourceId();
            OrgTreeNode node = nodesMap.get(source_id);

            if (RoleConstants.Id.ADMIN.name().equals(helpDTO.getRoleParentId())) {
                if (StringUtils.isEmpty(buffer.toString())) {
                    buffer.append(helpDTO.getRoleName());
                } else {
                    buffer.append(",");
                    buffer.append(helpDTO.getRoleName());
                }
                continue;
            }


            if (ObjectUtils.isEmpty(node)) continue;
            helpDTO.setSourceName(node.getNodeName());
            //helpDTO.setParentId(node.getParentId());

            if (StringUtils.isEmpty(helpDTO.getSourceName()) && !StringUtils.isEmpty(helpDTO.getSourceId())) {
                continue;
            }

            parentSet.addAll(cascadeIds(node.getParentId(), orgTreeNodes));

            if (StringUtils.equalsIgnoreCase(helpDTO.getRoleId(), RoleConstants.Id.ADMIN.name()) ||
                    StringUtils.equalsIgnoreCase(helpDTO.getRoleId(), RoleConstants.Id.ORGADMIN.name()) ||
                    StringUtils.equalsIgnoreCase(helpDTO.getRoleId(), RoleConstants.Id.USER.name())) {
                helpDTO.setRoleName(Translator.toI18nKey(helpDTO.getRoleName()));
            }



            //第三方角色
            if ("other".equalsIgnoreCase(helpDTO.getRoleParentId())) {
                UserRoleDTO dto = new UserRoleDTO();
                dto.setId(helpDTO.getRoleId());
                dto.setType("other");
                dto.setName(helpDTO.getRoleName());
                dto.setDesc(helpDTO.getRoleName());
                otherList.add(dto);
                continue;
            }

            //组织和系统管理员
            UserRoleDTO userRoleDTO = roleMap.get(helpDTO.getSourceId());


            if (userRoleDTO == null) {
                userRoleDTO = new UserRoleDTO();
                String type = (!StringUtils.isEmpty(node.getNodeType()) && StringUtils.equals("wks",node.getNodeType())) ?
                        "workspace" : "organization";
                parentSet.add(node.getNodeId());
                userRoleDTO.setType(type);
                userRoleDTO.setId(helpDTO.getSourceId());
                userRoleDTO.setName(helpDTO.getSourceName());
                userRoleDTO.setParentId(node.getParentId());
                userRoleDTO.setDesc(helpDTO.getRoleName());

            } else {
                userRoleDTO.setDesc(userRoleDTO.getDesc() + "," + helpDTO.getRoleName());
            }
            roleMap.put(helpDTO.getSourceId(), userRoleDTO);
        }

        if (!StringUtils.isEmpty(buffer.toString())) {
            UserRoleDTO dto = new UserRoleDTO();
            dto.setId("admin");
            dto.setName(WebConstants.getUiInfo().get(ParamConstants.UI.SYSTEM_NAME.getValue()));
            dto.setType("admin");
            dto.setDesc(buffer.toString());
            resultList.add(dto);
        }

        List<UserRoleDTO> orgWorkSpace = new ArrayList<>(roleMap.values());

        parentSet  = parentSet.stream().filter(parentId -> !orgWorkSpace.stream().anyMatch(roleDto -> StringUtils.equals(roleDto.getId(), parentId))).collect(Collectors.toSet());

        if (!CollectionUtils.isEmpty(parentSet)) {
            parentSet.forEach(parentId -> {
                OrgTreeNode node = nodesMap.get(parentId);
                UserRoleDTO dto = new UserRoleDTO();
                dto.setId(parentId);
                dto.setParentId(node.getParentId());
                dto.setName(node.getNodeName());
                dto.setSwitchable(false);
                dto.setType("organization");
                orgWorkSpace.add(dto);
            });
        }

        orgWorkSpace.sort((o1, o2) -> {
            if (o1.getParentId() == null) {
                return -1;
            }

            if (o2.getParentId() == null) {
                return 1;
            }

            return o1.getParentId().compareTo(o2.getParentId());
        });
        resultList.addAll(orgWorkSpace);
        resultList.addAll(otherList);

        return resultList;
    }

    public List<UserRoleDTO> getUserRoleList(String userId) {

        if (StringUtils.isEmpty(userId)) {
            return new ArrayList<>();
        }

        return convertUserRoleDTO(extUserRoleMapper.getUserRoleHelpList(userId), orgTreeMapper.nodes(false));
    }

    public void setLastSourceId(User user, String sourceId) {
        user.setLastSourceId(sourceId);
        userMapper.updateByPrimaryKeySelective(user);
    }

    public UserDTO getUserDTO(String userId) {

        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyBean(userDTO, user);
        //
        UserRoleExample userRoleExample = new UserRoleExample();
        userRoleExample.createCriteria().andUserIdEqualTo(userId);
        List<UserRole> userRoleList = userRoleMapper.selectByExample(userRoleExample);

        if (CollectionUtils.isEmpty(userRoleList)) {
            return userDTO;
        }

        List<String> roleIds = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toList());

        RoleExample roleExample = new RoleExample();
        roleExample.createCriteria().andIdIn(roleIds);

        List<Role> roleList = roleMapper.selectByExample(roleExample);
        userDTO.setRoles(roleList);

        return userDTO;
    }

    public void setLanguage(String lang) {
        if (SessionUtils.getUser() != null) {
            User user = new User();
            user.setId(SessionUtils.getUser().getId());
            user.setLang(lang);
            userMapper.updateByPrimaryKeySelective(user);
            SessionUtils.getUser().setLang(lang);
            //本地测试用
            if (!GlobalConfigurations.isReleaseMode()) {
                HttpServletRequest request = getRequest();
                if (request != null) {
                    request.getSession(true).setAttribute(I18nConstants.LANG_COOKIE_NAME, lang);
                }
            }
        }
    }

    public String getLangByUserId(String userId) {
        String result = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(userId)) {
            User user = userMapper.selectByPrimaryKey(userId);
            if (user != null) {
                result = user.getLang();
            }
        }
        if (StringUtils.isBlank(result)) {
            result = parameterCommonService.getSystemLanguage();
        }
        return result;
    }

    public List<User> getUsersByIdList(List<String> ids) {
       if(CollectionUtils.isEmpty(ids)){
           return new ArrayList<>();
       }
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(ids);
        return userMapper.selectByExample(userExample);
    }


    @Resource
    private OrgTreeMapper orgTreeMapper;

    private List<OrgTreeNode> filterByName(String orgName, List<OrgTreeNode> nodes) {
        if (CollectionUtils.isEmpty(nodes) || StringUtils.isEmpty(orgName)) return nodes;
        Set<String> nodeIdSets = new HashSet<>();
        //1.找到符合条件的节点
        List<OrgTreeNode> treeNodes = nodes.stream().filter(node -> StringUtils.contains(node.getNodeName(), orgName)).collect(Collectors.toList());
        //2.根据1的结果向上查找父节点

        if (CollectionUtils.isEmpty(treeNodes)) return null;

        Map<String,OrgTreeNode> nodesMap = nodes.stream().collect(Collectors.toMap(OrgTreeNode::getNodeId, node -> node, (k1,k2) -> k1));

        treeNodes.stream().forEach(node -> {
            while (ObjectUtils.isNotEmpty(node) ){
                nodeIdSets.add(node.getNodeId());
                String parentId = node.getParentId();
                node = StringUtils.isNotEmpty(parentId) ? nodesMap.get(parentId) : null;
            }
        });

        return nodeIdSets.stream().map(nodesMap::get).collect(Collectors.toList());
    }

    public List<OrgTreeNode> orgTreeNodeList(String rootId, String orgName, boolean excludeWs){
        List<OrgTreeNode> nodes = orgTreeMapper.nodes(!!excludeWs);
        List<Map<String,Object>> nums = orgTreeMapper.relativeNum();
        if (ObjectUtils.isNotEmpty(rootId)){
            //这里需要过滤
        }
        if (ObjectUtils.isNotEmpty(orgName)){
            nodes = filterByName(orgName, nodes);
        }
        if (CollectionUtils.isEmpty(nodes)) return nodes;
        if (CollectionUtils.isEmpty(nums)) return nodes;

        //设置relativeNums
        Map<String, List<Map<String, Object>>> numDataMap = nums.stream().collect(Collectors.groupingBy(num -> num.get("source_id").toString()));
        nodes.forEach(node -> {
            String nodeType = StringUtils.equalsIgnoreCase(node.getNodeType(),"org") ? RoleConstants.Id.ORGADMIN.name(): RoleConstants.Id.USER.name();
            List<Map<String, Object>> nodeNums = numDataMap.get(node.getNodeId());
            boolean existNums = !CollectionUtils.isEmpty(nodeNums) && nodeNums.stream().anyMatch(num -> {
                node.setRelativeNum(Integer.parseInt(num.get("nums").toString()));
                return StringUtils.equals(num.get("role_type").toString(), nodeType);
            });
            if(!existNums)
                node.setRelativeNum(0);
        });
        return nodes;
    }


    /**
     * 根据List格式化为树结构
     * 时间复杂度为o(n平方)
     * @param lists
     * @return
     */
    private List<OrgTreeNode> formatRoot(List<OrgTreeNode> lists) {
        List<OrgTreeNode> rootNodes = new ArrayList<>();
        lists.forEach(node -> {
            if (StringUtils.isEmpty(node.getParentId())) {
                rootNodes.add(node);
            }
            lists.forEach(tNode -> {
                if(StringUtils.equals(tNode.getParentId(), node.getNodeId())){
                    if (node.getChildNodes() == null){
                        node.setChildNodes(new ArrayList<OrgTreeNode>());
                    }
                    //tNode.setParentNode(node);
                    node.getChildNodes().add(tNode);
                }
            });
        });
        return rootNodes;
    }

    public List<OrgTreeNode> orgTreeSelect (String rootId, boolean excludeWs) {
        return formatRoot(orgTreeNodeList(rootId, null, excludeWs));
    }
}
