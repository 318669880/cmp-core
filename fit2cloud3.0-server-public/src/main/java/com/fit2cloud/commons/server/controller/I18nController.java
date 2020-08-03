package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.constants.I18nConstants;
import com.fit2cloud.commons.server.constants.Lang;
import com.fit2cloud.commons.server.constants.WebConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.handle.annotation.NoResultHolder;
import com.fit2cloud.commons.server.i18n.I18nManager;
import com.fit2cloud.commons.server.service.I18nService;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by liqiang on 2019/4/1.
 */
@RestController
public class I18nController {

    private static final int FOR_EVER = 3600 * 24 * 30 * 12 * 10; //10 years in second
    @Resource
    private UserCommonService userCommonService;

    @NoResultHolder
    @GetMapping("/anonymous/i18n/{lang}.json")
    public Object getTrans(@PathVariable String lang) throws IOException {
        return I18nManager.getWebI18nMap().get(lang.toLowerCase());
    }

    @NoResultHolder
    @GetMapping("/anonymous/all/i18n/{lang}.json")
    public Object getAllTrans(@PathVariable String lang) throws IOException {
        return I18nService.langWeb(lang.toLowerCase());
    }

    @GetMapping("lang/change/{lang}")
    public void changeLang(@PathVariable String lang, HttpServletResponse response) {
        Lang targetLang = Lang.getLangWithoutDefault(lang);
        if (targetLang == null) {
            response.setStatus(HttpStatus.SC_NOT_ACCEPTABLE);
            F2CException.throwException("Invalid parameter: " + lang + ", the acceptable parameters are: " + Lang.values());
        }
        userCommonService.setLanguage(targetLang.getDesc());
        Cookie cookie = new Cookie(I18nConstants.LANG_COOKIE_NAME, targetLang.getDesc());
        cookie.setPath(WebConstants.ROOT_PATH);
        cookie.setMaxAge(FOR_EVER);
        response.addCookie(cookie);
        //重新登录
        if (GlobalConfigurations.isReleaseMode()) {
            Cookie f2cCookie = new Cookie(GlobalConfigurations.getCookieName(), "deleteMe");
            f2cCookie.setPath(WebConstants.ROOT_PATH);
            f2cCookie.setMaxAge(0);
            response.addCookie(f2cCookie);
        }
    }
}
