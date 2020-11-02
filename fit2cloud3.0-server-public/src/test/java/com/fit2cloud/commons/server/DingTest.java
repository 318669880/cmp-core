package com.fit2cloud.commons.server;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.request.OapiUserGetByMobileRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.dingtalk.api.response.OapiUserGetByMobileResponse;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.util.Arrays;

/**
 * @Author gin
 * @Date 2020/11/1 9:18 上午
 */
public class DingTest {
    public static void main(String[] args) throws Exception {
//        robot();
        msg();
    }

    public static void robot() throws Exception {
        Long timestamp = System.currentTimeMillis();
        String secret = "SECa8beb756f8579c9e3c373e1cc2dce0875fadbc98c518fd6644a9bc7cb0a05068";
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");

        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?access_token=92ba04610034a5c8943daa57748b6dccc2012108efab0d53075f6da01ecac9aa&timestamp=" + timestamp + "&sign=" + sign);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent("测试文本消息");
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtMobiles(Arrays.asList("18368109081"));
// isAtAll类型如果不为Boolean，请升级至最新SDK
        at.setIsAtAll(false);
//        request.setAt(at);
        OapiRobotSendResponse response = client.execute(request);
    }

    public static void msg() throws Exception {
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey("ding7eo2y6txgxggrkby");
        request.setAppsecret("kMkB3-BB2aoj46esuMBTeKMU7Pmqo1fownC0qTpfmbQRSmMAx-i6mOlUok8EmQIt");
        request.setHttpMethod("GET");
        OapiGettokenResponse response = client.execute(request);

        DingTalkClient client1 = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/get_by_mobile");
        OapiUserGetByMobileRequest request1 = new OapiUserGetByMobileRequest();
        request1.setMobile("18368109081");
        OapiUserGetByMobileResponse execute = client1.execute(request1, response.getAccessToken());

        DingTalkClient client2 = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request request2 = new OapiMessageCorpconversationAsyncsendV2Request();
        request2.setUseridList(execute.getUserid());
        request2.setAgentId(950506572L);
        request2.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
        msg.getText().setContent("test123");
        request2.setMsg(msg);
        OapiMessageCorpconversationAsyncsendV2Response response2 = client2.execute(request2, response.getAccessToken());
    }

}
