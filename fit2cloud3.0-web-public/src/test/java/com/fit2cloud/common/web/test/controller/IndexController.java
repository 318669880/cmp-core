package com.fit2cloud.common.web.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {

    private static final Long timestamp = System.currentTimeMillis();

    /**
     * 跳转到无Header主页
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("timestamp", timestamp);
        model.addAttribute("frameTargetSrc", "/example");
        return "index";
    }


    /**
     * 跳转到无Header主页
     */
    @RequestMapping(value = "/example", method = RequestMethod.GET)
    public String example(Model model) {
        model.addAttribute("timestamp", timestamp);
        return "example";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
        model.addAttribute("timestamp", timestamp);
        return "login";
    }
}
