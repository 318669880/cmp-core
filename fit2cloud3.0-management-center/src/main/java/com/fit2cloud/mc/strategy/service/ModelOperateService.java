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
    public void installOrUpdate(ModelInstalledDto modelInstalledDto) throws Exception;
    //卸载
    public void unInstall(String modeule) throws Exception;
    //启动
    public void start(String modeule) throws Exception;
    //停止
    public void stop(String modeule) throws Exception;
}
