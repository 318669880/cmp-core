package com.fit2cloud.commons.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JavaBeanSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.util.TypeUtils;
import com.fit2cloud.commons.server.constants.Lang;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.security.ApiKeyHandler;
import com.fit2cloud.commons.server.service.RoleCommonService;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.CodingUtil;
import com.fit2cloud.commons.utils.UUIDUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by liqiang on 2018/7/19.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LiqiangTest {

    @Resource
    RoleCommonService roleCommonService;

    @Test
    public void test1() throws Exception {
        String accessKey = "psbbCe9qtW3laRBk";
        String secretKey = "ZdZ5nbhTBX6ShMb2";
        String signature = CodingUtil.aesEncrypt(accessKey + "|" + UUIDUtil.newUUID() + "|" + System.currentTimeMillis(), secretKey, accessKey);
        System.out.println(signature);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("accessKey", accessKey);
        request.addHeader("signature", signature);
        String userId = ApiKeyHandler.getUser(request);
        System.out.println(userId);
    }

    @Test
    public void test2() throws Exception {
        String a = "asdfasdf_$[{i18n_exception}]_adsf_$[{ex_tag_import_column_count}]_adsf_$[{_end";
        System.out.println(Translator.gets(a));
    }

    @Test
    public void test3() throws Exception {
        Object o = Translator.clusterGets(Lang.en_US, roleCommonService.listSystemRoles());
        System.out.println(JSON.toJSONString(o));
    }


}
