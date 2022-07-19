package com.xuanwu.ipaas.plugin.qywx.utils;

import com.xuanwu.ipaas.plugin.sdk.util.HttpClientUtils;
import com.xuanwu.ipaas.plugin.sdk.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author Cheng Hyatt
 * @version 1.0
 * @date 2022/7/18
 */

@Slf4j
public class WxApiUtil {

    private static String accessTokenAuth = "access_token_auth";
    private static String accessTokenContact = "access_token_contact";

    //    errcode	出错返回码，为0表示成功，非0表示调用失败
    //    errmsg	返回码提示语
    //    access_token	获取到的凭证，最长为512字节
    //    expires_in	凭证的有效时间（秒）
    public static String getAccessToken(String type) {

        Object o = LocalCacheUtil.get(type);
        if (o != null) {
            return o.toString();
        }
        String secret = "";
        if (type.equals(accessTokenAuth)) {
            secret = WxConstants.AUTH_APP_SECRET;
        } else if (type.equals(accessTokenContact)) {
            secret = WxConstants.CONTACT_SECRET;
        }
        // 获取token
        String json = HttpClientUtils.httpGet(String.format(WxConstants.GET_ACCESS_TOKEN_URL, WxConstants.CORP_ID, secret), null);
        Map<String, Object> result = JsonUtils.parseObject(json, Map.class);
        if (JsonUtils.get(json, "errcode").asText() == "0") {
            String accessToken = JsonUtils.get(json, "access_token").asText();
            int expires_in = JsonUtils.get(json, "expires_in").asInt();
            LocalCacheUtil.set(type, accessToken, expires_in * 1000);
            return accessToken;
        } else {
            log.info("get accessToken error: {}", result.toString());
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(WxApiUtil.getAccessToken(accessTokenAuth));
    }

}
