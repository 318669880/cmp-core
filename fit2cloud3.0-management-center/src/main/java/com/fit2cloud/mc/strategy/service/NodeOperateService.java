package com.fit2cloud.mc.strategy.service;

import com.fit2cloud.mc.model.ModelManager;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/10 9:33 上午
 * @Description: Please Write notes scientifically
 *
 * 执行模版管理 安装 卸载 升级 降级 启动 停止 脚本
 */
public interface NodeOperateService {
    //安装
    public void installOrUpdate(ModelManager managerInfo, String module, String nodeId) throws Exception;
    //卸载
    public void unInstall(ModelManager managerInfo,String modeule) throws Exception;
    //启动
    public void start(ModelManager managerInfo,String modeule ,String nodeId) throws Exception;
    //停止
    public void stop(ModelManager managerInfo,String modeule,String nodeId) throws Exception;
}
