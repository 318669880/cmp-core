package com.fit2cloud.mc.controller;

import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.model.ModelVersion;
import com.fit2cloud.mc.service.ModelManagerService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    //  无敏感信息 无需使用dto
    @PostMapping("/indexServer/save")
    public void save (@RequestBody ModelManager modelManager){
        modelManagerService.add(modelManager);
    }

    @GetMapping("/indexServer/query")
    public ModelManager query (){
        return modelManagerService.select();
    }

    @GetMapping("/indexInstaller/modelBasics")
    public List<ModelBasic> modelBasics(){
        return modelManagerService.modelBasicSelect();
    }

    @PostMapping("/indexInstaller/modeVersion")
    public ModelVersion modelVersion(String model_basic_uuid,String model_version){
        return modelManagerService.modelVersionByBasic(model_basic_uuid,model_version);
    }

    @PostMapping("/indexInstaller/install")
    public void modelInstall(@RequestBody List<ModelInstalledDto> modelInstalledDtos) {
        modelInstalledDtos.forEach(modelInstalledDto -> modelManagerService.addInstaller(modelInstalledDto));
    }

    @PostMapping("/indexInstaller/unUninstall")
    public void modelUninstall(@RequestBody ModelInstalledDto modelInstalledDto) {

    }
}
