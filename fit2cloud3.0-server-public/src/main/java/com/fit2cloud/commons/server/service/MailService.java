package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.base.domain.SystemParameter;
import com.fit2cloud.commons.server.base.mapper.SystemParameterMapper;
import com.fit2cloud.commons.server.constants.ParamConstants;
import com.fit2cloud.commons.server.model.Authentication;
import com.fit2cloud.commons.server.model.MailAttachmentInfo;
import com.fit2cloud.commons.utils.EncryptUtils;
import com.fit2cloud.commons.utils.LogUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Author: chunxing
 * Date: 2018/7/27  下午2:54
 * Description:
 */
@Service
public class MailService {

    @Resource
    private SystemParameterMapper systemParameterMapper;

    private JavaMailSenderImpl getMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setDefaultEncoding("UTF-8");
        javaMailSender.setHost(getNotNullValue(ParamConstants.MAIL.SERVER.getKey()));
        javaMailSender.setPort(Integer.valueOf(getNotNullValue(ParamConstants.MAIL.PORT.getKey())));

        // 是否匿名登录
        String anon = getNotNullValue(ParamConstants.MAIL.ANON.getKey());
        boolean isAnon = StringUtils.isBlank(anon) || Boolean.parseBoolean(anon);
        if (!isAnon) {
            javaMailSender.setUsername(getNotNullValue(ParamConstants.MAIL.ACCOUNT.getKey()));
            String password = getValue(ParamConstants.MAIL.PASSWORD.getKey());
            if (StringUtils.isNotBlank(password)) {
                javaMailSender.setPassword(EncryptUtils.aesDecrypt(password).toString());
            }
        }
        Properties props = new Properties();
        String auth = "mail.smtp.auth";
        props.put(auth, String.valueOf(!isAnon));
        Authentication authentication = new Authentication(getNotNullValue(ParamConstants.MAIL.ACCOUNT.getKey()), getNotNullValue(ParamConstants.MAIL.PASSWORD.getKey()));
        Session session = Session.getDefaultInstance(javaMailSender.getJavaMailProperties(), authentication);
        if (BooleanUtils.toBoolean(getValue(ParamConstants.MAIL.SSL.getKey()))) {
            String stlKey = "mail.smtp.socketFactory.class";
            String stlValue = "javax.net.ssl.SSLSocketFactory";
            props.put(stlKey, stlValue);//tsl
            javaMailSender.setSession(session);
        }
        if (BooleanUtils.toBoolean(getValue(ParamConstants.MAIL.TLS.getKey()))) {
            String ssl = "mail.smtp.starttls.enable";
            props.put(ssl, String.valueOf(true));//ssl
            javaMailSender.setSession(session);
        }
        javaMailSender.setJavaMailProperties(props);
        return javaMailSender;
    }

    @Async
    public void sendSimpleEmail(String subject, String content, String... to) {
        JavaMailSenderImpl javaMailSender = getMailSender();
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setFrom(javaMailSender.getUsername());
        simpleMailMessage.setSentDate(new Date());
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);
        javaMailSender.send(simpleMailMessage);
    }

    @Async
    public void sendHtmlEmail(String subject, String content, String... to) {
        JavaMailSenderImpl javaMailSender = getMailSender();
        MimeMessage message = javaMailSender.createMimeMessage();//创建一个MINE消息
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(getNotNullValue(ParamConstants.MAIL.ACCOUNT.getKey()));
            helper.setTo(to);
            helper.setSentDate(new Date());
            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            LogUtil.error(e.getMessage());
        }
    }

    //发送邮件带附件
    public void sendAttachmentEmail(String subject, String content, MailAttachmentInfo mailAttachmentInfo, String... to) {
        List<MailAttachmentInfo> mailAttachmentInfoList = new ArrayList<>();
        mailAttachmentInfoList.add(mailAttachmentInfo);
        sendAttachmentEmail(subject, content, mailAttachmentInfoList, to);
    }

    //发送邮件带附件
    public void sendAttachmentEmail(String subject, String content, List<MailAttachmentInfo> mailAttachmentInfoList, String... to) {
        JavaMailSenderImpl javaMailSender = getMailSender();
        MimeMessage message = javaMailSender.createMimeMessage();//创建一个MINE消息
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(getNotNullValue(ParamConstants.MAIL.ACCOUNT.getKey()));
            helper.setTo(to);
            helper.setSentDate(new Date());
            helper.setSubject(subject);
            helper.setText(content, true);
            if (CollectionUtils.isNotEmpty(mailAttachmentInfoList)) {
                for (MailAttachmentInfo mailAttachmentInfo : mailAttachmentInfoList) {
                    helper.addAttachment(mailAttachmentInfo.getAttachmentName(), mailAttachmentInfo.getInputStreamSource());
                }
            }
            javaMailSender.send(message);
        } catch (MessagingException e) {
            LogUtil.error(e.getMessage());
        }
    }

    private String getValue(String key) {
        SystemParameter systemParameter = systemParameterMapper.selectByPrimaryKey(key);
        if (systemParameter != null) {
            return systemParameter.getParamValue();
        }
        return null;
    }

    private String getNotNullValue(String key) {
        SystemParameter systemParameter = systemParameterMapper.selectByPrimaryKey(key);
        if (systemParameter == null) {
            throw new RuntimeException(key + " not set");
        }
        String value = systemParameter.getParamValue();
        if (StringUtils.isBlank(value)) {
            throw new RuntimeException(key + " not set");
        }
        return value;
    }
}
