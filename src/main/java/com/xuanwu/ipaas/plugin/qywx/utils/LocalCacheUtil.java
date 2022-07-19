package com.xuanwu.ipaas.plugin.qywx.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Cheng Hyatt
 * @version 1.0
 * @date 2022/7/18
 */
public class LocalCacheUtil {
    // 缓存对象map
    private static Map<String, Object> cacheMap = new HashMap<String, Object>();
    // 缓存有效期map
    private static Map<String, Long> expireTimeMap = new HashMap<String, Long>();


    /**
     * 获取指定的value，如果key不存在或者已过期，则返回null
     * @param key
     * @return
     */
    public static Object get(String key) {
        if (!cacheMap.containsKey(key)) {
            return null;
        }
        if (expireTimeMap.containsKey(key)) {
            // 缓存失效，已过期
            if (expireTimeMap.get(key) < System.currentTimeMillis()) {
                return null;
            }
        }
        return cacheMap.get(key);
    }

    /**
     * 设置value（不过期）
     * @param key
     * @param value
     */
    public static void set(String key, Object value) {
        cacheMap.put(key, value);
    }

    /**
     * 设置value
     * @param key
     * @param value
     * @param millSeconds 过期时间（毫秒）
     */
    public static void set(final String key, Object value, int millSeconds) {
        final long expireTime = System.currentTimeMillis() + millSeconds;
        cacheMap.put(key, value);
        expireTimeMap.put(key, expireTime);
        if (cacheMap.size() > 2) { // 清除过期数据
            new Thread(() -> {
                Iterator<Map.Entry<String, Object>> iterator = cacheMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    if (expireTimeMap.containsKey(entry.getKey())) {
                        long expireTime1 = expireTimeMap.get(key);
                        if (System.currentTimeMillis() > expireTime1) {
                            iterator.remove();
                            expireTimeMap.remove(entry.getKey());
                        }
                    }
                }
            }).start();
        }
    }
}
