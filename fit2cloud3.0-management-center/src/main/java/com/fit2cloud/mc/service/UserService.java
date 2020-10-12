package com.fit2cloud.mc.service;

import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.OrganizationMapper;
import com.fit2cloud.commons.server.base.mapper.RoleMapper;
import com.fit2cloud.commons.server.base.mapper.UserMapper;
import com.fit2cloud.commons.server.base.mapper.UserRoleMapper;
import com.fit2cloud.commons.server.constants.ResourceOperation;
import com.fit2cloud.commons.server.constants.ResourceTypeConstants;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.constants.SystemUserConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.ExcelExportRequest;
import com.fit2cloud.commons.server.model.UserDTO;
import com.fit2cloud.commons.server.service.OperationLogService;
import com.fit2cloud.commons.server.service.RoleCommonService;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.server.utils.RoleUtils;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.server.utils.UserRoleUtils;
import com.fit2cloud.commons.server.utils.WorkspaceUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.EncryptUtils;
import com.fit2cloud.commons.utils.ExcelExportUtils;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.common.constants.UserConstants;
import com.fit2cloud.mc.dao.ext.ExtUserMapper;
import com.fit2cloud.mc.dto.RoleInfo;
import com.fit2cloud.mc.dto.UserOperateDTO;
import com.fit2cloud.mc.dto.UserRoleOperateDTO;
import com.fit2cloud.mc.dto.WorkspaceDTO;
import com.fit2cloud.mc.dto.request.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(rollbackFor = Exception.class)
@Service

public class UserService {

    @Resource
    private ExtUserMapper extUserMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    @Lazy
    private WorkspaceService workspaceService;
    @Resource
    private UserCommonService userCommonService;
    @Resource
    private RoleCommonService roleCommonService;
    @Resource
    private RoleService roleService;
    @Resource
    private OrganizationMapper organizationMapper;

    public List<UserDTO> paging(Map<String, Object> map) {
        List<UserDTO> paging = extUserMapper.paging(map);
        convertUserDTO(paging, map);
        return paging;
    }

    public void convertUserDTO(List<UserDTO> list, Map<String, Object> map) {
        list.forEach(userDTO -> {
            map.put("userId", userDTO.getId());
            userDTO.setRoles(roleService.getRolesByResourceIds(map));
        });
    }

    public void delete(String userId) {
        if (StringUtils.equalsIgnoreCase(userId, SessionUtils.getUser().getId())) {
            throw new RuntimeException(Translator.get("i18n_ex_user_no_delete_self"));
        }
        User user = userMapper.selectByPrimaryKey(userId);
        if (user != null) {
            OperationLogService.log(null, userId, user.getName(), ResourceTypeConstants.USER.name(), ResourceOperation.DELETE, null);
        }
        UserRoleExample userRoleExample = new UserRoleExample();

        if (roleCommonService.isOrgAdmin()) {
            //当为组织管理员时，删除当前组织和当前组织下的工作空间（解绑关系user_role）
            List<String> list = new ArrayList<>();
            list.add(SessionUtils.getOrganizationId());
            List<Workspace> workspaces = workspaceService.workspacesByOrgId(SessionUtils.getOrganizationId());
            if (CollectionUtils.isNotEmpty(workspaces)) {
                list.addAll(workspaces.stream().map(Workspace::getId).collect(Collectors.toList()));
            }
            userRoleExample.createCriteria().andUserIdEqualTo(userId).andSourceIdIn(list);
            userRoleMapper.deleteByExample(userRoleExample);
        }
        if (roleCommonService.isAdmin()) {
            userMapper.deleteByPrimaryKey(userId);
            userRoleExample.createCriteria().andUserIdEqualTo(userId);
            userRoleMapper.deleteByExample(userRoleExample);
        }
    }

    public UserDTO insert(CreateUserRequest request) {
        if (StringUtils.isBlank(request.getSource())) {
            checkCreateUserParam(request);
            createUser(request);
            OperationLogService.log(null, request.getId(), request.getName(), ResourceTypeConstants.USER.name(), ResourceOperation.CREATE, "");
            return userCommonService.getUserDTO(request.getId());
        } else {
            UserOperateDTO user = new UserOperateDTO();
            BeanUtils.copyBean(user, request);
            return insert(user);
        }

    }

