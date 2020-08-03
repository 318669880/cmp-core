package com.fit2cloud.commons.server.utils;

import com.fit2cloud.commons.server.base.domain.Role;
import com.fit2cloud.commons.server.base.domain.RoleExample;
import com.fit2cloud.commons.server.base.mapper.RoleMapper;
import com.fit2cloud.commons.server.base.mapper.ext.ExtUserRoleMapper;
import com.fit2cloud.commons.server.constants.ParamConstants;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.constants.WebConstants;
import com.fit2cloud.commons.server.model.SessionUser;
import com.fit2cloud.commons.server.model.UserRoleDTO;
import com.fit2cloud.commons.server.model.UserRoleHelpDTO;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author: chunxing
 * Date: 2018/5/30  下午3:46
 * Description:
 */
@Component
public class RoleUtils {


    private static RoleMapper roleMapper;

    private static ExtUserRoleMapper extUserRoleMapper;

    @Resource
    public void setRoleMapper(RoleMapper roleMapper) {
        RoleUtils.roleMapper = roleMapper;
    }

    @Resource
    public void setExtUserRoleMapper(ExtUserRoleMapper extUserRoleMapper) {
        RoleUtils.extUserRoleMapper = extUserRoleMapper;
    }

    public static String getParentId(String roleId) {
        Role role = roleMapper.selectByPrimaryKey(roleId);
        return getParentId(role);
    }

    public static Set<String> getChildIds(String RoleId) {
        RoleExample example = new RoleExample();
        example.createCriteria().andParentIdEqualTo(RoleId);
        List<Role> roles = roleMapper.selectByExample(example);
        return roles.stream().map(Role::getId).collect(Collectors.toSet());
    }

    public static String getParentId(Role role) {
        if (role == null) {
            return null;
        }
        if (role.getParentId() != null) {
            return role.getParentId();
        } else {
            return role.getId();
        }
    }

    public static List<String> getAdminRoleIdList(String userId) {
        List<UserRoleHelpDTO> userRoleHelpList = extUserRoleMapper.getUserRoleHelpList(userId);
        return userRoleHelpList.stream().filter(userRoleHelpDTO -> RoleConstants.Id.ADMIN.name().equals(userRoleHelpDTO.getRoleParentId()))
                .map(UserRoleHelpDTO::getRoleId).collect(Collectors.toList());
    }

    public static void convertUserRole(SessionUser sessionUser) {
        List<UserRoleHelpDTO> userRoleHelpList = extUserRoleMapper.getUserRoleHelpList(sessionUser.getId());

        boolean setValue = false;

        if (!StringUtils.isEmpty(sessionUser.getLastSourceId())) {
            setValue = setSessionUser(sessionUser, userRoleHelpList);
        }

        if (!setValue) {
            UserCommonService userCommonService = (UserCommonService) CommonBeanFactory.getBean("userCommonService");
            List<UserRoleDTO> userRoleList = userCommonService.getUserRoleList(sessionUser.getId());

            for (UserRoleDTO dto : userRoleList) {
                if (dto.getSwitchable()) {
                    sessionUser.setLastSourceId(dto.getId());
                    setSessionUser(sessionUser, userRoleHelpList);
                    userCommonService.setLastSourceId(sessionUser, dto.getId());
                    break;
                }
            }
        }


    }


    private static boolean setSessionUser(SessionUser sessionUser, List<UserRoleHelpDTO> userRoleHelpDTOList) {
        boolean setValue = false;

        List<String> roleList = new ArrayList<>();
        for (UserRoleHelpDTO helpDTO : userRoleHelpDTOList) {
            if (!StringUtils.isEmpty(helpDTO.getSourceId()) && helpDTO.getSourceId().equals(sessionUser.getLastSourceId())) {
                roleList.add(helpDTO.getRoleId());
                if (!StringUtils.isEmpty(helpDTO.getParentId())) {
                    //工作空间
                    sessionUser.setWorkspaceId(helpDTO.getSourceId());
                    sessionUser.setOrganizationId(helpDTO.getParentId());
                } else {
                    //组织
                    sessionUser.setOrganizationId(helpDTO.getSourceId());
                }

                sessionUser.setParentRoleId(helpDTO.getRoleParentId());
                sessionUser.setSourceName(helpDTO.getSourceName());
                setValue = true;
            }
        }

        if (!setValue) {
            for (UserRoleHelpDTO helpDTO : userRoleHelpDTOList) {
                if (helpDTO.getRoleId().equals(sessionUser.getLastSourceId())) {
                    roleList.add(helpDTO.getRoleId());
                    sessionUser.setParentRoleId("other");
                    sessionUser.setSourceName(helpDTO.getRoleName());
                    setValue = true;
                    break;
                }
            }
        }

        if (!setValue && "admin".equals(sessionUser.getLastSourceId())) {
            roleList.addAll(userRoleHelpDTOList.stream().filter(userRoleHelpDTO -> RoleConstants.Id.ADMIN.name().equals(userRoleHelpDTO.getRoleParentId()))
                    .map(UserRoleHelpDTO::getRoleId).collect(Collectors.toList()));
            sessionUser.setParentRoleId(RoleConstants.Id.ADMIN.name());
            setValue = true;
            sessionUser.setSourceName(WebConstants.getUiInfo().get(ParamConstants.UI.SYSTEM_NAME.getValue()));
        }

        if (setValue) {
            sessionUser.setSourceId(sessionUser.getLastSourceId());
            sessionUser.setRoleIdList(roleList);
        }

        return setValue;
    }


    public static boolean existSourceId(String userId, String sourceId) {
        boolean hasAdmin = false;
        List<UserRoleHelpDTO> userRoleHelpList = extUserRoleMapper.getUserRoleHelpList(userId);
        for (UserRoleHelpDTO helpDTO : userRoleHelpList) {
            if ((!StringUtils.isEmpty(helpDTO.getRoleParentId()) && RoleConstants.Id.ADMIN.name().equals(helpDTO.getRoleParentId()))
                    || RoleConstants.Id.ADMIN.name().equals(helpDTO.getRoleId())) {
                hasAdmin = true;
            }
            if (!StringUtils.isEmpty(helpDTO.getSourceId()) && helpDTO.getSourceId().equals(sourceId)) {
                return true;
            }
        }

        if (hasAdmin && "admin".equals(sourceId)) {
            return true;
        }

        for (UserRoleHelpDTO helpDTO : userRoleHelpList) {
            if (helpDTO.getRoleId().equals(sourceId)) {
                return true;
            }
        }

        return false;
    }
}
