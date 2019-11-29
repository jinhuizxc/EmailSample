package com.example.emailsample.email;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * https://blog.csdn.net/csdnwr/article/details/82252377
 * android 发送邮件（带附件）
 */
public class MailService {

    public static void send_email(String email, String content) throws IOException, MessagingException {
        File file = new File(content);


        //创建配置文件
        Properties props = new Properties();
        // 开启认证
        props.put("mail.smtp.auth", true);
        // 设置协议方式
        props.put("mail.transport.protocol", "smtp");
        // 设置主/机名
        props.put("mail.smtp.host", "smtp.163.com");//host
        // 设置SSL加密(未采用SSL时，端口一般为25，可以不用设置；采用SSL时，端口为465，需要显示设置)
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        // 设置账户和密码
        props.put("mail.smtp.username", "176xxxxxxxx");//username
        props.put("mail.smtp.password", "123456");//password
        // 创建会话，getDefaultInstace得到的始终是该方法初次创建的缺省的对象，getInstace每次获取新对象
//        Session session = Session.getInstance(props, new MyAuthenticator("176xxxxxxxx", "123456"));
        Session session = Session.getInstance(props);
        // 显示错误信息
        session.setDebug(true);
        // 创建发送时的消息对象
        MimeMessage message = new MimeMessage(session);
        // 设置发送发的账户和名称
        message.setFrom(new InternetAddress("176xxxxxxxx@163.com", "123456", "UTF-8"));
        // 获取收件方的账户和名称
        message.setRecipients(MimeMessage.RecipientType.TO, email);
        // 设置主题
        message.setSubject("作业统计");

        // 设置带附件的内容
        Multipart multipart = new MimeMultipart();
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(content, "text/html; charset=utf-8");
        multipart.addBodyPart(messageBodyPart);
        // 添加邮件附件
        MimeBodyPart attachPart = new MimeBodyPart();
        DataSource source = new FileDataSource(content);
        attachPart.setDataHandler(new DataHandler(source));
        attachPart.setFileName("作业统计");
        multipart.addBodyPart(attachPart);
        // 保存邮件内容
        message.setContent(multipart);

        // 设置文本内容内容
        //message.setContent(content, "text/html; charset=utf-8");

        // 发送
        Transport.send(message);

    }


    /**
     *  发送邮件:
     *  new Thread() {
     *     @Override
     *     public void run() {
     *         //把网络访问的代码放在这里
     *         try {
     *             MailService.send_email(emailStr, fileUrl);
     *         } catch (IOException e) {
     *             e.printStackTrace();
     *         } catch (MessagingException e) {
     *             e.printStackTrace();
     *         } catch (Exception e) {
     *             e.printStackTrace();
     *         }
     *     }
     * }.start();
     */
    public static void main(String[] args) {
        new Thread() {
            @Override
            public void run() {
                //把网络访问的代码放在这里
                try {
                    MailService.send_email("", "url");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
