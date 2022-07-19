package com.xuanwu.ipaas.plugin.qywx.spi;

import com.xuanwu.ipaas.plugin.sdk._enum.ConnectType;
import com.xuanwu.ipaas.plugin.sdk._enum.PluginErrorCode;
import com.xuanwu.ipaas.plugin.sdk.domain.Connection;
import com.xuanwu.ipaas.plugin.sdk.exception.PluginException;
import com.xuanwu.ipaas.plugin.sdk.spi.ConnectSPI;
import com.xuanwu.ipaas.plugin.sdk.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Map;
import java.util.Properties;

/**
 * @author Cheng Hyatt
 * @version 1.0
 * @date 2022/7/18
 */
@Slf4j
public class ConnectionFactory implements ConnectSPI {

    /**
     * @param input {host,fromAddr,password}
     * @return session
     */
    @Override
    public Connection getConnection(Map<String, Object> input) {
        try {
            log.info("接口入参：" + JsonUtils.toJSONString(input));
            // 参数校验
            if (input.get("host") == null || StringUtils.isBlank(String.valueOf(input.get("host")))) {
                throw PluginException.asPluginException(PluginErrorCode.PLUGIN_PARAM_ERROR, "host不能为空");
            }
            if (input.get("fromAddr") == null || StringUtils.isBlank(String.valueOf(input.get("fromAddr")))) {
                throw PluginException.asPluginException(PluginErrorCode.PLUGIN_PARAM_ERROR, "fromAddr不能为空");
            }
            if (input.get("password") == null || StringUtils.isBlank(String.valueOf(input.get("password")))) {
                throw PluginException.asPluginException(PluginErrorCode.PLUGIN_PARAM_ERROR, "password不能为空");
            }
            String host = String.valueOf(input.get("host"));
            String fromAddr = String.valueOf(input.get("fromAddr"));
            String password = String.valueOf(input.get("password"));

            final Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            //props.put("mail.smtp.host", "smtp.163.com");
            //smtp.exmail.qq.com
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "465");
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.ssl.enable", "true");
            // 发件人的账号
            props.put("mail.user", fromAddr);
            //发件人的密码
            props.put("mail.password", password);

            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // 用户名、密码
                    String userName = props.getProperty("mail.user");
                    String password = props.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };
            // 使用环境属性和授权信息，创建邮件会话
            Session mailSession = Session.getInstance(props, authenticator);
            Long expired = System.currentTimeMillis() + 24 * 60 * 60 * 1000L;
            return new Connection(mailSession, expired, ConnectType.SESSION);
        } catch (Exception e) {
            throw PluginException.asPluginException(PluginErrorCode.CONNECT_INIT_ERROR, "无法建立连接");
        }
    }
}