    public UserDTO insert(UserOperateDTO user) {

        if (StringUtils.isBlank(user.getId()) || StringUtils.isBlank(user.getEmail())) {
            F2CException.throwException(Translator.get("i18n_ex_user_no_empty_id_email"));
        }

        if (SystemUserConstants.getUserId().equalsIgnoreCase(user.getId())) {
            F2CException.throwException(Translator.get("i18n_ex_user_id_no_system"));
        }

        user.setCreateTime(Instant.now().toEpochMilli());
        user.setPassword(EncryptUtils.md5Encrypt(user.getPassword()).toString());
        if (StringUtils.isBlank(user.getSource())) {
            user.setSource(UserConstants.Source.LOCAL.getValue());
        }
        user.setActive(true);

        if (userMapper.selectByPrimaryKey(user.getId()) != null) {
            F2CException.throwException(Translator.get("i18n_ex_user_id_exist"));
        }

        UserExample userExample = new UserExample();
        UserExample.Criteria criteriaMail = userExample.createCriteria();
        criteriaMail.andEmailEqualTo(user.getEmail());
        List<User> userList = userMapper.selectByExample(userExample);

        if (!CollectionUtils.isEmpty(userList)) {
            F2CException.throwException(Translator.get("i18n_ex_user_email_exist"));
        }

        userMapper.insert(user);
        OperationLogService.log(null, user.getId(), user.getName(), ResourceTypeConstants.USER.name(), ResourceOperation.CREATE, null);
        if (!CollectionUtils.isEmpty(user.getRoleInfoList())) {
            insertUserRoleInfo(user);
        }
        return userCommonService.getUserDTO(user.getId());
    }

    public void addUserRole(UserRoleOperateDTO roleOperateDTO) {
        if (CollectionUtils.isEmpty(roleOperateDTO.getUserIdList())
                || CollectionUtils.isEmpty(roleOperateDTO.getRoleInfoList())) {
            return;
        }

        roleOperateDTO.getUserIdList().forEach(userId -> {
            roleOperateDTO.getRoleInfoList().forEach(roleInfo -> {
                UserRole userRole = new UserRole();
                userRole.setRoleId(roleInfo.getRoleId());
                userRole.setUserId(userId);
                String parentId = RoleUtils.getParentId(roleInfo.getRoleId());
                if (StringUtils.equals(parentId, RoleConstants.Id.USER.name())) {
                    //查询TODO
                    roleInfo.getWorkspaceIds().forEach(workspaceId -> {
                                if (!hasUserRole(userId, roleInfo.getRoleId(), workspaceId)) {
                                    insertUserRoleInfo(userRole, workspaceId);
                                }
                            }
                    );
                }
                if (StringUtils.equals(parentId, RoleConstants.Id.ORGADMIN.name())) {
                    roleInfo.getOrganizationIds().forEach(organizationId -> {
                                if (!hasUserRole(userId, roleInfo.getRoleId(), organizationId)) {
                                    insertUserRoleInfo(userRole, organizationId);
                                }
                            }
                    );
                }

                if (StringUtils.equals(parentId, RoleConstants.Id.ADMIN.name())) {
                    if (!hasUserRole(userId, roleInfo.getRoleId(), null)) {
                        insertUserRoleInfo(userRole, null);
                    }
                }

                if (StringUtils.equals(parentId, "other")) {
                    if (!hasUserRole(userId, roleInfo.getRoleId(), null)) {
                        insertUserRoleInfo(userRole, null);
                    }
                }
            });
        });
    }

    private boolean hasUserRole(String userId, String roleId, String sourceId) {
        UserRoleExample userRoleExample = new UserRoleExample();
        UserRoleExample.Criteria criteria = userRoleExample.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andRoleIdEqualTo(roleId);
        if (!StringUtils.isBlank(sourceId)) {
            criteria.andSourceIdEqualTo(sourceId);
        }

        List<UserRole> userRoleList = userRoleMapper.selectByExample(userRoleExample);

        return !CollectionUtils.isEmpty(userRoleList);
    }

