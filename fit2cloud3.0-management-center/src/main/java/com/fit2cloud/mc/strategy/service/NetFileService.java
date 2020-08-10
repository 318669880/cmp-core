package com.fit2cloud.mc.strategy.service;

import com.fit2cloud.mc.strategy.entity.ResultInfo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/10 1:45 下午
 * @Description: Please Write notes scientifically
 */

@Service
public class NetFileService extends ResultService{

    private final static String filePath = "/Users/chenyawen/Downloads/menu-mix.zip";

    public  ResultInfo<String> down(String url) throws Exception{
//        ResultService<String> resultService = new ResultService<>();
        CloseableHttpClient client = HttpClients.createDefault();
        RequestConfig config = null;
        //使用代理
        config = RequestConfig.custom().build();
        //目标文件url
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);
        try {
            HttpResponse respone = client.execute(httpGet);
            if(respone.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                return setResultError("down installer file faild");
            }
            HttpEntity entity = respone.getEntity();
            if(entity != null) {
                InputStream is = entity.getContent();
                File file = new File(filePath);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[4096];
                int len = -1;
                while((len = is.read(buffer) )!= -1){
                    fos.write(buffer, 0, len);
                }
                fos.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return setResultSuccess(filePath,"execute success");
    }
}
