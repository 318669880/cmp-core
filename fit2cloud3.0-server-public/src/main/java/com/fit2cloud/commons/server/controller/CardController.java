package com.fit2cloud.commons.server.controller;

import com.fit2cloud.commons.server.handle.annotation.I18n;
import com.fit2cloud.commons.server.model.Card;
import com.fit2cloud.commons.server.service.CardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(headers = "Accept=application/json")
public class CardController {

    @Resource
    private CardService cardService;

    /***
     * 获取当前模块 概览也的card(有权限的)
     * @return  List<Card>
     */
    @GetMapping(value = "card/info")
    @I18n
    public List<Card> getCardList() {
        return cardService.getCardList();
    }
}
