package com.fit2cloud.commons.server.redis.queue;

import com.fit2cloud.commons.server.base.domain.CloudAccount;


public class CloudAccountQueue extends PushAbstractQueue {

    public CloudAccountQueue(String queue, int interval) {
        super(queue);
    }

    public void handleMessage(CloudAccount account) {
        System.out.println(account.getName());
    }
}
