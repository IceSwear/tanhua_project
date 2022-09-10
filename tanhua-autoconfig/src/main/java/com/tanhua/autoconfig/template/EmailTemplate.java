package com.tanhua.autoconfig.template;


import com.tanhua.autoconfig.properties.EmailProperties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

/**
 * @Description:  邮箱
 * @Author: Spike Wong
 * @Date: 2022/8/8
 */

public class EmailTemplate {
    public final static String receiverAlias = "";
    public final static String DEFAULT_SENDER_NAME = "";
    public final static String DEFAULT_SUBJECT = "Greeting";
    public static String smtpPort;


    private EmailProperties properties;

    public EmailTemplate(EmailProperties properties) {
        if (Objects.isNull(properties.getEmailSubject())) {
            properties.setEmailSubject(DEFAULT_SUBJECT);
        }
        if (Objects.isNull(properties.getSenderAlias())) {
            properties.setSenderAlias(DEFAULT_SENDER_NAME);
        }
        this.properties = properties;
    }

    /**
     * 创建会话
     *
     * @return
     * @throws Exception
     */
    public Session getSession() throws Exception {
        smtpPort = properties.getSmtpPort();
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", properties.getSmtpHost());   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证
        props.setProperty("mail.smtp.port", smtpPort);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", smtpPort);
        Session session = Session.getInstance(props);
        //session.setDebug(true);
        return session;
    }


    public MimeMessage createMimeMessage(Session session, String receiver, String content) throws Exception {

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(properties.getUsername(), properties.getSenderAlias(), "UTF-8"));
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiver, receiverAlias, "UTF-8"));
//        message.addRecipient( MimeMessage.RecipientType.BCC,new InternetAddress("huangyk@mail.sustech.edu.cn",receiverAlias));
        message.setSubject(properties.getEmailSubject(), "UTF-8");
        message.setContent(content, "text/html;charset=UTF-8");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }


    public void sendCode(String receiver, String code) throws Exception {
        Session session = getSession();
        String content = "您的验证码为：" + code + " ，5分钟内有效。";
        MimeMessage message = createMimeMessage(session, receiver, content);
        Transport transport = session.getTransport();
        transport.connect(properties.getUsername(), properties.getPassword());
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }


}
