package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.dto.request.ModelInstalledRequest;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelInstall;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.model.ModelNode;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.service.ModuleNodeService;
import com.fit2cloud.mc.strategy.service.NodeOperateService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
    public void save (@RequestBody ModelManager modelManager){
        modelManagerService.add(modelManager);
    }

    @GetMapping("/indexServer/query")
    public ModelManager query (){
        return modelManagerService.select();
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

    @PostMapping("/operate/readyInstall")
    public void modelInstall(@RequestBody List<ModelInstalledDto> modelInstalledDtos) {
        ModelManager modelManager = modelManagerService.select();
        String addr = modelManager.getModelAddress();
        modelInstalledDtos.forEach(modelInstalledDto -> {
            try{
                String url = modelInstalledDto.getModelVersion().getDownloadUrl();
                modelInstalledDto.getModelVersion().setDownloadUrl(modelManagerService.prefix(addr,url));
                String icon = modelInstalledDto.getModelBasic().getIcon();
                modelInstalledDto.getModelBasic().setIcon(modelManagerService.prefix(addr,icon));
                modelManagerService.readyInstallModule(modelInstalledDto);
            }catch (Exception e){
                F2CException.throwException(e);
            }
        });
    }

    @PostMapping("/operate/unUninstall")
    public void modelUninstall(@RequestBody List<String> module_arr) {
        ModelManager managerInfo = modelManagerService.select();
        module_arr.forEach(module-> {
            try{
                //moduleNodeService.unInstall(managerInfo, module);
            }catch (Exception e){
                F2CException.throwException(e);
            }
        });
    }

    @PostMapping("operate/node/install/{nodeId}")
    public void nodeInstall(@PathVariable String nodeId) throws Exception {
        ModelNode nodeInfo = moduleNodeService.nodeInfo(nodeId);
        ModelBasic modelBasic = modelManagerService.basicByUuid(nodeInfo.getModelBasicUuid());
        moduleNodeService.installNode(modelBasic.getModule());
    }

    @PostMapping("/operate/node/start/{nodeId}")
    public void start(@PathVariable String nodeId) throws Exception {
        ModelNode nodeInfo = moduleNodeService.nodeInfo(nodeId);
        ModelBasic modelBasic = modelManagerService.basicByUuid(nodeInfo.getModelBasicUuid());
        moduleNodeService.startNode( modelBasic.getModule());
    }

    @PostMapping("/operate/node/stop/{nodeId}")
    public void stop(@PathVariable String nodeId) throws Exception {
        ModelNode nodeInfo = moduleNodeService.nodeInfo(nodeId);
        ModelBasic modelBasic = modelManagerService.basicByUuid(nodeInfo.getModelBasicUuid());
        moduleNodeService.stopNode( modelBasic.getModule());
    }

    @PostMapping("/model/nodes")
    public List<ModelNode> modelNodes(){
        return moduleNodeService.queryNodes(null);
    }

    @PostMapping(value = "/node/{uuid}/{goPage}/{pageSize}")
    public Pager<List<ModelNode>> paging(@PathVariable String uuid, @PathVariable int goPage, @PathVariable int pageSize) {
        Page page = PageHelper.startPage(goPage, pageSize, true);
        Map<String,Object> param = new HashMap<>();
        param.put("model_basic_uuid",uuid);
        List<ModelNode> paging = moduleNodeService.nodePage(param);
        return PageUtils.setPageInfo(page, paging);
    }



}
