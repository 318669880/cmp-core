package com.fit2cloud.dashboard.service;

import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.server.base.domain.OperationLog;
import com.fit2cloud.commons.server.base.mapper.ext.ExtOperationLogMapper;
import com.fit2cloud.commons.server.model.Card;
import com.fit2cloud.commons.server.service.ConditionService;
import com.fit2cloud.commons.server.service.MicroService;
import com.fit2cloud.commons.server.service.ModuleService;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.dashboard.dao.DashboardMapper;
import com.fit2cloud.dashboard.module.ConditionItem;
import com.fit2cloud.dashboard.module.Dashboard;
import com.fit2cloud.dashboard.module.DashboardExample;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: chunxing
 * Date: 2018/8/2  下午6:06
 * Description:
 */
@Service
public class DashboardService {

    private static List<Card> cards = null;
    @Resource
    private DashboardMapper dashboardMapper;
    @Resource
    private MicroService microService;
    @Resource
    private ModuleService moduleService;
    @Resource
    private ExtOperationLogMapper extOperationLogMapper;
    @Resource
    private ConditionService conditionService;

    public void savePreference(List<Card> cardList) {
        if (!CollectionUtils.isEmpty(cardList)) {
            for (Card card : cardList) {

                DashboardExample dashboardExample = new DashboardExample();
                dashboardExample.createCriteria().andUserIdEqualTo(SessionUtils.getUser().getId()).andCardIdEqualTo(card.getId());
                dashboardMapper.deleteByExample(dashboardExample);

                Dashboard dashboard = new Dashboard();
                dashboard.setId(UUIDUtil.newUUID());
                dashboard.setUserId(SessionUtils.getUser().getId());
                dashboard.setCardId(card.getId());
                dashboard.setPosition(card.getPosition());
                dashboard.setDisplay(card.isDisplay());
                dashboard.setSort(card.getOrder());
                dashboardMapper.insert(dashboard);
            }
        }
    }


    public Pager<List<OperationLog>> getActivity(int goPage, int pageSize, Map<String, Object> param) {

        conditionService.convertParam(param);
        List<OperationLog> activityLog = new ArrayList<>();
        Page page = PageHelper.startPage(goPage, pageSize, true);
        if (!"other".equalsIgnoreCase(SessionUtils.getUser().getParentRoleId())) {
            activityLog = extOperationLogMapper.getActivityLog(param);
        }

        return PageUtils.setPageInfo(page, activityLog);
    }

    public List<Card> getCardList() {
        getCards();

        DashboardExample dashboardExample = new DashboardExample();
        dashboardExample.createCriteria().andUserIdEqualTo(SessionUtils.getUser().getId());
        List<Dashboard> dashboardList = dashboardMapper.selectByExample(dashboardExample);

        List<Card> cardList = JSON.parseArray(JSON.toJSONString(cards), Card.class);
        for (Card card : cardList) {
            for (Dashboard dashboard : dashboardList) {
                if (card.getId().equalsIgnoreCase(dashboard.getCardId())) {
                    card.setDisplay(dashboard.getDisplay());
                    card.setPosition(dashboard.getPosition());
                    card.setOrder(dashboard.getSort());
                }
            }
        }
        cardList.sort(Comparator.comparing(Card::getOrder));
        return cardList;
    }

    public List<Card> getCards() {

        List<Card> resultList = new ArrayList<>();

        List<String> serviceIds = moduleService.getAuthAndEnableServiceList();

        serviceIds.remove("gateway");

        List<ResultHolder> resultHolders = microService.getForResultHolder(serviceIds, "/card/info", 10);
        for (ResultHolder resultHolder : resultHolders) {
            if (resultHolder.isSuccess()) {
                resultList.addAll(JSON.parseArray(JSON.toJSONString(resultHolder.getData()), Card.class));
            }
        }

        cards = resultList;

        return resultList;

    }


    public List<ConditionItem> conditions(String key) {
        if (StringUtils.isEmpty(key)) {
            return new ArrayList<>();
        }

        List<String> conditions = extOperationLogMapper.getConditions(key);

        return conditions.stream().filter(s -> !StringUtils.isEmpty(s)).map(s -> new ConditionItem(s, s)).collect(Collectors.toList());
    }
}
