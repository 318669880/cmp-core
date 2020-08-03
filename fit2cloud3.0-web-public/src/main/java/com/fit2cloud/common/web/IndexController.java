package com.fit2cloud.common.web;

import com.fit2cloud.common.web.template.WebPublicConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Configuration
public class IndexController {

    /**
     * 跳转到通知主页
     */
    @RequestMapping(value = "notification-index.html", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("timestamp", WebPublicConfig.timestamp);
        return "web-public/project/html/notification-list";
    }

    @RequestMapping(value = "anonymous/web-public/login", method = RequestMethod.GET)
    public String login(Model model) {
        model.addAttribute("timestamp", WebPublicConfig.timestamp);
        return "web-public/login";
    }
}
