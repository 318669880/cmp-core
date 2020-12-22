package com.fit2cloud.commons.server.constants;

import com.fit2cloud.commons.server.base.domain.SystemParameter;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.service.FileStoreService;
import com.fit2cloud.commons.server.service.ParameterCommonService;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liqiang on 2018/6/13.
 */
public class WebConstants {

    public static final long timestamp = System.currentTimeMillis();

    public static final String WEB_PUBLIC_CONTEXT = "web-public";

    public static final String ROOT_PATH = "/";

    public static Map<String, Object> getUiConfiguration() {
        Map<String, Object> result = new HashMap<>();
        result.put("userLocale", Translator.getLangDes());
        result.put("timestamp", String.valueOf(WebConstants.timestamp));
        result.put("IndexConstants", getUiInfo());
        return result;
    }

    public static Map<String, String> getUiInfo() {
        Map<String, String> ui = new HashMap<>();
        ui.put("release.model", String.valueOf(GlobalConfigurations.isReleaseMode()));
        try {
            List<SystemParameter> systemParameterList = CommonBeanFactory.getBean(ParameterCommonService.class).uiInfo();
            FileStoreService fileStoreService = CommonBeanFactory.getBean(FileStoreService.class);
            if (CollectionUtils.isNotEmpty(systemParameterList)) {
                systemParameterList.forEach(systemParameter -> {
                    ui.put(systemParameter.getParamKey(), systemParameter.getParamValue());
                });
            }
            if (StringUtils.isBlank(ui.get(ParamConstants.UI.FAVICON.getValue()))) {
                ui.put(ParamConstants.UI.FAVICON.getValue(), "/web-public/fit2cloud/img/fit2cloud/favicon.bmp?_t=" + timestamp);
            } else {
                if (fileStoreService.isFileExists(ui.get(ParamConstants.UI.FAVICON.getValue()))) {
                    ui.put(ParamConstants.UI.FAVICON.getValue(), "/anonymous/favicon?_t=1000000000000&_f=" + ui.get(ParamConstants.UI.FAVICON.getValue()));
                }
            }
            if (StringUtils.isBlank(ui.get(ParamConstants.UI.LOGO.getValue()))) {
                ui.put(ParamConstants.UI.LOGO.getValue(), "/web-public/fit2cloud/img/fit2cloud/white-logo.png?_t=" + timestamp);
            } else {
                if (fileStoreService.isFileExists(ui.get(ParamConstants.UI.LOGO.getValue()))) {
                    ui.put(ParamConstants.UI.LOGO.getValue(), "/anonymous/logo?_t=1000000000000&_f=" + ui.get(ParamConstants.UI.LOGO.getValue()));
                }
            }
            if (StringUtils.isBlank(ui.get(ParamConstants.UI.SYSTEM_NAME.getValue()))) {
                ui.put(ParamConstants.UI.SYSTEM_NAME.getValue(), Translator.get("i18n_p_ui_system_name"));
            }
            if (StringUtils.isBlank(ui.get(ParamConstants.UI.THEME_PRIMARY.getValue()))) {
                ui.put(ParamConstants.UI.THEME_PRIMARY.getValue(), "#0A7BE0");
            }

            if (StringUtils.isBlank(ui.get(ParamConstants.UI.THEME_ACCENT.getValue()))) {
                ui.put(ParamConstants.UI.THEME_ACCENT.getValue(), "#F48FB1");
            }

            if (StringUtils.isBlank(ui.get(ParamConstants.UI.TITLE.getValue()))) {
                ui.put(ParamConstants.UI.TITLE.getValue(), "CloudExplorer " + Translator.get("i18n_p_ui_system_name"));
            }
            if (StringUtils.isBlank(ui.get(ParamConstants.UI.LOGIN_TITLE.getValue()))) {
                ui.put(ParamConstants.UI.LOGIN_TITLE.getValue(), "CloudExplorer " + Translator.get("i18n_p_ui_system_name"));
            }
            if (StringUtils.isBlank(ui.get(ParamConstants.UI.LOGIN_IMG.getValue()))) {
                ui.put(ParamConstants.UI.LOGIN_IMG.getValue(), "/web-public/fit2cloud/img/fit2cloud/login-banner.png?_t=" + timestamp);
            } else {
                if (fileStoreService.isFileExists(ui.get(ParamConstants.UI.LOGIN_IMG.getValue()))) {
                    ui.put(ParamConstants.UI.LOGIN_IMG.getValue(), "/anonymous/login/img?_t=1000000000000&_f=" + ui.get(ParamConstants.UI.LOGIN_IMG.getValue()));
                }
            }
            if (StringUtils.isBlank(ui.get(ParamConstants.UI.SUPPORT_NAME.getValue()))) {
                ui.put(ParamConstants.UI.SUPPORT_NAME.getValue(), Translator.get("i18n_p_ui_support_name"));
            }
            if (StringUtils.isBlank(ui.get(ParamConstants.UI.SUPPORT_URL.getValue()))) {
                ui.put(ParamConstants.UI.SUPPORT_URL.getValue(), "mailto:support@fit2cloud.com");
            }
        } catch (Exception e) {
            LogUtil.error("failed to getUiConfiguration", e);
            throw new RuntimeException(e);
        }
        return ui;
    }

    public static Map<String, Object> getErrorVariables(String path, Integer httpStatus, String error) {
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", System.currentTimeMillis());
        result.put("path", path);
        result.put("status", httpStatus);
        result.put("error", error);
        return result;

    }

    public static HttpServletRequest getRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (NullPointerException npe) {
            return null;
        }
    }
}
