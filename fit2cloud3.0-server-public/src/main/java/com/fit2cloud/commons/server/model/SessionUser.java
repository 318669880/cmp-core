package com.fit2cloud.commons.server.model;

import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.commons.server.constants.ParamConstants;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.constants.WebConstants;
import com.fit2cloud.commons.server.security.SsoSessionHandler;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.server.utils.RoleUtils;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.LogUtil;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.List;

public class SessionUser extends User implements Serializable {

    private static final long serialVersionUID = -7489978940477838898L;

    @ApiModelProperty("角色ID列表")
    private List<String> roleIdList;
    @ApiModelProperty("父角色ID")
    private String parentRoleId;
    @ApiModelProperty("工作空间ID")
    private String workspaceId;
    @ApiModelProperty("组织ID")
    private String organizationId;
    @ApiModelProperty("权限ID")
    private String sourceId;
    @ApiModelProperty("权限名称")
    private String sourceName;
    @ApiModelProperty("语言")
    private String lang;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getParentRoleId() {
        return parentRoleId;
    }

    public void setParentRoleId(String parentRoleId) {
        this.parentRoleId = parentRoleId;
    }

    public List<String> getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(List<String> roleIdList) {
        this.roleIdList = roleIdList;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public static SessionUser fromUser(User old) {
        UserCommonService userCommonService = (UserCommonService) CommonBeanFactory.getBean("userCommonService");
        User user = userCommonService.getUserById(old.getId());
        SessionUser sessionUser = new SessionUser();
        BeanUtils.copyBean(sessionUser, user);
        String sourceId = SessionUtils.getHttpHeader(SsoSessionHandler.SSO_SOURCE_ID);
        if (!StringUtils.isEmpty(sourceId)) {
            String httpHeader = SessionUtils.getHttpHeader(SsoSessionHandler.SSO_HEADER_AUTH_NAME);
            if (!RoleUtils.existSourceId(SsoSessionHandler.SessionGenerator.fromId(httpHeader), sourceId)) {
                throw new RuntimeException("sourceId不存在");
            }
            sessionUser.setLastSourceId(sourceId);
            user.setLastSourceId(sourceId);
        }

        if ("admin".equals(user.getLastSourceId()) && RoleUtils.existSourceId(user.getId(), "admin")) {
            sessionUser.setRoleIdList(RoleUtils.getAdminRoleIdList(user.getId()));
            sessionUser.setSourceId(user.getLastSourceId());
            sessionUser.setParentRoleId(RoleConstants.Id.ADMIN.name());
            sessionUser.setSourceName(WebConstants.getUiInfo().get(ParamConstants.UI.SYSTEM_NAME.getValue()));
        } else {
            if ("admin".equals(user.getLastSourceId())) {
                sessionUser.setLastSourceId(null);
            }
            RoleUtils.convertUserRole(sessionUser);
        }

        if (LogUtil.getLogger().isTraceEnabled()) {
            LogUtil.getLogger().trace("sessionUser info {userId:" + sessionUser.getId() + ",last sourceId:" + sessionUser.getLastSourceId() + "}");
        }

        return sessionUser;
    }
}
