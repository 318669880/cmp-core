package com.fit2cloud.dashboard.controller;

import com.fit2cloud.commons.server.base.domain.OperationLog;
import com.fit2cloud.commons.server.constants.I18nConstants;
import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.model.Card;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.dashboard.controller.request.ActivityRequest;
import com.fit2cloud.dashboard.module.ConditionItem;
import com.fit2cloud.dashboard.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("dashboard")
@Api(tags = Translator.PREFIX + "i18n_dashboard_swagger_activity_tag" + Translator.SUFFIX)
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @GetMapping("card/list")
    public List<Card> getCardList() {
        return dashboardService.getCardList();
    }

    @PostMapping("preference")
    public void savePreference(@RequestBody List<Card> cards) {
        dashboardService.savePreference(cards);
    }

    @ApiOperation(Translator.PREFIX + "i18n_dashboard_swagger_activity_list" + Translator.SUFFIX)
    @PostMapping("activity/{goPage}/{pageSize}")
    @I18n(I18nConstants.CLUSTER)
    public Pager<List<OperationLog>> getActivity(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ActivityRequest request) {
        return dashboardService.getActivity(goPage, pageSize, BeanUtils.objectToMap(request));
    }

    @GetMapping("activity/{key}")
    @I18n(I18nConstants.CLUSTER)
    public List<ConditionItem> conditions(@PathVariable String key) {
        return dashboardService.conditions(key);
    }


}
