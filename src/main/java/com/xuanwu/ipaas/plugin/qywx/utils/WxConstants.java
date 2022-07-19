package com.xuanwu.ipaas.plugin.qywx.utils;

/**
 * @author Cheng Hyatt
 * @version 1.0
 * @date 2022/7/18
 */
public class WxConstants {

    public static String CORP_ID = "";
    public static String AUTH_APP_SECRET ="";
    public static String CONTACT_SECRET ="";

    public static String GET_ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
    public static String SEND_MESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s";
}
