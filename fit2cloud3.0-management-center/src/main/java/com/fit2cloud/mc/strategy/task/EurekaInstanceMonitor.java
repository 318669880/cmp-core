package com.fit2cloud.mc.strategy.task;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.model.ModelNode;
import com.google.gson.Gson;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class EurekaInstanceMonitor {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    public DiscoveryClient discoveryClient;

    @Resource
    private  ModelNodeTask  modelNodeTask;

    @Resource
    private Environment environment;



    public List<Object> execute(String module, String nodeId, String urlSuffix, ModelNode modelNode){
        String mc_module = environment.getProperty("spring.application.name");
        List<String> serverInfos = eurekaClusterIps(mc_module, modelNode);
        List<Object> results = serverInfos.stream().filter(info -> modelNodeTask.isHostConnectable(info)).map(serverInfo -> {
            String url = serverInfo + urlSuffix;
            Object result = null;
            try{
                HttpHeaders httpHeaders = new HttpHeaders();
                /*ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
                Cookie[] cookies = httpServletRequest.getCookies();
                List<String> cookieLists = new ArrayList<>();
                for (Cookie cookie : cookies) {
                    cookieLists.add(cookie.getName() + "=" + cookie.getValue());
                }
                httpHeaders.put("Cookie", cookieLists);*/
                MultiValueMap<String,Object> param = new LinkedMultiValueMap<>();
                param.add("module",module);
                param.add("nodeId",StringUtils.isEmpty(nodeId) ? UUIDUtil.newUUID() : nodeId);
                HttpEntity<MultiValueMap<String,Object>> request = new HttpEntity<>(param,httpHeaders);
                ResponseEntity<String> entity = restTemplate.postForEntity(url, request, String.class);
                result = entity.getBody();
                LogUtil.info("restTemplate??????????????????"+new Gson().toJson(entity));
                return result;
            }catch (Exception e){
                LogUtil.error(e.getMessage(),e);
                // ???????????????????????? ???????????????????????? ??????????????????
                if(!ObjectUtils.isEmpty(modelNode)) F2CException.throwException(e);
                return null;
            }

        }).collect(Collectors.toList());
        return results;
    }

    /**
     * ???????????????????????????????????????
     * @return
     */
    private List<String> eurekaClusterIps(String module, ModelNode modelNode){
        Map<String,List<ServiceInstance>> sis = new HashMap<>();
        List<String> services = discoveryClient.getServices();

        if(CollectionUtils.isEmpty(services)) return new ArrayList<>();
        services.forEach(service -> {
            //List<InstanceInfo> instancesByVipAddressAndAppName = eurekaClient.getInstancesByVipAddressAndAppName(null, module.toUpperCase(), false);
            sis.put(service,discoveryClient.getInstances(service));
        });
        List<ServiceInstance> moduleInstances = sis.get(module);
        if(CollectionUtils.isEmpty(moduleInstances)) return new ArrayList<>();
        Stream<ServiceInstance> stream = moduleInstances.stream();
        if(!ObjectUtils.isEmpty(modelNode) && !StringUtils.isEmpty(modelNode.getNodeIp())){
            List<String> results = new ArrayList<>();
            stream.anyMatch(ins -> {
                boolean current = ins.getUri().toString().indexOf(modelNode.getNodeHost()) != -1;
                if(current){
                    results.add(ins.getUri().toString());
                }
                return current;
            });
            return results;
        }
        return stream.map(instance -> instance.getUri().toString()).collect(Collectors.toList());
    }
}
