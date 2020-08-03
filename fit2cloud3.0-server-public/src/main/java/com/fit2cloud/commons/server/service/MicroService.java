package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.commons.server.i18n.Translator;
import com.fit2cloud.commons.server.utils.HttpHeaderUtils;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.ResultHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@Service
public class MicroService {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private DiscoveryClient discoveryClient;


    /**
     * GET 方法
     *
     * @param serviceId 服务ID
     * @param url       请求url
     * @return ResultHolder
     */
    public ResultHolder getForResultHolder(String serviceId, String url) {
        HttpHeaders httpHeaders = HttpHeaderUtils.getHttpHeaders();
        return getForResultHolder(serviceId, url, httpHeaders);
    }

    private ResultHolder getForResultHolder(String serviceId, String url, HttpHeaders httpHeaders) {
        if (!validateServiceAndUrl(serviceId, url)) {
            return ResultHolder.error(Translator.get("i18n_micro_service_serviceId_url"));
        }
        try {
            HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
            ResponseEntity<ResultHolder> entity = restTemplate.exchange(chooseService(serviceId, url), HttpMethod.GET, httpEntity, ResultHolder.class);
            return entity.getBody();
        } catch (Exception e) {
            LogUtil.error("Service call error[serviceId:" + serviceId + ",url:" + url + "],Error:" + e.getMessage(), e);
            return ResultHolder.error(e.getMessage());
        }
    }

    public MicroService runAsUser(User user) {
        HttpHeaderUtils.runAsUser(user);
        return this;
    }

    /**
     * 批量GET
     *
     * @param serviceIds 服务list
     * @param url        请求url
     * @return List<ResultHolder>
     */
    public List<ResultHolder> getForResultHolder(List<String> serviceIds, String url, long... timeoutSeconds) {
        if (CollectionUtils.isEmpty(serviceIds)) {
            return new ArrayList<>();
        }
        HttpHeaders httpHeaders = HttpHeaderUtils.getHttpHeaders();
        List<Callable<ResultHolder>> callableList = new ArrayList<>();
        for (String serviceId : serviceIds) {
            callableList.add(() -> getForResultHolder(serviceId, url, httpHeaders));
        }

        return executeResultHolder(callableList, timeoutSeconds);
    }

    private List<ResultHolder> executeResultHolder(List<Callable<ResultHolder>> callableList, long... timeoutSeconds) {
        List<ResultHolder> resultList = new ArrayList<>();
        long timeout = 60L;
        if (ArrayUtils.isNotEmpty(timeoutSeconds)) {
            timeout = timeoutSeconds[0];
            if (timeout < 5) {
                timeout = 5;
            }
        }
        ExecutorService executorService = Executors.newFixedThreadPool(callableList.size());
        try {
            List<Future<ResultHolder>> futureList = executorService.invokeAll(callableList, timeout, TimeUnit.SECONDS);
            for (Future<ResultHolder> future : futureList) {
                try {
                    resultList.add(future.get());
                } catch (Exception e) {
                    LogUtil.error("Service call, future.get error, message:" + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
        } finally {
            executorService.shutdown();
        }
        return resultList;
    }

    /**
     * POST 方法
     *
     * @param serviceId 服务ID
     * @param url       请求url
     * @param param     请求参数
     * @return ResultHolder
     */

    public ResultHolder postForResultHolder(String serviceId, String url, Object param) {
        HttpHeaders httpHeaders = HttpHeaderUtils.getHttpHeaders();
        return postForResultHolder(serviceId, url, httpHeaders, param);
    }

    private ResultHolder postForResultHolder(String serviceId, String url, HttpHeaders httpHeaders, Object param) {
        if (!validateServiceAndUrl(serviceId, url)) {
            return ResultHolder.error(Translator.get("i18n_micro_service_serviceId_url"));
        }

        try {
            HttpEntity<Object> httpEntity = new HttpEntity<>(param, httpHeaders);
            ResponseEntity<ResultHolder> entity = restTemplate.exchange(chooseService(serviceId, url), HttpMethod.POST, httpEntity, ResultHolder.class);
            return entity.getBody();
        } catch (Exception e) {
            LogUtil.error("Service call error[serviceId:" + serviceId + ",url:" + url + "],Error:" + e.getMessage(), e);
            return ResultHolder.error(e.getMessage());
        }
    }

    /**
     * POST 批量调用
     *
     * @param serviceIds 服务ID list
     * @param url        请求url
     * @param param      参数
     * @return List<ResultHolder>
     */
    public List<ResultHolder> postForResultHolder(List<String> serviceIds, String url, Object param, long... timeoutSeconds) {
        if (CollectionUtils.isEmpty(serviceIds)) {
            return new ArrayList<>();
        }
        HttpHeaders httpHeaders = HttpHeaderUtils.getHttpHeaders();
        List<Callable<ResultHolder>> callableList = new ArrayList<>();
        for (String serviceId : serviceIds) {
            callableList.add(() -> postForResultHolder(serviceId, url, httpHeaders, param));
        }
        return executeResultHolder(callableList, timeoutSeconds);
    }

    private String chooseService(String serviceId, String url) {
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(serviceId);
        if (CollectionUtils.isEmpty(serviceInstanceList)) {
            throw new RuntimeException("No available instance for service: " + serviceId);
        }
        ServiceInstance serviceInstance = serviceInstanceList.get(RandomUtils.nextInt(0, serviceInstanceList.size()));
        return serviceInstance.getUri().toString() + url;
    }

    private boolean validateServiceAndUrl(String serviceId, String url) {
        return !StringUtils.isBlank(serviceId) && !StringUtils.isBlank(url);
    }


}
