package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.commons.server.base.domain.UserKey;
import com.fit2cloud.commons.server.constants.RoleConstants;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.ExcelExportRequest;
import com.fit2cloud.commons.server.model.UserDTO;
import com.fit2cloud.commons.server.service.UserKeysService;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.server.utils.WorkspaceUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.dto.UserOperateDTO;
import com.fit2cloud.mc.dto.UserRoleOperateDTO;
import com.fit2cloud.mc.dto.request.*;
import com.fit2cloud.mc.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@RequestMapping("user")
@RestController
@Api(tags = Translator.PREFIX + "i18n_mc_user_tag" + Translator.SUFFIX)
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserKeysService userKeysService;

    //用户列表
    @ApiOperation(value = Translator.PREFIX + "i18n_mc_user_list" + Translator.SUFFIX)
    @PostMapping(value = "/{goPage}/{pageSize}")
    @RequiresPermissions(PermissionConstants.USER_READ)
    @I18n
    public Pager<List<UserDTO>> paging(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody UserRequest request) {
        Map<String, Object> map = BeanUtils.objectToMap(request);
        String parentRoleId = SessionUtils.getUser().getParentRoleId();
        if (StringUtils.equals(parentRoleId, RoleConstants.Id.ORGADMIN.name())) {
            List<String> resourceIds = WorkspaceUtils.getWorkspaceIdsByOrgIds(SessionUtils.getOrganizationId());
            resourceIds.add(SessionUtils.getOrganizationId());
            map.put("resourceIds", resourceIds);
            Page page = PageHelper.startPage(goPage, pageSize, true);
            return PageUtils.setPageInfo(page, userService.paging(map));
        }
        if (StringUtils.equals(parentRoleId, RoleConstants.Id.ADMIN.name())) {
            Page page = PageHelper.startPage(goPage, pageSize, true);
            return PageUtils.setPageInfo(page, userService.paging(map));
        }
        return new Pager<>();
    }


    @ApiOperation(value = Translator.PREFIX + "i18n_mc_user_delete" + Translator.SUFFIX)
    @GetMapping(value = "/delete/{userId}")
    @RequiresPermissions(PermissionConstants.USER_DELETE)
    public void deleteUser(@PathVariable(value = "userId") String userId) {
        userService.delete(userId);
    }

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_user_create_org" + Translator.SUFFIX)
    @PostMapping(value = "/add/organization")
    @RequiresPermissions(PermissionConstants.USER_CREATE)
    @I18n
    public UserDTO createOrganizationUser(@RequestBody CreateOrganizationUserRequest request) {
        return userService.createOrganizationUser(request);
    }

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_user_create_user" + Translator.SUFFIX)
    @PostMapping(value = "/add/workspace")
    @RequiresPermissions(PermissionConstants.USER_CREATE)
    @I18n
    public UserDTO createWorkspaceUser(@RequestBody CreateWorkspaceUserRequest request) {
        return userService.CreateWorkspaceUser(request);
    }

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_user_create" + Translator.SUFFIX)
    @PostMapping("/add")
    @RequiresPermissions(PermissionConstants.USER_CREATE)
    @I18n
    public UserDTO insertUser(@RequestBody CreateUserRequest request) {
        return userService.insert(request);
    }

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_add_role" + Translator.SUFFIX)
    @PostMapping(value = "/add/role")
    @RequiresPermissions(PermissionConstants.USER_CREATE)
    @I18n
    public UserDTO createOrganizationUser(@RequestBody AddRoleToUserRequest request) {
        return userService.addRoleToUser(request);
    }

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_delete_role" + Translator.SUFFIX)
    @PostMapping(value = "/delete/role")
    @RequiresPermissions(PermissionConstants.USER_CREATE)
    @I18n
    public UserDTO deleteRoleFromUser(@RequestBody DeleteRoleFromUserRequest request) {
        return userService.deleteRoleFromUser(request);
    }

    @PostMapping("/update")
    @RequiresPermissions(PermissionConstants.USER_EDIT)
    public void updateUser(@RequestBody UserOperateDTO user) {
        userService.update(user);
    }

    @PostMapping("/role/add")
    @RequiresPermissions(PermissionConstants.USER_ADD_ROLE)
    public void addUserRole(@RequestBody UserRoleOperateDTO roleOperateDTO) {
        userService.addUserRole(roleOperateDTO);
    }


    @GetMapping(value = "/ids/{userId}")
    @RequiresPermissions(PermissionConstants.USER_DELETE)
    public Object resourceIds(@PathVariable String userId) {
        return userService.resourceIds(userId);
    }

    @PostMapping(value = "/reset/password")
    @RequiresPermissions(PermissionConstants.USER_RESET_PASSWORD)
    public void resetPassword(@RequestBody User user) {
        userService.resetPassword(user);
    }

    @PostMapping(value = "/active")
    @RequiresPermissions(PermissionConstants.USER_DISABLED)
    public void changeActive(@RequestBody UserDTO user) {
        userService.changeActive(user);
    }

    @GetMapping(value = "/role/info/{userId}")
    @RequiresPermissions(value = PermissionConstants.USER_DELETE)
    public Object roleInfo(@PathVariable String userId) {
        return userService.roleInfo(userId);
    }

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_user_api_list" + Translator.SUFFIX)
    @GetMapping(value = "/key/list/{userId}")
    @RequiresPermissions(value = PermissionConstants.USER_KEY_READ)
    public List<UserKey> listApiKeys(@PathVariable String userId) {
        return userKeysService.getUserKeysInfo(userId);
    }

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_user_api_create" + Translator.SUFFIX)
    @PostMapping(value = "/key/create")
    @RequiresPermissions(value = PermissionConstants.USER_KEY_EDIT)
    public UserKey createApiKey(@RequestBody CreateUserKeyRequest createUserKeyRequest) {
        return userKeysService.generateUserKey(createUserKeyRequest.getUserId());
    }

    @ApiOperation(value = Translator.PREFIX + "i18n_mc_user_api_delete" + Translator.SUFFIX)
    @GetMapping(value = "/key/delete/{keyId}")
    @RequiresPermissions(value = PermissionConstants.USER_KEY_EDIT)
    public void deleteApiKey(@PathVariable String keyId) {
        userKeysService.deleteUserKey(keyId);
    }

    @RequiresPermissions(value = PermissionConstants.USER_READ)
    @PostMapping(value = "resource/{resourceType}/{userId}/{roleId}/{goPage}/{pageSize}")
    public Object resourcePaging(@PathVariable String resourceType, @PathVariable String userId, @PathVariable String roleId, @PathVariable int goPage, @PathVariable int pageSize) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, userService.resourcePaging(resourceType, userId, roleId));
    }

    @PostMapping(value = "export")
    @RequiresPermissions(value = {PermissionConstants.USER_READ})
    public ResponseEntity<byte[]> export(@RequestBody ExcelExportRequest request) throws Exception {
        byte[] bytes = null;
        String parentRoleId = SessionUtils.getUser().getParentRoleId();
        if (StringUtils.equals(parentRoleId, RoleConstants.Id.ORGADMIN.name())) {
            List<String> resourceIds = WorkspaceUtils.getWorkspaceIdsByOrgIds(SessionUtils.getOrganizationId());
            request.getParams().put("resourceIds", resourceIds);
            bytes = userService.exportUsers(request);
        }
        if (StringUtils.equals(parentRoleId, RoleConstants.Id.ADMIN.name())) {
            bytes = userService.exportUsers(request);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "Excel");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .headers(headers)
                .body(bytes);
    }
}
