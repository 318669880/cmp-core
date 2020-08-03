package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.SessionUser;
import com.fit2cloud.commons.server.model.UserDTO;
import com.fit2cloud.commons.server.model.UserRoleDTO;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.server.swagger.annotation.ApiHasModules;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "user", headers = "Accept=application/json")
@Api(tags = Translator.PREFIX + "i18n_swagger_user_tag" + Translator.SUFFIX)
public class UserCommonController {


    @Resource
    private UserCommonService userCommonService;

    /**
     * 用户修改全局密码
     */
    @RequestMapping("current/reset/password")
    public void restPassword(@RequestBody Map<String, String> param) {
        userCommonService.restPassword(param);
    }

    /**
     * 用户修改全局密码
     */
    @RequestMapping("current/edit/info")
    public void editUserInfo(@RequestBody User user) {
        userCommonService.editUserInfo(user);
    }

    @RequestMapping("current/info")
    public SessionUser getCurrentUserInfo() {
        SessionUser sessionUser = new SessionUser();
        BeanUtils.copyBean(sessionUser, SessionUtils.getUser());
        UserDTO userDTO = userCommonService.getUserDTO(SessionUtils.getUser().getId());
        BeanUtils.copyBean(sessionUser, userDTO);
        sessionUser.setPassword(null);
        return sessionUser;
    }

    /**
     * 获取当前用户信息
     *
     * @return
     */
    @GetMapping("info")
    @ApiOperation(value = Translator.PREFIX + "i18n_swagger_user_info" + Translator.SUFFIX)
    @ApiHasModules("dashboard")
    @I18n
    public UserDTO getUserInfo() {
        return userCommonService.getUserDTO(SessionUtils.getUser().getId());
    }

    /**
     * 当前父角色
     */
    @RequestMapping("current/parent/role")
    public String currentParentRole() {
        return SessionUtils.getUser().getParentRoleId();
    }


    /**
     * 获取用户能切换的角色
     */
    @RequestMapping(value = "switch/source/{userId}", method = RequestMethod.GET)
    @I18n
    public List<UserRoleDTO> getUserRoleList(@PathVariable String userId) {
        return userCommonService.getUserRoles(userId);
    }


}
