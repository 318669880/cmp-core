package com.fit2cloud.commons.server.dcslock.util;

import com.fit2cloud.commons.server.base.domain.TLock;
import com.fit2cloud.commons.server.base.domain.TLockExample;
import com.fit2cloud.commons.server.base.mapper.TLockMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/11 1:15 下午
 * @Description: Please Write notes scientifically
 */
@Component
public class DbLockUtilService {

    private static Logger log = LoggerFactory.getLogger(DbLockUtilService.class);

    @Resource
    private TLockMapper tLockMapper;

    //将requestid保存在该变量中
    private static ThreadLocal<String> requestIdTL = new ThreadLocal<>();



    /**
     * 获取锁
     *
     * @param lock_key        锁key
     * @param overTime(毫秒) 持有锁的有效时间，防止死锁
     * @param waitime(毫秒)  获取锁的超时时间，这个时间内获取不到将重试
     * @return
     */
    public boolean lock(String lock_key, long overTime, long waitime) throws Exception {
        log.info("start");
        boolean lockResult = false;
        String request_id = getRequestId();
        //long starttime = System.currentTimeMillis();
        while (true) {
            TLock tLock = get(lock_key);
            if (Objects.isNull(tLock)) {
                //插入一条记录,重新尝试获取锁
                insert(lock_key);
                continue;
            } else {
                String reqid = tLock.getRequestId();
                //如果reqid为空字符，表示锁未被占用
                if ("".equals(reqid)) {
                    tLock.setRequestId(request_id);
                    tLock.setLockCount(1);
                    tLock.setTimeout(System.currentTimeMillis() + overTime);
                    if (update(tLock) == 1) {
                        lockResult = true;
                        break;
                    }
                } else if (request_id.equals(reqid)) {
                    //如果request_id和表中request_id一样表示锁被当前线程持有者，此时需要加重入锁
                    tLock.setTimeout(System.currentTimeMillis() + overTime);
                    tLock.setLockCount(tLock.getLockCount() + 1);
                    if (update(tLock) == 1) {
                        lockResult = true;
                        break;
                    }
                } else {
                    //锁不是自己的，并且已经超时了，则重置锁，继续重试
                    if (tLock.getTimeout() < System.currentTimeMillis()) {
                        resetLock(tLock);
                    } else {
                        //如果未超时，休眠waitime秒，继续重试
                        TimeUnit.MILLISECONDS.sleep(waitime);
                    }
                }
            }
        }
        log.info("end");
        return lockResult;
    }

    /**
     * 释放锁
     *
     * @param lock_key
     * @throws Exception
     */
    public void unlock(String lock_key) throws Exception {
        //获取当前线程requestId
        String requestId = getRequestId();
        TLock tLock = get(lock_key);
        //当前线程requestId和库中request_id一致 && lock_count>0，表示可以释放锁
        if (Objects.nonNull(tLock) && requestId.equals(tLock.getRequestId()) && tLock.getLockCount() > 0) {
            if (tLock.getLockCount() == 1) {
                //重置锁
                resetLock(tLock);
            } else {
                tLock.setLockCount(tLock.getLockCount() - 1);
                update(tLock);
            }
        }
    }

    /**
     * 重置锁
     *
     * @param tLock
     * @return
     * @throws Exception
     */
    private int resetLock(TLock tLock) throws Exception {
        tLock.setRequestId("");
        tLock.setLockCount(0);
        tLock.setTimeout(0L);
        return update(tLock);
    }

    /**
     * 获取当前线程requestid
     *
     * @return
     */
    private String getRequestId() {
        String requestId = requestIdTL.get();
        if (requestId == null || "".equals(requestId)) {
            requestId = randomUUID().toString();
            requestIdTL.set(requestId);
        }
        log.info("requestId:{}", requestId);
        return requestId;
    }

    private String randomUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }



    /**
     * 更新tLock信息，内部采用乐观锁来更新
     *
     * @param tLock
     * @return
     * @throws Exception
     */
    private int update(TLock tLock) throws Exception {
        TLockExample example = new TLockExample();
        example.createCriteria().andLockKeyEqualTo(tLock.getLockKey()).andVersionEqualTo(tLock.getVersion());
        tLock.setVersion(tLock.getVersion()+1);
        return tLockMapper.updateByExampleSelective(tLock,example);
    }

    private void insert(String lock_key){
        TLock tLock = new TLock();
        tLock.setLockKey(lock_key);
        tLock.setRequestId("");
        tLock.setTimeout(0L);
        tLock.setLockCount(0);
        tLock.setVersion(0);
        tLockMapper.insert(tLock);
    }

    private TLock get(String lock_key) throws Exception {
        return tLockMapper.selectByPrimaryKey(lock_key);
    }

}
