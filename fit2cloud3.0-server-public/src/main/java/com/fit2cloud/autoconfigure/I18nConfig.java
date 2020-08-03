package com.fit2cloud.autoconfigure;

import com.fit2cloud.commons.server.i18n.I18nManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liqiang on 2019/4/1.
 */
@Configuration
public class I18nConfig {

    @Bean
    @ConditionalOnMissingBean
    public I18nManager i18nManager() {
        List<String> dirs = new ArrayList<>();
        dirs.add("server-public/i18n/");
        dirs.add("web-public/fit2cloud/i18n/");
        dirs.add("static/project/i18n/");
        return new I18nManager(dirs);
    }
}
