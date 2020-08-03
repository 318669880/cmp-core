package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(headers = "Accept=application/json")
public class StatsController {

    @Resource
    private StatsService statsService;

    @GetMapping("anonymous/healthcheck")
    @Deprecated
    public List<Map<String, String>> healthCheck(HttpServletRequest request, HttpServletResponse response) {
        return statsService.healthCheck(request, response);
    }

    @GetMapping("anonymous/status")
    public List<Map<String, String>> statusCheck(HttpServletRequest request, HttpServletResponse response) {
        return statsService.healthCheck(request, response);
    }

}
