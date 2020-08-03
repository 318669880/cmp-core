package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.commons.server.base.mapper.OrganizationMapper;
import com.fit2cloud.commons.server.base.mapper.RoleMapper;
import com.fit2cloud.commons.server.base.mapper.UserMapper;
import com.fit2cloud.commons.server.base.mapper.UserRoleMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtUserRoleMapper;
import com.fit2cloud.commons.server.constants.I18nConstants;
import com.fit2cloud.commons.server.constants.ParamConstants;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.constants.WebConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.UserDTO;
import com.fit2cloud.commons.server.model.UserRoleDTO;
import com.fit2cloud.commons.server.model.UserRoleHelpDTO;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.server.utils.WorkspaceUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.EncryptUtils;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
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

        List<UserRoleHelpDTO> helpDTOList = extUserRoleMapper.getUserRoleHelpList(userId);
        if (roleCommonService.isOrgAdmin()) {
            List<String> sourceIds = WorkspaceUtils.getWorkspaceIdsByOrgIds(SessionUtils.getOrganizationId());
            sourceIds.add(SessionUtils.getOrganizationId());
            return convertUserRoleDTO(helpDTOList.stream()
                    .filter(userRoleHelpDTO -> sourceIds.contains(userRoleHelpDTO.getSourceId()))
                    .collect(Collectors.toList()));
        }
        return convertUserRoleDTO(helpDTOList);
    }

    private List<UserRoleDTO> convertUserRoleDTO(List<UserRoleHelpDTO> helpDTOList) {
        StringBuilder buffer = new StringBuilder();

        Map<String, UserRoleDTO> roleMap = new HashMap<>();

        List<UserRoleDTO> resultList = new ArrayList<>();

        List<UserRoleDTO> otherList = new ArrayList<>();

        Set<String> orgSet = new HashSet<>();

        Set<String> workspaceSet = new HashSet<>();

        for (UserRoleHelpDTO helpDTO : helpDTOList) {

            if (StringUtils.isEmpty(helpDTO.getSourceName()) && !StringUtils.isEmpty(helpDTO.getSourceId())) {
                continue;
            }

            if (StringUtils.equalsIgnoreCase(helpDTO.getRoleId(), RoleConstants.Id.ADMIN.name()) ||
                    StringUtils.equalsIgnoreCase(helpDTO.getRoleId(), RoleConstants.Id.ORGADMIN.name()) ||
                    StringUtils.equalsIgnoreCase(helpDTO.getRoleId(), RoleConstants.Id.USER.name())) {
                helpDTO.setRoleName(Translator.toI18nKey(helpDTO.getRoleName()));
            }

            if (RoleConstants.Id.ADMIN.name().equals(helpDTO.getRoleParentId())) {
                if (StringUtils.isEmpty(buffer.toString())) {
                    buffer.append(helpDTO.getRoleName());
                } else {
                    buffer.append(",");
                    buffer.append(helpDTO.getRoleName());
                }

                continue;
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

                if (!StringUtils.isEmpty(helpDTO.getParentId())) {
                    workspaceSet.add(helpDTO.getParentId());
                    userRoleDTO.setType("workspace");
                } else {
                    orgSet.add(helpDTO.getSourceId());
                    userRoleDTO.setType("organization");
                }


                userRoleDTO.setId(helpDTO.getSourceId());
                userRoleDTO.setName(helpDTO.getSourceName());
                userRoleDTO.setParentId(helpDTO.getParentId());
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

        for (String org : orgSet) {
            workspaceSet.remove(org);
        }

        List<UserRoleDTO> orgWorkSpace = new ArrayList<>(roleMap.values());

        if (!CollectionUtils.isEmpty(workspaceSet)) {
            for (String orgId : workspaceSet) {
                Organization organization = organizationMapper.selectByPrimaryKey(orgId);
                if (organization != null) {
                    UserRoleDTO dto = new UserRoleDTO();
                    dto.setId(orgId);
                    dto.setName(organization.getName());
                    dto.setSwitchable(false);
                    dto.setType("organization");
                    orgWorkSpace.add(dto);
                }
            }
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

        return convertUserRoleDTO(extUserRoleMapper.getUserRoleHelpList(userId));
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

}
