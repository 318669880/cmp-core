package com.fit2cloud.commons.server.elastic.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.status.ErrorStatus;
import com.fit2cloud.commons.server.elastic.dao.SystemLogRepository;
import com.fit2cloud.commons.server.elastic.domain.SystemLog;
import com.fit2cloud.commons.server.module.ServerInfo;
import com.fit2cloud.commons.utils.CommonBeanFactory;
import com.fit2cloud.commons.utils.GlobalConfigurations;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

public class ElasticsearchAppender extends AppenderBase<LoggingEvent> implements java.io.Serializable {
    private String machine;
    private SystemLogRepository systemLogRepository;
    private ServerInfo serverInfo;

    @Override
    public void start() {
        try {
            machine = java.net.InetAddress.getLocalHost().toString();
        } catch (Exception e) {
            addStatus(new ErrorStatus("failed to initialize ElasticsearchAppender", this, e));
            return;
        }
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    protected void append(LoggingEvent e) {
        try {
            try {
                // todo
                // this filter should be configured in logback.xml, but it doesn't work for now, don't know why.
                if (e.getLevel().levelInt < Level.toLevel(GlobalConfigurations.getProperty("logger.level", String.class, "INFO"), Level.INFO).levelInt) {
                    return;
                }
            } catch (NullPointerException npe) {
                // GlobalConfigurations.environment is null while application start up
            }
            doLogging(e);
        } catch (Exception exception) {
            addError("Fail to log event into elasticsearch", exception);
        } finally {
            MDC.clear();
        }
    }

    private void doLogging(LoggingEvent e) {
        if (systemLogRepository == null) {
            systemLogRepository = CommonBeanFactory.getBean(SystemLogRepository.class);
            serverInfo = CommonBeanFactory.getBean(ServerInfo.class);
            if (systemLogRepository == null) {
                addWarn("systemLogRepository is null.");
                return;
            }
        }
        String errorMessage = buildMessage(e);
        SystemLog systemLog = new SystemLog();
        systemLog.setLogTime(e.getTimeStamp());
        systemLog.setHost(machine);
        systemLog.setMessage(errorMessage);
        systemLog.setLevel(e.getLevel().toString());
        systemLog.setLogger(e.getLoggerName());
        systemLog.setThread(e.getThreadName());
        if (StringUtils.isBlank(serverInfo.getModule().getId())) {
            return;
        }
        systemLog.setModule(serverInfo.getModule().getId());
        try {
            systemLogRepository.save(systemLog);
        } catch (Exception ex) {
            addError(ex.getMessage());
        }
    }

    //获取完整堆栈
    private String buildMessage(LoggingEvent e) {
        if (e.getLevel().toInt() == Level.ERROR_INT && e.getThrowableProxy() != null) {
            return e.getFormattedMessage() + CoreConstants.LINE_SEPARATOR
                    + ThrowableProxyUtil.asString(e.getThrowableProxy());
        }
        return e.getFormattedMessage();
    }
}
