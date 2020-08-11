package com.fit2cloud.commons.server.dcslock.service.strategy;

import com.fit2cloud.commons.server.dcslock.service.DcsLockService;
import com.fit2cloud.commons.server.dcslock.util.DbLockUtilService;
import com.fit2cloud.commons.server.exception.F2CException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/11 1:09 下午
 * @Description: Please Write notes scientifically
 */
@Service("dbLock")
public class DbDcsLockService implements DcsLockService {

    @Resource
    private DbLockUtilService dbLockUtilService;


    @Override
    public boolean tryLock(String key,Long overTime,Long waitime) {
        try{
            return dbLockUtilService.lock(key,overTime,waitime);
        }catch (Exception e){
            F2CException.throwException(e);
        }
        return false;
    }

    @Override
    public void unLock(String key) {
        try{
            dbLockUtilService.unlock(key);
        }catch (Exception e){
            F2CException.throwException(e);
        }
    }
}
