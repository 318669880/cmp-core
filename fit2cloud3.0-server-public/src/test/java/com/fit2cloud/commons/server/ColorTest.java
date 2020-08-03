package com.fit2cloud.commons.server;

import com.fit2cloud.commons.utils.CodingUtil;
import com.fit2cloud.commons.utils.UUIDUtil;

/**
 * Author: chunxing
 * Date: 2018/8/29  下午4:15
 * Description:
 */
public class ColorTest {

    public static void main(String[] args) {
        String accessKey = "J7Xly6m7pdxWOGL3";
        String secretKey = "H2toemn5SVYfnlCq";
        String signature = CodingUtil.aesEncrypt(accessKey + "|" + UUIDUtil.newUUID() + "|" + System.currentTimeMillis(), secretKey, accessKey);
        System.out.println(signature);
    }
}
