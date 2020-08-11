package com.fit2cloud.commons.server.dcslock.service;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/11 1:06 下午
 * @Description: Please Write notes scientifically
 */
public interface DcsLockService {

    public boolean tryLock(String key,Long overTime,Long waitime);

    public void unLock(String key);


}
