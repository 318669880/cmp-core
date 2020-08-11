package com.fit2cloud.mc.strategy.listener;

import com.fit2cloud.commons.server.constants.RedisConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.strategy.entity.ModuleMessageInfo;
import com.fit2cloud.mc.strategy.factory.ModelOperateStrategyFactory;
import com.fit2cloud.mc.strategy.service.ModelOperateStrategy;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/11 4:55 下午
 * @Description: Please Write notes scientifically
 */
@Component
public class ModuleOperateListener implements MessageListener {
    @Resource
    private RedisMessageListenerContainer redisMessageListenerContainer;

    /**
     * 启动之后订阅 topic
     */
    @EventListener
    public void init(ApplicationReadyEvent event) {
        String topic = RedisConstants.Topic.MODULE_OPERATE;
        try {
            LogUtil.info("subscribing topic:" + topic);
            redisMessageListenerContainer.addMessageListener(new MessageListenerAdapter(this), new ChannelTopic(topic));
        } catch (Exception e) {
            LogUtil.error("failed to subscribe topic: " + topic, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param message 消息内容
     * @param pattern 暂时用不到
     */
    @Override
    public void onMessage(final Message message, final byte[] pattern) {
        try  {
            ByteArrayInputStream bis = new ByteArrayInputStream(message.getBody());
            ObjectInput in = new ObjectInputStream(bis);
            Object o = in.readObject();
            if (!(o instanceof ModuleMessageInfo)) {
                return;
            }
            ModuleMessageInfo moduleMessageInfo = (ModuleMessageInfo)o;
            ModelOperateStrategy modelOperateStrategy = ModelOperateStrategyFactory.build(moduleMessageInfo.getModelManager().getEnv());
            executeMethod(modelOperateStrategy,moduleMessageInfo.getMethodName(),moduleMessageInfo.getArgs());
        } catch (Throwable e) {
            LogUtil.error(e);
            F2CException.throwException(e);
            //这里还需要考虑 脚本执行失败的情况 把数据库中的记录抹掉
        }
    }

    private Object executeMethod(Object targetObject,String methodName,Object[] args) throws Exception{
        Method method = getMethod(targetObject,methodName);
        Optional.ofNullable(method).ifPresent(m -> {
            try{
                m.invoke(targetObject,args);
            }catch (Exception e){
                F2CException.throwException(e);
            }
        });
        return method.invoke(targetObject,args);

    }

    private Method getMethod(Object targetObject,String methodName){
        Method method = Arrays.stream(targetObject.getClass().getMethods()).filter(cmethod -> {
            cmethod.setAccessible(true);
            return cmethod.getName().equals(methodName);
        }).findFirst().get();
        return method;
    }
}
