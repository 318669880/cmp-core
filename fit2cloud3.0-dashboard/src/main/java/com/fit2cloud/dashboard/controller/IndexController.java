package com.fit2cloud.dashboard.controller;


import com.fit2cloud.commons.server.constants.IndexConstants;
import com.fit2cloud.commons.server.constants.WebConstants;
import com.fit2cloud.commons.server.model.Card;
import com.fit2cloud.dashboard.service.DashboardService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class IndexController {

    @Resource
    private DashboardService dashboardService;

    /**
     * 跳转到无Header主页
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("timestamp", WebConstants.timestamp);
        List<Card> cardList = dashboardService.getCards();
        List<String> jsList = cardList.stream()
                .filter(card -> !StringUtils.isEmpty(card.getTemplateJs()))
                .map(Card::getTemplateJs).collect(Collectors.toList());

        modelAndView.addObject("jsList", jsList);
        String banner = request.getParameter(IndexConstants.BANNER);
        if (StringUtils.equals(banner, IndexConstants.BANNER_FALSE)) {
            modelAndView.setViewName("index");
        } else {
            modelAndView.setViewName("web-public/index");
        }
        return modelAndView;
    }


}
