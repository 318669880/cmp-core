package com.fit2cloud.mc.strategy.service;

import com.fit2cloud.mc.dto.ModelInstalledDto;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/10 9:33 上午
 * @Description: Please Write notes scientifically
 *
 * 执行模版管理 安装 卸载 升级 降级 启动 停止 脚本
 */
public interface ModelOperateService {
    //安装
    public Object install(ModelInstalledDto modelInstalledDto);
    //卸载
    public Object unInstall(String model_basic_uuid);
    //更新
    public Object update(ModelInstalledDto modelInstalledDto);
    //启动
    public Object start(String model_basic_uuid);
    //停止
    public Object stop(String model_basic_uuid);
}
