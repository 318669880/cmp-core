package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.commons.server.constants.WebConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.UserRoleDTO;
import com.fit2cloud.commons.server.security.SsoSessionHandler;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.server.swagger.annotation.ApiHasModules;
import com.fit2cloud.commons.server.utils.RoleUtils;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(value = "user", headers = "Accept=application/json")
@Api(tags = Translator.PREFIX + "i18n_swagger_permission_tag" + Translator.SUFFIX)
public class PermissionCommonController {

    @Resource
    private UserCommonService userCommonService;

    /**
     * 获取当前用户能切换的角色
     */
    @ApiOperation(Translator.PREFIX + "i18n_swagger_permission_get" + Translator.SUFFIX)
    @ApiHasModules("dashboard")
    @RequestMapping(value = "source/list", method = RequestMethod.GET)
    @I18n
    public List<UserRoleDTO> getUserRoleList() {
        return userCommonService.getUserRoleList(SessionUtils.getUser().getId());
    }

    @ApiOperation(Translator.PREFIX + "i18n_swagger_permission_switch" + Translator.SUFFIX)
    @ApiHasModules("dashboard")
    @RequestMapping(value = "switch/source/{sourceId}", method = RequestMethod.POST)
    public void switchUserRole(@PathVariable String sourceId, HttpServletResponse response) {
        User user = SessionUtils.getUser();
        if (!RoleUtils.existSourceId(user.getId(), sourceId)) {
            F2CException.throwException("sourceId not exist");
        }
        userCommonService.setLastSourceId(user, sourceId);
        if (GlobalConfigurations.isReleaseMode()) {
            Cookie cookie = new Cookie(GlobalConfigurations.getCookieName(), "deleteMe");
            cookie.setPath(WebConstants.ROOT_PATH);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        } else {
            SecurityUtils.getSubject().login(new UsernamePasswordToken(SessionUtils.getUser().getId(), SsoSessionHandler.random));
        }
    }


}
