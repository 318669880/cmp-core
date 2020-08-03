package com.fit2cloud.mc.service;

import com.fit2cloud.commons.server.base.domain.CloudAccountExample;
import com.fit2cloud.commons.server.base.domain.UserRole;
import com.fit2cloud.commons.server.base.domain.UserRoleExample;
import com.fit2cloud.commons.server.base.mapper.*;
import com.fit2cloud.commons.server.constants.CloudAccountConstants;
import com.fit2cloud.commons.server.constants.DashboardColor;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.DashBoardTextDTO;
import com.fit2cloud.commons.server.model.SessionUser;
import com.fit2cloud.commons.server.service.ModuleService;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.server.utils.WorkspaceUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: chunxing
 * Date: 2018/8/3  上午11:46
 * Description:
 */
@Service
public class DashboardService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    private WorkspaceMapper workspaceMapper;
    @Resource
    private OrganizationMapper organizationMapper;
    @Resource
    private CloudAccountMapper cloudAccountMapper;
    @Resource
    private ModuleService moduleService;

    public List<DashBoardTextDTO> getTenant() {
        SessionUser user = SessionUtils.getUser();
        String parentRoleId = user.getParentRoleId();
        long userCount = 0;
        long workspaceCount;
        List<DashBoardTextDTO> textList = new ArrayList<>();
        if (StringUtils.equalsIgnoreCase(parentRoleId, RoleConstants.Id.ADMIN.name())) {
            userCount = userMapper.countByExample(null);
            long organizationCount = organizationMapper.countByExample(null);
            textList.add(new DashBoardTextDTO(Translator.get("i18n_dashboard_organization_count"), organizationCount));
            workspaceCount = workspaceMapper.countByExample(null);
        } else {
            List<String> workspaceIds = WorkspaceUtils.getWorkspaceIdsByOrgIds(SessionUtils.getOrganizationId());
            workspaceCount = workspaceIds.stream().distinct().count();
            if (!CollectionUtils.isEmpty(workspaceIds)) {
                UserRoleExample userRoleExample = new UserRoleExample();
                userRoleExample.createCriteria().andSourceIdIn(workspaceIds);
                List<UserRole> userRoles = userRoleMapper.selectByExample(userRoleExample);
                userCount = userRoles.stream().map(UserRole::getUserId).distinct().count();
            }
        }
        textList.add(new DashBoardTextDTO(Translator.get("i18n_dashboard_user_count"), userCount));
        textList.add(new DashBoardTextDTO(Translator.get("i18n_dashboard_workspace_count"), workspaceCount));
        return textList;
    }


    public List<DashBoardTextDTO> getCloud() {
        List<DashBoardTextDTO> textList = new ArrayList<>();
        CloudAccountExample cloudAccountExample1 = new CloudAccountExample();
        cloudAccountExample1.createCriteria().andStatusEqualTo(CloudAccountConstants.Status.VALID.name());
        long valid = cloudAccountMapper.countByExample(cloudAccountExample1);
        if (valid > 0) {
            textList.add(new DashBoardTextDTO(Translator.get("i18n_dashboard_valid_cloud_account_count"), valid, DashboardColor.GREEN.getColor()));
        } else {
            textList.add(new DashBoardTextDTO(Translator.get("i18n_dashboard_valid_cloud_account_count"), 0));
        }
        CloudAccountExample cloudAccountExample2 = new CloudAccountExample();
        cloudAccountExample2.createCriteria().andStatusEqualTo(CloudAccountConstants.Status.INVALID.name());
        long inValid = cloudAccountMapper.countByExample(cloudAccountExample2);
        if (inValid > 0) {
            textList.add(new DashBoardTextDTO(Translator.get("i18n_dashboard_invalid_cloud_account_count"), inValid, DashboardColor.RED.getColor()));
        } else {
            textList.add(new DashBoardTextDTO(Translator.get("i18n_dashboard_invalid_cloud_account_count"), 0));
        }
        return textList;
    }

    public ArrayList<DashBoardTextDTO> getModule() {
        return moduleService.getModuleToDashboard();
    }
}
