package com.fit2cloud.commons.server.security;


import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.SessionUser;
import com.fit2cloud.commons.server.module.ServerInfo;
import com.fit2cloud.commons.server.service.MenuService;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.EncryptUtils;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;

import javax.annotation.Resource;
import java.util.Set;


/**
 * 自定义Realm 注入service 可能会导致在 service的aop 失效，例如@Transactional,
 * 解决方法：
 * <p>
 * 1. 这里改成注入mapper，这样mapper 中的事务失效<br/>
 * 2. 这里仍然注入service，在配置ShiroConfig 的时候不去set realm, 等到spring 初始化完成之后
 * set realm
 * </p>
 */
public class ShiroDBRealm extends AuthorizingRealm {

    private Logger logger = LogUtil.getLogger();

    @Resource
    private UserCommonService userCommonService;
    @Resource
    private MenuService menuService;
    @Resource
    private ServerInfo serverInfo;

    /**
     * 权限认证 注意： name其实是用户id
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 登录认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String userId = token.getUsername();
        String password = String.valueOf(token.getPassword());
        String msg;
        User user;
        try {
            user = userCommonService.getUserBySth(userId);
        } catch (Exception e) {
            throw new AuthenticationException(e.getMessage());
        }

        if (user == null) {
            msg = "not exist user is trying to login, user:" + userId;
            logger.warn(msg);
            throw new UnknownAccountException(String.format(Translator.get("i18n_ex_user_not_exist"), userId));
        }
        if (!user.getActive()) {
            msg = "user is not active, user:" + userId;
            logger.warn(msg);
            throw new LockedAccountException(String.format(Translator.get("i18n_ex_user_disable"), userId));
        }
        if (!GlobalConfigurations.isReleaseMode() || StringUtils.equals(EncryptUtils.md5Encrypt(password).toString(), user.getPassword())
                || StringUtils.equalsIgnoreCase(password, SsoSessionHandler.random)) {
            SessionUser sessionUser;

            try {
                sessionUser = SessionUser.fromUser(user);
            } catch (Exception e) {
                throw new AuthenticationException(e.getMessage());
            }
            SessionUtils.putUser(sessionUser);
            if (CollectionUtils.isEmpty(sessionUser.getRoleIdList())) {
                throw new AuthenticationException(String.format(Translator.get("i18n_ex_user_no_role"), userId));
            }
            SecurityUtils.getSubject().getSession().setAttribute(serverInfo.getModule().getId(), menuService.getPermissionIdList(sessionUser.getRoleIdList()));
            SecurityUtils.getSubject().getSession().setAttribute(serverInfo.getModule().getId() + "PERMISSION", menuService.getPermissions(sessionUser.getRoleIdList()));

            return new SimpleAuthenticationInfo(userId, password, getName());
        } else {
            msg = "wrong password input, " + userId + ", wrong password: " + password;
            logger.warn(msg);
            throw new IncorrectCredentialsException(Translator.get("i18n_ex_password_not_correct"));
        }
    }

    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        Session session = SecurityUtils.getSubject().getSession();
        Object o = session.getAttribute(serverInfo.getModule().getId());
        Set<String> permissions;
        if (o == null) {
            permissions = menuService.getPermissionIdList(SessionUtils.getRoleIdList());
            session.setAttribute(serverInfo.getModule().getId(), permissions);
            session.setAttribute(serverInfo.getModule().getId() + "PERMISSION", menuService.getPermissions(SessionUtils.getRoleIdList()));
        } else {
            permissions = (Set<String>) o;
        }

        return permissions.contains(permission);
    }
}
