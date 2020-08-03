package com.fit2cloud.commons.server.handle;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.Permission;
import com.fit2cloud.commons.server.module.ServerInfo;
import com.fit2cloud.commons.utils.ResultHolder;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: chunxing
 * Date: 2018/5/10  上午11:40
 * Description:
 */
@RestControllerAdvice
public class RestControllerExceptionHandler {
    @Resource
    private ServerInfo serverInfo;

    /*=========== Shiro 异常拦截==============*/
    @ExceptionHandler(ShiroException.class)
    public ResultHolder exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return ResultHolder.error(getErrorMsg(exception.getMessage()));
    }

    private String getErrorMsg(String msg) {
        if (!StringUtils.isEmpty(msg)) {
            int start = msg.indexOf("[") + 1;
            int end = msg.indexOf("]");
            if (end > start) {
                for (Permission permission : serverInfo.getPermissionList()) {
                    if (permission.getId().equals(msg.substring(start, end))) {
                        return String.format(Translator.get("i18n_has_no_permission"), permission.getName());
                    }
                }
            }
        }
        return msg;
    }

    @ExceptionHandler(F2CException.class)
    public ResultHolder f2cExceptionHandler(HttpServletRequest request, HttpServletResponse response, F2CException e) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResultHolder.error(e.getMessage());
    }
}
