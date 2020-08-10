package com.fit2cloud.mc.controller;

import com.fit2cloud.commons.server.base.domain.ExtraUser;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.BeanUtils;
import com.fit2cloud.commons.utils.PageUtils;
import com.fit2cloud.commons.utils.Pager;
import com.fit2cloud.mc.common.constants.PermissionConstants;
import com.fit2cloud.mc.dto.ModelInstalledDto;
import com.fit2cloud.mc.dto.request.ExtraUserRequest;
import com.fit2cloud.mc.dto.request.ModelInstalledRequest;
import com.fit2cloud.mc.model.ModelBasic;
import com.fit2cloud.mc.model.ModelInstall;
import com.fit2cloud.mc.model.ModelManager;
import com.fit2cloud.mc.model.ModelVersion;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.strategy.factory.ModelOperateServiceFactory;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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



    /*@PostMapping("/indexInstaller/modeVersion")
    public ModelVersion modelVersion(String model_basic_uuid,String model_version){
        return modelManagerService.modelVersionByBasic(model_basic_uuid,model_version);
    }*/




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




    @PostMapping("/operate/install")
    public void modelInstall(@RequestBody List<ModelInstalledDto> modelInstalledDtos) {
        /*modelInstalledDtos.forEach(modelInstalledDto -> modelManagerService.addInstaller(modelInstalledDto));*/
        ModelManager managerInfo = modelManagerService.select();
        String addr = managerInfo.getModelAddress();
        modelInstalledDtos.forEach(modelInstalledDto -> {
            try{
                String url = modelInstalledDto.getModelVersion().getDownloadUrl();
                if(url.indexOf(addr) == -1){
                    url = (addr.endsWith("/")? addr : (addr+"/")) + url;
                    modelInstalledDto.getModelVersion().setDownloadUrl(url);
                }
                ModelOperateServiceFactory.build(managerInfo.getEnv()).installOrUpdate(modelInstalledDto);
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
                ModelOperateServiceFactory.build(managerInfo.getEnv()).unInstall(module);
            }catch (Exception e){
                F2CException.throwException(e);
            }
        });
    }

    @GetMapping("/operate/start")
    public void start(@RequestBody List<String> module_arr){
        ModelManager managerInfo = modelManagerService.select();
        module_arr.forEach(module-> {
            try{
                ModelOperateServiceFactory.build(managerInfo.getEnv()).start(module);
            }catch (Exception e){
                F2CException.throwException(e);
            }
        });
    }

    @GetMapping("/operate/stop")
    public void stop(@RequestBody List<String> module_arr){
        ModelManager managerInfo = modelManagerService.select();
        module_arr.forEach(module-> {
            try{
                ModelOperateServiceFactory.build(managerInfo.getEnv()).stop(module);
            }catch (Exception e){
                F2CException.throwException(e);
            }
        });
    }



    @GetMapping("/{action}/{module}")
    public void actionModules(@PathVariable String action, @PathVariable String module) throws Exception{
        modelManagerService.actionModule(action, module);
    }

}
