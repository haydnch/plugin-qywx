package com.xuanwu.ipaas.plugin.qywx.spi;

import com.xuanwu.ipaas.plugin.qywx.utils.AccountConstants;
import com.xuanwu.ipaas.plugin.sdk._enum.PluginErrorCode;
import com.xuanwu.ipaas.plugin.sdk.domain.Connection;
import com.xuanwu.ipaas.plugin.sdk.domain.ResultMap;
import com.xuanwu.ipaas.plugin.sdk.exception.PluginException;
import com.xuanwu.ipaas.plugin.sdk.spi.ActionSPI;
import com.xuanwu.ipaas.plugin.sdk.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Cheng Hyatt
 * @version 1.0
 * @date 2022/7/18
 */
@Slf4j
public class SendEmailAction implements ActionSPI {

    public static void main(String[] args) {
        Map<String, Object> connectInput = new HashMap<>();
        connectInput.put("host","smtp.163.com");
        connectInput.put("fromAddr","wave98@163.com");
        connectInput.put("password","AXBQUBEWIWKIPCHN");
        ConnectionFactory connectionFactory = new ConnectionFactory();
        Connection connection = connectionFactory.getConnection(connectInput);
        SendEmailAction send = new SendEmailAction();
        Map<String, Object> actionInput = new HashMap<>();
        actionInput.put("fromAddr", "wave98@163.com");
        actionInput.put("toAddr", "chenghaitao@wxchina.com");
        actionInput.put("title", "1");
        actionInput.put("text", "2");
        Map<String, Object> res = send.action(connection, actionInput);
        System.out.println(JsonUtils.toJSONString(res));
    }

    @Override
    public Map<String, Object> action(Connection connection, Map<String, Object> input) {
        try {
            log.info("接口入参：" + JsonUtils.toJSONString(input));
            if (connection == null) {
                throw PluginException.asPluginException(PluginErrorCode.CONNECT_NOT_FOUND_ERROR, "连接不能为空");
            }
            if(input.get("fromAddr") == null || StringUtils.isBlank(String.valueOf(input.get("fromAddr")))){
                throw PluginException.asPluginException(PluginErrorCode.PLUGIN_PARAM_ERROR, "fromAddr不能为空");
            }
            if (input.get("toAddr") == null || StringUtils.isBlank(String.valueOf(input.get("toAddr")))) {
                throw PluginException.asPluginException(PluginErrorCode.PLUGIN_PARAM_ERROR, "收件人地址不能为空");
            }
            if (input.get("title") == null || StringUtils.isBlank(String.valueOf(input.get("title")))) {
                throw PluginException.asPluginException(PluginErrorCode.PLUGIN_PARAM_ERROR, "邮件标题不能为空");
            }
            if (input.get("text") == null || StringUtils.isBlank(String.valueOf(input.get("text")))) {
                throw PluginException.asPluginException(PluginErrorCode.PLUGIN_PARAM_ERROR, "邮件正文不能为空");
            }
            Session mailSession = (Session) connection.getConnection();
//            String toAddress = "413218232@qq.com";
//            String title = "hello";
//            String text = "wxchina";

            String toAddr = String.valueOf(input.get("toAddr"));
            String fromAddr = String.valueOf(input.get("fromAddr"));
            String title = String.valueOf(input.get("title"));
            String text = String.valueOf(input.get("text"));
            boolean res = sendMail(mailSession,fromAddr,toAddr, title, text);
            if (res) {
                return ResultMap.success(new ResultMap().add("response", "发送成功"));
            }
            return ResultMap.failed(PluginErrorCode.RESULT_EXCEPTION.getCode(), "发送失败");
        } catch (Throwable e) {
            return ResultMap.failed(PluginErrorCode.RESULT_EXCEPTION.getCode(), e.getMessage());
        }
    }

    public boolean sendMail(Session mailSession, String fromAddr, String toAddr, String title, String text) {
        try {

            // 创建邮件消息
            MimeMessage message = new MimeMessage(mailSession);
            // 设置发件人
            InternetAddress from = new InternetAddress(fromAddr);
            message.setFrom(from);

            // 设置收件人
            InternetAddress toAddress = new InternetAddress(toAddr);
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

}
