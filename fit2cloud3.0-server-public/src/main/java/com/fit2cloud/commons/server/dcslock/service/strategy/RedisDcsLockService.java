package com.fit2cloud.commons.server.dcslock.service.strategy;

import com.fit2cloud.commons.server.dcslock.service.DcsLockService;
import org.springframework.stereotype.Service;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/11 1:10 下午
 * @Description: Please Write notes scientifically
 */

@Service("redisLock")
public class RedisDcsLockService implements DcsLockService {

    @Override
    public boolean tryLock(String key,Long overTime,Long waitime) {
        return false;
    }

    @Override
    public void unLock(String key) {
        return;
    }
}
