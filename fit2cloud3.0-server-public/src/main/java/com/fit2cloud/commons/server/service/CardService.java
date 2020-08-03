package com.fit2cloud.commons.server.service;

import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.server.dashboard.CardInfo;
import com.fit2cloud.commons.server.model.Card;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class CardService {

    @Resource
    private CardInfo cardInfo;

    public List<Card> getCardList() {
        List<Card> cardList = JSON.parseArray(JSON.toJSONString(cardInfo.getCardList()), Card.class);
        return cardList.stream()
                .filter(card -> validPermission(card.getPermissions(), card.getLogical()))
                .collect(toList());
    }


    /**
     * 权限校验 不能自己直接取拿session里的权限去校验，用shiro的subject去校验
     *
     * @param permissions
     * @param logical
     * @return
     */
    private boolean validPermission(List<String> permissions, String logical) {
        Subject subject = SecurityUtils.getSubject();

        if (isEmpty(permissions)) {
            return false;
        }

        if ("OR".equalsIgnoreCase(logical)) {
            return permissions.stream()
                    .anyMatch(subject::isPermitted);

        } else if ("AND".equalsIgnoreCase(logical)) {
            return permissions.stream()
                    .allMatch(subject::isPermitted);
        }

        return false;

    }
}
