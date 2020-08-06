package com.fit2cloud.gateway;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Created by liqiang on 2018/10/18.
 */
public class ApiDemo {

    static String accessKey = "";
    static String secretKey = "";

    public static void main(String[] args) {
        try {
            URL url = new URL("https://rdtest2.fit2cloud.com/dashboard/user/info");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json;charset=UTF-8");
            urlConnection.setRequestProperty("accessKey", accessKey);
            String signature = aesEncrypt(accessKey + "|" + UUID.randomUUID().toString() + "|" + System.currentTimeMillis(), secretKey, accessKey);
            urlConnection.setRequestProperty("signature", signature);
            InputStream is = urlConnection.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while (-1 != (len = is.read(buffer))) {
                baos.write(buffer, 0, len);
                baos.flush();
            }
            System.out.println(urlConnection.getResponseCode());
            System.out.println(baos.toString("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String aesEncrypt(String src, String secretKey, String iv) throws Exception {
        byte[] raw = secretKey.getBytes("UTF-8");
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv1 = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv1);
        byte[] encrypted = cipher.doFinal(src.getBytes("UTF-8"));
        return Base64.encodeBase64String(encrypted);

    }
}
