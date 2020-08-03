package com.fit2cloud.commons.server.dashboard;

import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.server.model.Card;
import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class CardInfo {

    @Resource
    private Environment environment;

    private List<Card> cardList = new ArrayList<>();

    @PostConstruct
    public void initCardInfo() {
        InputStream inputStream = null;
        try {
            PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            if (!patternResolver.getResource("dashboard.json").exists()) {
                LogUtil.info("dashboard.json not found in this module.");
            } else {
                inputStream = patternResolver.getResource("dashboard.json").getInputStream();
                String json = IOUtils.toString(new BufferedReader(new InputStreamReader(inputStream, UTF_8)));
                cardList.addAll(JSON.parseArray(json, Card.class));
            }
            for (Card card : cardList) {
                card.setId(environment.getProperty("spring.application.name", "") + card.getId());
            }

            if (isKubernetes()) {
                cardList = cardList.stream().filter(CardInfo::filter).collect(Collectors.toList());
            }

        } catch (Exception e) {
            LogUtil.error("parse dashboard.json error:" + e.getMessage());
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    private boolean isKubernetes() {
        return environment.getProperty("KUBERNETES_PORT") != null && StringUtils.endsWithIgnoreCase(environment.getProperty("spring.application.name"), "management-center");
    }

    private static boolean filter(Card card) {
        return !StringUtils.endsWithIgnoreCase("management-center4", card.getId());
    }


    public List<Card> getCardList() {
        return cardList;
    }
}
