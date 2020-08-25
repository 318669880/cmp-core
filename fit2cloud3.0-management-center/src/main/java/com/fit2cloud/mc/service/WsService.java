package com.fit2cloud.mc.service;


import com.fit2cloud.mc.model.WsMessage;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


@Component
public class WsService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    private List<WsMessage> queueMessages(String uid){
        return redisTemplate.opsForList().range(uid,0,-1);
    }


    public void releaseMessage (List<WsMessage> wsMessages) {
        Optional.ofNullable(wsMessages).ifPresent(messages -> {
            messages.forEach(this::releaseMessage);
        });
    }

    public void releaseMessage(WsMessage wsMessage){
        if(ObjectUtils.isEmpty(wsMessage)) return;
        AtomicBoolean is_topic = new AtomicBoolean(false);
        Optional.ofNullable(wsMessage.getTopic()).ifPresent(topic -> {
            messagingTemplate.convertAndSend(topic,wsMessage.getData());
            is_topic.set(true);
        });
        if(is_topic.get()) return;
        messagingTemplate.convertAndSendToUser(wsMessage.getUserId(),wsMessage.getTopic(),wsMessage.getData());
    }


}