    private void checkSystemAdmin(UserOperateDTO userOperate) {
        //查看默认系统管理员的个数，即roleId为ADMIN
        UserRoleExample userRoleExample = new UserRoleExample();
        UserRoleExample.Criteria criteria = userRoleExample.createCriteria();
        criteria.andRoleIdEqualTo(RoleConstants.Id.ADMIN.name());
        long countAdmin = userRoleMapper.countByExample(userRoleExample);
        if (countAdmin < 2) {
            List<UserRole> userRoles = userRoleMapper.selectByExample(userRoleExample);
            boolean anyMatch = userRoles.stream().anyMatch(userRole -> userRole.getUserId().equals(userOperate.getId()));
            boolean isContainAdmin = userOperate.getRoleInfoList().stream().anyMatch(roleInfo -> StringUtils.equals(roleInfo.getRoleId(), RoleConstants.Id.ADMIN.name()));
            if (anyMatch && !isContainAdmin) {
                F2CException.throwException(Translator.get("i18n_ex_user_have_no_admin"));
            }
        }
    }

    public void update(UserOperateDTO userOperate) {
        if (StringUtils.isBlank(userOperate.getId()) || StringUtils.isBlank(userOperate.getEmail())) {
            F2CException.throwException(Translator.get("i18n_ex_user_no_empty_id_email"));
        }

        UserRoleExample deleteExample = new UserRoleExample();
        UserRoleExample.Criteria deleteCriteria = deleteExample.createCriteria();
        deleteCriteria.andUserIdEqualTo(userOperate.getId());
        if (roleCommonService.isAdmin()) {
            checkSystemAdmin(userOperate);
            //删除要编辑的用户在user_role的信息，然后reinsert
            userRoleMapper.deleteByExample(deleteExample);

        } else if (roleCommonService.isOrgAdmin()) {
            //删除要编辑的用户在当前组织下的user_role的信息，然后reinsert
            //由于userOperate.roleInfoList 传的数据有特殊性 注意
            List<String> list = new ArrayList<>();
            list.add(SessionUtils.getOrganizationId());
            List<Workspace> workspaces = workspaceService.workspacesByOrgId(SessionUtils.getOrganizationId());
            if (CollectionUtils.isNotEmpty(workspaces)) {
                list.addAll(workspaces.stream().map(Workspace::getId).collect(Collectors.toList()));
            }

            deleteCriteria.andSourceIdIn(list);
            userRoleMapper.deleteByExample(deleteExample);
        }

        //reinsert
        insertUserRoleInfo(userOperate);
        //非本地创建用户 不允许修改邮箱和名称
        if (!UserConstants.Source.LOCAL.getValue().equals(SessionUtils.getUser().getSource())) {
            userOperate.setEmail(null);
            userOperate.setName(null);
        } else {
            validateUser(userOperate);
        }
        userMapper.updateByPrimaryKeySelective(userOperate);
        OperationLogService.log(null, userOperate.getId(), userOperate.getName(), ResourceTypeConstants.USER.name(), ResourceOperation.UPDATE, null);
    }

