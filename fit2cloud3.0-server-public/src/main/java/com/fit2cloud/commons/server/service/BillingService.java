package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.constants.RedisConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.model.billing.BillingItem;
import com.fit2cloud.commons.server.module.ServerInfo;
import com.fit2cloud.commons.server.redis.queue.PushAbstractQueue;
import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by liqiang on 2018/12/11.
 */
@Service
public class BillingService extends PushAbstractQueue {
    @Resource
    private ServerInfo serverInfo;

    @Resource
    private DiscoveryClient discoveryClient;

    public BillingService() {
        super(RedisConstants.Queue.BILLING_ITEM);
    }

    public void charge(BillingItem billingItem) {
        if (!CollectionUtils.exists(discoveryClient.getServices(), service -> StringUtils.equalsIgnoreCase("billing-center", service.toString()))) {
            LogUtil.debug("billing-center is not running.");
            return;
        }
        if (billingItem == null) {
            F2CException.throwException("billingItem cannot be null");
        }
        if (billingItem.getBillingResource() == null) {
            F2CException.throwException("billingResource cannot be null");
        }
        if (StringUtils.isBlank(billingItem.getBillingResource().getResourceId())) {
            F2CException.throwException("resourceId cannot be null");
        }
        if (CollectionUtils.isEmpty(billingItem.getBillingUsages())) {
            F2CException.throwException("billingUsages cannot be null");
        }
        billingItem.setModuleId(serverInfo.getModule().getId());
        push(billingItem);
    }
}
