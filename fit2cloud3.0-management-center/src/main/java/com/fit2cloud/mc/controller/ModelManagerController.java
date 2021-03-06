package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.*;
import com.fit2cloud.mc.common.constants.WsTopicConstants;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.dto.request.ModelInstalledRequest;
import com.fit2cloud.mc.model.*;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.service.ModuleNodeService;
import com.fit2cloud.mc.strategy.task.ModelNodeTask;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/5 10:50 下午
 * @Description: Please Write notes scientifically
 */

@RequestMapping("/modelManager")
@RestController
public class ModelManagerController {

    @Resource
    private ModelManagerService modelManagerService;

    @Resource
    @Lazy
    private ModuleNodeService moduleNodeService;


    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private RestTemplate restTemplate;


    @PostMapping("/indexServer/indexData")
    public String indexData(@RequestBody Map<String, String> param){
        String resultStr = HttpClientUtil.get(param.get("dataUrl"), null);
        return resultStr;
    }

    //  无敏感信息 无需使用dto
    @PostMapping("/indexServer/save")
    public ModelManager save (@RequestBody ModelManager modelManager){
        return modelManagerService.add(modelManager);
    }

    @GetMapping("/indexServer/query")
    public ModelManager query (){
        return modelManagerService.queryModelManager();
    }

    @PostMapping("/indexServer/address")
    public String address(){
        return modelManagerService.address();
    }

    @PostMapping(value = "/runner/{goPage}/{pageSize}")
    public Pager<List<ModelInstall>> paging(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody ModelInstalledRequest request) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        List<ModelInstall> paging = modelManagerService.paging(BeanUtils.objectToMap(request));
        return PageUtils.setPageInfo(page, paging);
    }

    @GetMapping("/indexInstaller/modelInstallInfos")
    public List<ModelInstall> modelInstallInfos() {
        return modelManagerService.installInfoquery();
    }

    @PostMapping("/operate/readyInstall/{mcNodeId}/{type}")
    public void modelInstall(@RequestBody List<ModelInstalledDto> modelInstalledDtos, @PathVariable("mcNodeId") String mcNodeId ,@PathVariable("type") String type) {
        ModelManager modelManager = modelManagerService.select();
        String addr = modelManager.getModelAddress();
        modelInstalledDtos.forEach(modelInstalledDto -> {
            try{
                String url = modelInstalledDto.getModelVersion().getDownloadUrl();
                modelInstalledDto.getModelVersion().setDownloadUrl(modelManagerService.prefix(addr,url));
                String icon = modelInstalledDto.getModelBasic().getIcon();
                modelInstalledDto.getModelBasic().setIcon(modelManagerService.prefix(addr,icon));
                modelManagerService.readyInstallModule(modelInstalledDto, StringUtils.equals("-1",mcNodeId) ? null : moduleNodeService.nodeInfo(mcNodeId), StringUtils.equals("update",type));
            }catch (Exception e){
                F2CException.throwException(e);
            }
        });
    }


    @PostMapping("operate/node/install/{module}/{nodeId}")
    public void nodeInstall(@PathVariable String module, @PathVariable String nodeId) throws Exception {
        moduleNodeService.installNode(module, nodeId);
    }

    @PostMapping("/operate/node/start/{module}/{nodeId}")
    public void start(@PathVariable String module, @PathVariable String nodeId) throws Exception {
        moduleNodeService.startNode( module, nodeId);
    }

    @PostMapping("/operate/node/stop/{module}/{nodeId}")
    public void stop(@PathVariable String module, @PathVariable String nodeId) throws Exception {

        moduleNodeService.stopNode( module, nodeId);
    }

    @PostMapping("operate/module/start")
    public void batchStart(@RequestBody List<String> modules){
        modules.forEach(module -> {
            moduleNodeService.queryNodes(module).forEach(node -> {
                try {
                    moduleNodeService.startNode( module, node.getMcNodeUuid());
                } catch (Exception e) {
                    LogUtil.error(e.getMessage(),e);
                    F2CException.throwException(e);
                }
            });
        });
    }

    @PostMapping("operate/module/stop")
    public void batchStop(@RequestBody List<String> modules){
        modules.forEach(module -> {
            moduleNodeService.queryNodes(module).forEach(node -> {
                try {
                    moduleNodeService.stopNode( module, node.getMcNodeUuid());
                } catch (Exception e) {
                    LogUtil.error(e.getMessage(),e);
                    F2CException.throwException(e);
                }
            });
        });
    }

    @PostMapping("/model/nodes")
    public List<ModelNode> modelNodes(){
        return moduleNodeService.allBusiNodes();
    }

    @PostMapping(value = "/node/{module}/{goPage}/{pageSize}")
    public Pager<List<ModelNode>> paging(@PathVariable String module, @PathVariable int goPage, @PathVariable int pageSize) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        Map<String,Object> param = new HashMap<>();
        param.put("model_basic_uuid",module);
        List<ModelNode> paging = moduleNodeService.nodePage(param);
        return PageUtils.setPageInfo(page, paging);
    }

    @PostMapping("/model/reload")
    public boolean reloadMcInfo() {
        List<ServiceInstance> instances = discoveryClient.getInstances("management-center");
        if (CollectionUtils.isNotEmpty(instances)){
            return instances.stream().allMatch(instance -> {
                String url = instance.getUri().toString() + "/modelNode/mcRefresh";
                try{
                    HttpHeaders httpHeaders = new HttpHeaders();
                    MultiValueMap<String,Object> param = new LinkedMultiValueMap<>();
                    HttpEntity<MultiValueMap<String,Object>> request = new HttpEntity<>(param,httpHeaders);
                    ResponseEntity<String> entity = restTemplate.postForEntity(url, request, String.class);
                    LogUtil.info("refresh management-center："+new Gson().toJson(entity));
                    return true;
                }catch (Exception e){
                    LogUtil.error(e.getMessage(),e);
                    // 如果不是批量操作 那么直接抛出异常 否则继续运行
                    return false;
                }
            });
        }
        return true;
    }

    @PostMapping("/model/topics")
    public List<Map<String,String>> topics(){
        WsTopicConstants[] values = WsTopicConstants.values();
        List<Map<String, String>> resultLists = Arrays.stream(values).map(WsTopicConstants::toMap).collect(Collectors.toList());
        return resultLists;
    }






}