    private void validateUser(UserOperateDTO userOperate) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andEmailEqualTo(userOperate.getEmail());
        List<User> userList = userMapper.selectByExample(userExample);
        if (!CollectionUtils.isEmpty(userList)) {
            for (User user : userList) {
                if (!user.getId().equals(userOperate.getId())) {
                    F2CException.throwException(Translator.get("i18n_ex_user_email_exist"));
                }
            }
        }
    }

    public void insertUserRoleInfo(UserOperateDTO userOperate) {
        for (RoleInfo roleInfo : userOperate.getRoleInfoList()) {
            UserRole userRole = new UserRole();
            userRole.setRoleId(roleInfo.getRoleId());
            userRole.setUserId(userOperate.getId());

            String parentId = RoleUtils.getParentId(roleInfo.getRoleId());
            if (StringUtils.equals(parentId, RoleConstants.Id.USER.name())) {
                if (roleInfo.getWorkspace()) {
                    CreateWorkspaceRequest createWorkspaceRequest = new CreateWorkspaceRequest();
                    if (StringUtils.equalsIgnoreCase(SessionUtils.getUser().getParentRoleId(), RoleConstants.Id.ORGADMIN.name())) {
                        createWorkspaceRequest.setOrganizationId(SessionUtils.getOrganizationId());
                    } else {
                        createWorkspaceRequest.setOrganizationId(roleInfo.getSelectOrganizationId());
                    }
                    createWorkspaceRequest.setName(userOperate.getName() + "-workspace");
                    createWorkspaceRequest.setDescription("auto create");
                    WorkspaceDTO workspaceDTO = workspaceService.insert(createWorkspaceRequest);
                    userRole.setId(UUIDUtil.newUUID());
                    insertUserRoleInfo(userRole, workspaceDTO.getId());
                } else {
                    roleInfo.getWorkspaceIds().forEach(workspaceId -> insertUserRoleInfo(userRole, workspaceId));
                }
            }
            if (StringUtils.equals(parentId, RoleConstants.Id.ORGADMIN.name())) {
                roleInfo.getOrganizationIds().forEach(organizationId -> insertUserRoleInfo(userRole, organizationId));
            }
            if (StringUtils.equals(parentId, RoleConstants.Id.ADMIN.name())) {
                insertUserRoleInfo(userRole, null);
            }

            if (StringUtils.equals(parentId, "other")) {
                insertUserRoleInfo(userRole, null);
            }
        }

    }

    private void insertUserRoleInfo(UserRole userRole, String resourceId) {
        String uuid = UUIDUtil.newUUID();
        userRole.setId(uuid);
        userRole.setSourceId(resourceId);
        userRoleMapper.insert(userRole);
    }

    public Object resourceIds(String userId) {
        return UserRoleUtils.getResourceIds(userId);
    }

    public void resetPassword(User user) {
        if (!"local".equals(SessionUtils.getUser().getSource())) {
            F2CException.throwException(Translator.get("i18n_ex_no_local_user_no_edit"));
        }
        User select = userMapper.selectByPrimaryKey(user.getId());
        select.setPassword(EncryptUtils.md5Encrypt(user.getPassword()).toString());
        userMapper.updateByPrimaryKeySelective(select);
    }

    public void changeActive(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setActive(userDTO.getActive());
        if (userDTO.getRoles().stream().map(Role::getId).anyMatch(s -> s.equals(RoleConstants.Id.ADMIN.name())) && !userDTO.getActive()) {
            Long countAdmin = extUserMapper.countActivesUser(RoleConstants.Id.ADMIN.name());
            if (countAdmin > 1) {
                userMapper.updateByPrimaryKeySelective(user);
            } else {
                throw new RuntimeException(Translator.get("i18n_ex_last_one_admin"));
            }
        } else {
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

    public List<RoleInfo> roleInfo(String userId) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        if (roleCommonService.isOrgAdmin()) {
            List<String> resourceIds = WorkspaceUtils.getWorkspaceIdsByOrgIds(SessionUtils.getOrganizationId());
            resourceIds.add(SessionUtils.getOrganizationId());
            param.put("resourceIds", resourceIds);
        }
        List<RoleInfo> roleInfos = extUserMapper.roleInfo(param);
        return roleInfos;
    }

    public List<Workspace> resourcePaging(String resourceType, String userId, String roleId) {
        return extUserMapper.resourcePaging(resourceType, userId, roleId);
    }

    public byte[] exportUsers(ExcelExportRequest request) throws Exception {
        Map<String, Object> params = request.getParams();
        List<ExcelExportRequest.Column> columns = request.getColumns();
        List<UserDTO> userDTOS = this.paging(params);
        List<List<Object>> data = userDTOS.parallelStream().map(userDTO -> new ArrayList<Object>() {{
            columns.forEach(column -> {
                switch (column.getKey()) {
                    case "roleName":
                        add(StringUtils.join(userDTO.getRoles().stream().map(Role::getName).collect(Collectors.toList()), ","));
                        break;
                    case "active":
                        if (userDTO.getActive()) {
                            add(Translator.get("i18n_module_enable"));
                        } else {
                            add(Translator.get("i18n_module_disable"));
                        }
                        break;
                    case "user.create_time":
                        Instant instant = Instant.ofEpochMilli(userDTO.getCreateTime());
                        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                        String time = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        add(time);
                        break;
                    case "source":
                        UserConstants.Source source = UserConstants.Source.valueOf(userDTO.getSource().toUpperCase());
                        switch (source) {
                            case LOCAL:
                                add(Translator.get("i18n_user_source_local"));
                                break;
                            case EXTRA:
                                add(Translator.get("i18n_user_source_extra"));
                                break;
                        }
                        break;
                    default:
                        try {
                            add(MethodUtils.invokeMethod(userDTO, "get" + StringUtils.capitalize(ExcelExportUtils.underlineToCamelCase(column.getKey()))));
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            });
        }}).collect(Collectors.toList());
        return ExcelExportUtils.exportExcelData(Translator.get("i18n_user_list"), request.getColumns().stream().map(ExcelExportRequest.Column::getValue).collect(Collectors.toList()), data);
    }

    public UserDTO createOrganizationUser(CreateOrganizationUserRequest request) {

        checkCreateUserParam(request);

        if (StringUtils.isBlank(request.getRoleId())) {
            F2CException.throwException(Translator.get("i18n_ex_role_id_no_empty"));
        }

        Role role = roleMapper.selectByPrimaryKey(request.getRoleId());
        if (role == null) {
            F2CException.throwException(Translator.get("i18n_ex_role_no_exist"));
        }
        if (!RoleConstants.Id.ORGADMIN.name().equalsIgnoreCase(role.getParentId())) {
            F2CException.throwException(Translator.get("i18n_ex_role_no_org"));
        }

        if (CollectionUtils.isEmpty(request.getOrganizationIds()) && roleCommonService.isAdmin()) {
            F2CException.throwException(Translator.get("i18n_ex_org_list_id_no_empty"));
        }

        createUser(request);

        List<String> organizationIds = new ArrayList<>();
        //添加组织 组织管理员只能创建当前组织管理员
        if (roleCommonService.isAdmin()) {
            //检验组织是否存在
            checkHasOrgIds(request.getOrganizationIds());
            organizationIds.addAll(request.getOrganizationIds());
        } else if (roleCommonService.isOrgAdmin()) {
            organizationIds.add(SessionUtils.getOrganizationId());
        }

        organizationIds.forEach(organizationId -> addUserRole(request, organizationId, request.getRoleId()));

        OperationLogService.log(null, request.getId(), request.getName(), ResourceTypeConstants.USER.name(), ResourceOperation.CREATE, "");

        return userCommonService.getUserDTO(request.getId());
    }

    private void checkHasOrgIds(List<String> list) {
        for (String orgId : list) {
            Organization organization = organizationMapper.selectByPrimaryKey(orgId);
            if (organization == null) {
                F2CException.throwException(Translator.get("i18n_ex_org_no_exist") + orgId);
            }
        }
    }

    public UserDTO CreateWorkspaceUser(CreateWorkspaceUserRequest request) {

        checkCreateUserParam(request);

        if (StringUtils.isBlank(request.getRoleId())) {
            F2CException.throwException(Translator.get("i18n_ex_role_id_no_empty"));
        }

        Role role = roleMapper.selectByPrimaryKey(request.getRoleId());
        if (role == null) {
            F2CException.throwException(Translator.get("i18n_ex_role_no_exist"));
        }
        if (!RoleConstants.Id.USER.name().equalsIgnoreCase(role.getParentId())) {
            F2CException.throwException(Translator.get("i18n_ex_role_no_user"));
        }

        if (CollectionUtils.isEmpty(request.getWorkspaceIds())) {
            F2CException.throwException(Translator.get("i18n_ex_user_list_id_no_empty"));
        }

        checkHasWorkspaceIds(request.getWorkspaceIds());

        //组织管理员只能添加有权限的工作空间
        if (roleCommonService.isOrgAdmin()) {
            List<String> resourceIds = WorkspaceUtils.getWorkspaceIdsByOrgIds(SessionUtils.getOrganizationId());
            for (String workspaceId : request.getWorkspaceIds()) {
                if (!resourceIds.contains(workspaceId)) {
                    F2CException.throwException(String.format(Translator.get("i18n_ex_user_no_org"), workspaceId));
                }
            }
        }

        createUser(request);

        //添加工作空间
        List<String> workspaceIds = request.getWorkspaceIds();

        workspaceIds.forEach(workspaceId -> addUserRole(request, workspaceId, request.getRoleId()));
        OperationLogService.log(null, request.getId(), request.getName(), ResourceTypeConstants.USER.name(), ResourceOperation.CREATE, "");

        return userCommonService.getUserDTO(request.getId());
    }

    private void checkHasWorkspaceIds(List<String> list) {
        for (String workspaceId : list) {
            Workspace workspace = workspaceService.getWorkspaceById(workspaceId);
            if (workspace == null) {
                F2CException.throwException(Translator.get("i18n_ex_workspace_no_exist") + workspaceId);
            }
        }
    }

    private void addUserRole(CreateUserBaseRequest request, String sourceId, String roleId) {
        UserRole userRole = new UserRole();
        userRole.setId(UUIDUtil.newUUID());
        userRole.setRoleId(roleId);
        userRole.setSourceId(sourceId);
        userRole.setUserId(request.getId());
        userRoleMapper.insertSelective(userRole);
    }

    public void createUser(CreateUserBaseRequest request) {
        User user = new User();
        BeanUtils.copyBean(user, request);
        user.setSource(UserConstants.Source.LOCAL.getValue());
        user.setPassword(EncryptUtils.md5Encrypt(user.getPassword()).toString());
        user.setCreateTime(System.currentTimeMillis());
        userMapper.insertSelective(user);
    }

    private void checkCreateUserParam(CreateUserBaseRequest request) {
        if (StringUtils.isBlank(request.getId())) {
            F2CException.throwException(Translator.get("i18n_ex_user_id_empty"));
        }

        if (SystemUserConstants.getUserId().equalsIgnoreCase(request.getId())) {
            F2CException.throwException(Translator.get("i18n_ex_user_id_no_system"));
        }

        if (StringUtils.isBlank(request.getName())) {
            F2CException.throwException(Translator.get("i18n_ex_user_name_empty"));
        }

        if (StringUtils.isBlank(request.getPassword())) {
            F2CException.throwException(Translator.get("i18n_ex_user_password_empty"));
        }

        if (userMapper.selectByPrimaryKey(request.getId()) != null) {
            F2CException.throwException(Translator.get("i18n_ex_user_id_exist"));
        }
    }

    public UserDTO addRoleToUser(AddRoleToUserRequest request) {
        if (StringUtils.isBlank(request.getRoleId())) {
            F2CException.throwException(Translator.get("i18n_ex_role_id_no_empty"));
        }
        if (StringUtils.isBlank(request.getUserId())) {
            F2CException.throwException(Translator.get("i18n_ex_user_id_empty"));
        }

        User user = userMapper.selectByPrimaryKey(request.getUserId());
        if (user == null) {
            F2CException.throwException(Translator.get("i18n_ex_user_exist"));
        }

        Role role = roleMapper.selectByPrimaryKey(request.getRoleId());

        if (role == null) {
            F2CException.throwException(Translator.get("i18n_ex_role_no_exist"));
        }

        if (RoleConstants.Id.ADMIN.name().equalsIgnoreCase(role.getParentId())
                && !RoleConstants.Id.ADMIN.name().equalsIgnoreCase(SessionUtils.getUser().getParentRoleId())) {
            F2CException.throwException(Translator.get("i18n_ex_no_admin_no_delete_admin"));
        }

        if (RoleConstants.Id.USER.name().equalsIgnoreCase(role.getParentId())) {
            if (CollectionUtils.isEmpty(request.getResourceIds())) {
                F2CException.throwException(Translator.get("i18n_ex_resourceIds_workspace_no_empty"));
            }
            if (roleCommonService.isOrgAdmin()) {
                checkCurrentOrgHasWorkspaceIds(request.getResourceIds());
            }
            checkHasWorkspaceIds(request.getResourceIds());
        } else if (RoleConstants.Id.ORGADMIN.name().equalsIgnoreCase(role.getParentId())) {
            //分系统管理员和组织管理员
            if (roleCommonService.isOrgAdmin()) {
                //组织管理员只能添加当前组织
                List<String> list = new ArrayList<>();
                list.add(SessionUtils.getOrganizationId());
                request.setResourceIds(list);
            } else {
                if (CollectionUtils.isEmpty(request.getResourceIds())) {
                    F2CException.throwException(Translator.get("i18n_ex_resourceIds_org_no_empty"));
                }
                checkHasOrgIds(request.getResourceIds());
            }
        } else {
            request.setResourceIds(null);
        }

        addUserRole(request);

        return userCommonService.getUserDTO(request.getUserId());
    }

    private void checkCurrentOrgHasWorkspaceIds(List<String> list) {
        List<String> workspaceIds = WorkspaceUtils.getWorkspaceIdsByOrgIds(SessionUtils.getOrganizationId());
        for (String workspaceId : list) {
            if (!workspaceIds.contains(workspaceId)) {
                F2CException.throwException(String.format(Translator.get("i18n_ex_user_no_org"), workspaceId));
            }
        }
    }

    private void addUserRole(AddRoleToUserRequest request) {
        UserRoleExample userRoleExample = new UserRoleExample();
        UserRoleExample.Criteria criteria = userRoleExample.createCriteria();
        criteria.andUserIdEqualTo(request.getUserId());
        criteria.andRoleIdEqualTo(request.getRoleId());

        if (CollectionUtils.isEmpty(request.getResourceIds())) {
            if (CollectionUtils.isEmpty(userRoleMapper.selectByExample(userRoleExample))) {
                UserRole userRole = new UserRole();
                userRole.setId(UUIDUtil.newUUID());
                userRole.setRoleId(request.getRoleId());
                userRole.setUserId(request.getUserId());
                userRoleMapper.insertSelective(userRole);
            }
        } else {
            request.getResourceIds().forEach(resourceId -> {
                criteria.andSourceIdEqualTo(resourceId);
                if (CollectionUtils.isEmpty(userRoleMapper.selectByExample(userRoleExample))) {
                    UserRole userRole = new UserRole();
                    userRole.setId(UUIDUtil.newUUID());
                    userRole.setRoleId(request.getRoleId());
                    userRole.setSourceId(resourceId);
                    userRole.setUserId(request.getUserId());
                    userRoleMapper.insertSelective(userRole);
                }
            });
        }
    }

    public UserDTO deleteRoleFromUser(DeleteRoleFromUserRequest request) {
        if (StringUtils.isBlank(request.getRoleId())) {
            F2CException.throwException(Translator.get("i18n_ex_role_id_no_empty"));
        }
        if (StringUtils.isBlank(request.getUserId())) {
            F2CException.throwException(Translator.get("i18n_ex_user_id_no_empty"));
        }

        Role role = roleMapper.selectByPrimaryKey(request.getRoleId());
        if (role == null) {
            F2CException.throwException(Translator.get("i18n_ex_role_no_exist"));
        }

        User user = userMapper.selectByPrimaryKey(request.getUserId());
        if (user == null) {
            F2CException.throwException(Translator.get("i18n_ex_user_no_exist"));
        }

        if (RoleConstants.Id.ADMIN.name().equalsIgnoreCase(role.getParentId())
                && !RoleConstants.Id.ADMIN.name().equalsIgnoreCase(SessionUtils.getUser().getParentRoleId())) {
            F2CException.throwException(Translator.get("i18n_ex_no_admin_no_delete_admin"));
        }

        if (RoleConstants.Id.USER.name().equalsIgnoreCase(role.getParentId())) {
            if (CollectionUtils.isEmpty(request.getResourceIds())) {
                F2CException.throwException(Translator.get("i18n_ex_resourceIds_workspace_no_empty"));
            }
            if (roleCommonService.isOrgAdmin()) {
                checkCurrentOrgHasWorkspaceIds(request.getResourceIds());
            }
            checkHasWorkspaceIds(request.getResourceIds());
        } else if (RoleConstants.Id.ORGADMIN.name().equalsIgnoreCase(role.getParentId())) {
            if (roleCommonService.isOrgAdmin()) {
                //组织管理员只能删除当前组织
                List<String> list = new ArrayList<>();
                request.setResourceIds(list);
                list.add(SessionUtils.getOrganizationId());
            } else {
                if (CollectionUtils.isEmpty(request.getResourceIds())) {
                    F2CException.throwException(Translator.get("i18n_ex_resourceIds_org_no_empty"));
                }
                checkHasOrgIds(request.getResourceIds());
            }
        } else {
            request.setResourceIds(null);
        }

        deleteUserRole(request);

        return userCommonService.getUserDTO(request.getUserId());
    }

    private void deleteUserRole(DeleteRoleFromUserRequest request) {

        UserRoleExample userRoleExample = new UserRoleExample();
        UserRoleExample.Criteria criteria = userRoleExample.createCriteria();
        criteria.andUserIdEqualTo(request.getUserId());
        criteria.andRoleIdEqualTo(request.getRoleId());

        if (CollectionUtils.isEmpty(request.getResourceIds())) {
            userRoleMapper.deleteByExample(userRoleExample);
        } else {
            request.getResourceIds().forEach(resourceId -> {
                criteria.andSourceIdEqualTo(resourceId);
                userRoleMapper.deleteByExample(userRoleExample);
            });
        }

    }
}
