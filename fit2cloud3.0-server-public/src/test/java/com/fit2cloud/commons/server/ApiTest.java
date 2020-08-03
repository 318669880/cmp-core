package com.fit2cloud.commons.server;

import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.utils.CodingUtil;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.commons.utils.UUIDUtil;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class ApiTest {


    @Test
    public void test() {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> httpEntity = new HttpEntity<>(getHttpHeaders());
        try {
            ResponseEntity<ResultHolder> entity = restTemplate.exchange("https://rdtest2.fit2cloud.com/management-center/user/current/info", HttpMethod.GET, httpEntity, ResultHolder.class);
            System.out.println(JSON.toJSON(entity.getBody().getData()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accessKey", "J7Xly6m7pdxWOGL3");
        httpHeaders.add("signature", "Iu6HKITRpCq+rNojePcYUgP96TkbabDNdc3HS9Kuo2JsALcRqhXKN4hE6chPN8Up1c71GR7+vT5C7siHaJ+it52CGibsBc9x9si0BlA+kgE=");
        httpHeaders.add("sourceId", "admin");
        return httpHeaders;
    }


    public static void main(String[] args) {
        String accessKey = "x8eMvuKxpaBSNRJ7";
        String secretKey = "adsyA2cGQtAmLGZE";
        String signature = CodingUtil.aesEncrypt(accessKey + "|" + UUIDUtil.newUUID() + "|" + System.currentTimeMillis(), secretKey, accessKey);
        System.out.println(signature);
    }
}
