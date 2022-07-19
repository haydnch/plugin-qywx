package com.xuanwu.ipaas.plugin.qywx.utils.func;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @author Cheng Hyatt
 * @version 1.0
 * @date 2022/7/19
 */
public class SendEmail {
    /**
     * @param mailSession
     * @param fromAddr
     * @param toAddr
     * @return
     */
    public static boolean sendPlainText(Session mailSession, String fromAddr, String toAddr, String title, String text) {
        try {

            // 创建邮件消息
            MimeMessage message = new MimeMessage(mailSession);
            // 设置发件人
            InternetAddress from = new InternetAddress(fromAddr);
            message.setFrom(from);

            // 设置收件人
            InternetAddress toAddress = new InternetAddress(toAddr);
            /**
             * 设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
             * MimeMessage.RecipientType.TO:发送
             * MimeMessage.RecipientType.CC：抄送
             * MimeMessage.RecipientType.BCC：密送
             */
            message.setRecipient(Message.RecipientType.TO, toAddress);

            // 设置邮件标题
            message.setSubject(title);

            // 设置邮件的内容体
            message.setContent(text, "text/html;charset=UTF-8");
            // 发送邮件
            Transport.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean sendMultiPart(Session mailSession, String fromAddr, String toAddr, String title, String text, String img, String appendix) {
        try {
            MimeMessage message = new MimeMessage(mailSession);
            // 设置发件人
            InternetAddress from = new InternetAddress(fromAddr);
            message.setFrom(from);
            // 设置收件人
            InternetAddress toAddress = new InternetAddress(toAddr);
            message.setRecipient(Message.RecipientType.TO, toAddress);
            // 设置邮件标题
            message.setSubject(title);
            // 设置邮件内容
            MimeMultipart mimeMultipart = new MimeMultipart();
            // 1.图片
            if (img != null && !img.isEmpty()) {
                MimeBodyPart image = new MimeBodyPart();
                image.setDataHandler(new DataHandler(new FileDataSource(img)));
                image.setContentID("test.jpg");
                mimeMultipart.addBodyPart(image);
            }
            // 2.文本
            MimeBodyPart textBody = new MimeBodyPart();
            textBody.setContent(text + "<img src='cid:test.jpg'>", "text/html;charset=utf-8");
            // 拼接文本和图片
            mimeMultipart.addBodyPart(textBody);
            mimeMultipart.setSubType("related");
            MimeBodyPart content = new MimeBodyPart();
            content.setContent(mimeMultipart);
            // 3.附件
            MimeMultipart allFile = new MimeMultipart();
            if (appendix != null && !appendix.isEmpty()) {
                MimeBodyPart appendixPart = new MimeBodyPart();
                appendixPart.setDataHandler(new DataHandler(new FileDataSource(appendix)));
                appendixPart.setFileName("test.txt");
                allFile.addBodyPart(appendixPart);
            }
            allFile.addBodyPart(content);
            //正文和附件都存在邮件中，所有类型设置为mixed
            allFile.setSubType("mixed");
            message.setContent(allFile);
            message.saveChanges();//保存修改
            Transport.send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
