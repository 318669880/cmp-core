package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.mc.common.constants.RuntimeEnvironment;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.dto.request.ModelInstalledRequest;
import com.fit2cloud.mc.job.SyncEurekaServer;
import com.fit2cloud.mc.model.*;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.service.ModuleNodeService;
import com.fit2cloud.mc.strategy.service.NodeOperateService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ModuleNodeService moduleNodeService;



    //  无敏感信息 无需使用dto
    @PostMapping("/indexServer/save")
    public ModelManager save (@RequestBody ModelManager modelManager){
        return modelManagerService.add(modelManager);
    }

    @GetMapping("/indexServer/query")
    public ModelManager query (){
        return modelManagerService.queryModelManager();
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

    @PostMapping("/operate/readyInstall/{mcNodeId}")
    public void modelInstall(@RequestBody List<ModelInstalledDto> modelInstalledDtos, @PathVariable("mcNodeId") String mcNodeId) {
        ModelManager modelManager = modelManagerService.select();


        modelInstalledDtos.forEach(modelInstalledDto -> {
            try{
                String addr = modelManager.getModelAddress();
                ModelBasic basic = modelInstalledDto.getModelBasic();
                if(!modelManager.getOnLine()){
                    addr += "/"+basic.getModule()+"/"+basic.getLastRevision();
                }
                String url = modelInstalledDto.getModelVersion().getDownloadUrl();
                modelInstalledDto.getModelVersion().setDownloadUrl(modelManagerService.prefix(addr,url));
                String icon = modelInstalledDto.getModelBasic().getIcon();
                modelInstalledDto.getModelBasic().setIcon(modelManagerService.prefix(addr,icon));
                modelManagerService.readyInstallModule(modelInstalledDto, StringUtils.equals("-1",mcNodeId) ? null : moduleNodeService.nodeInfo(mcNodeId));
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
        return moduleNodeService.queryNodes(null);
    }

    @PostMapping(value = "/node/{module}/{goPage}/{pageSize}")
    public Pager<List<ModelNode>> paging(@PathVariable String module, @PathVariable int goPage, @PathVariable int pageSize) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        Map<String,Object> param = new HashMap<>();
        param.put("model_basic_uuid",module);
        List<ModelNode> paging = moduleNodeService.nodePage(param);
        return PageUtils.setPageInfo(page, paging);
    }



}
