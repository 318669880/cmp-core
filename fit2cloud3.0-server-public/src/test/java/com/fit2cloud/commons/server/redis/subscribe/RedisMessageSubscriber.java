package com.fit2cloud.commons.server.redis.subscribe;

import com.fit2cloud.commons.server.constants.RedisConstants;
import com.fit2cloud.commons.utils.LogUtil;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

/**
 * 订阅 Topic 之后的操作
 */
@Service
public class RedisMessageSubscriber implements MessageListener {
    @Resource
    private RedisMessageListenerContainer redisMessageListenerContainer;

    /**
     * 启动之后订阅 topic
     */
    @EventListener
    public void init(ApplicationReadyEvent event) {
        String topic = RedisConstants.Topic.CLOUD_ACCOUNT;
        LogUtil.info("Subscribe Topic: " + topic);
        redisMessageListenerContainer.addMessageListener(new MessageListenerAdapter(this), new ChannelTopic(topic));
    }

    /**
     * @param message 消息内容
     * @param pattern 暂时用不到
     */
    public void onMessage(final Message message, final byte[] pattern) {
        try (
                ByteArrayInputStream bis = new ByteArrayInputStream(message.getBody());
                ObjectInput in = new ObjectInputStream(bis)
        ) {
            Object o = in.readObject();
            System.out.println("Message received 1: " + o);
        } catch (Throwable e) {
            LogUtil.error(e);
        }
    }
}