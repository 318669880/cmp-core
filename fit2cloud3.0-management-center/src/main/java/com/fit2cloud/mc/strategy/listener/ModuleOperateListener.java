package com.fit2cloud.mc.strategy.listener;

import com.fit2cloud.commons.server.constants.RedisConstants;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.mc.common.constants.ModuleStatusConstants;
import com.fit2cloud.mc.service.ModelManagerService;
import com.fit2cloud.mc.strategy.entity.ModelStatusParam;
import com.fit2cloud.mc.strategy.entity.ModuleMessageInfo;
import com.fit2cloud.mc.strategy.factory.ModelOperateStrategyFactory;
import com.fit2cloud.mc.strategy.queue.ModuleDelayTaskManager;
import com.fit2cloud.mc.strategy.service.EurekaCheckService;
import com.fit2cloud.mc.strategy.service.ModelOperateStrategy;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
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

    /**
     * 检查注册中心中模块状态 延迟时间 默认30s
     */
    @Value("${check_eureka_delay_time:30000}")
    private Long check_eureka_delay_time;

    @Resource
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Resource
    private ModelManagerService modelManagerService;


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
        ModuleMessageInfo moduleMessageInfo = null;
        try  {
            ByteArrayInputStream bis = new ByteArrayInputStream(message.getBody());
            ObjectInput in = new ObjectInputStream(bis);
            Object o = in.readObject();
            if (!(o instanceof ModuleMessageInfo)) {
                return;
            }
            moduleMessageInfo = (ModuleMessageInfo)o;
            ModelOperateStrategy modelOperateStrategy = ModelOperateStrategyFactory.build(moduleMessageInfo.getModelManager().getEnv());
            executeMethod(modelOperateStrategy,moduleMessageInfo.getMethodName(),moduleMessageInfo.getArgs());
            afterExecute(moduleMessageInfo,true);
        } catch (Exception e) {
            afterExecute(moduleMessageInfo,false);
            LogUtil.error(e);
            F2CException.throwException(e);
            //这里还需要考虑 脚本执行失败的情况 把数据库中的记录抹掉
        }
    }

    private void executeMethod(Object targetObject,String methodName,Object[] args) throws Exception{
        Method method = getMethod(targetObject,methodName);
        /*if(ObjectUtils.isNotEmpty(method)){

        }*/
        Optional.ofNullable(method).ifPresent(m -> {
            try{
                method.invoke(targetObject,args);
            }catch (InvocationTargetException invocationTargetException){
                Throwable cause = invocationTargetException.getCause();
                F2CException.throwException(cause);
            }catch (Exception e){
                InvocationTargetException targetEx = (InvocationTargetException)e;
                Throwable t = targetEx .getTargetException();
                F2CException.throwException(t);
            }
        });
    }
    private Method getMethod(Object targetObject,String methodName){
        Method method = Arrays.stream(targetObject.getClass().getMethods()).filter(cmethod -> {
            cmethod.setAccessible(true);
            return cmethod.getName().equals(methodName);
        }).findFirst().get();
        return method;
    }

    private void afterExecute(ModuleMessageInfo moduleMessageInfo,boolean success){
        ModuleStatusConstants status = null;
        switch (moduleMessageInfo.getMethodName()){
            case "executeInstall" :
                status = success ? ModuleStatusConstants.installing : ModuleStatusConstants.installFaild;
                break;
            case "executeStart" :
                status = success ? ModuleStatusConstants.startting : ModuleStatusConstants.startFaild;
                break;
            case "executeStop" :
                status = success ? ModuleStatusConstants.stopping : ModuleStatusConstants.stopFaild;
                break;
            default: break;
        }
        try{
            String module = moduleMessageInfo.getModule();
            modelManagerService.addOrUpdateModelNode(module, status.value());
            modelManagerService.modelStatu(module);//这里只是过度状态 真正的状态 在注册中心发现模块的服务后 才确定
            //这里设置2分钟后 检查状态 如果我们的注册中心 该模块仍然没有发生相应变化 那么判定为失败
            if(success)
            moduleDelayTaskManager.addTask(check_eureka_delay_time, statusParam -> {
                try {
                    eurekaCheckService.checkModuleStatus((ModelStatusParam)statusParam);
                } catch (Exception e) {
                    F2CException.throwException(e);
                }
            },new ModelStatusParam(module, status));

        }catch (Exception e){
            LogUtil.error(e);
            F2CException.throwException(e);
        }
    }


    @Resource
    private EurekaCheckService eurekaCheckService;

    @Resource
    private ModuleDelayTaskManager moduleDelayTaskManager;
}
