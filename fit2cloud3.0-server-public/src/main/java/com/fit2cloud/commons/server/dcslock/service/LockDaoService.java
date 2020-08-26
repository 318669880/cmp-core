package com.fit2cloud.commons.server.dcslock.service;

import com.fit2cloud.commons.server.base.domain.TLock;
import com.fit2cloud.commons.server.base.mapper.TLockMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;



@Service
public class LockDaoService {

    @Resource
    private TLockMapper tLockMapper;

    public void insert(TLock lock) {
        tLockMapper.insert(lock);
    }

    public TLock query(String pk) {
        return tLockMapper.selectByPrimaryKey(pk);
    }
    public int update(TLock lock){
        return tLockMapper.updateByPrimaryKey(lock);
    }



}
