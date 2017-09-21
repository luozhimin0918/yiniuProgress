package com.library.base.http;

import com.library.util.ConvertUtils;

import java.util.HashMap;

/**
 * Created by Mr'Dai on 2017/9/21.
 * 提前缓存Http数据
 */
public class PreCacheHttpResponse {
    private static HashMap<String, Object> cacheHashMap = new HashMap<>();

    private static PreCacheHttpResponse preCacheHttpResponse;

    public static PreCacheHttpResponse getInstance() {
        if (preCacheHttpResponse == null) {
            preCacheHttpResponse = new PreCacheHttpResponse();
        }
        return preCacheHttpResponse;
    }

    public void addCacheData(String url, Object object) {
        String md5Url = ConvertUtils.md5(url);
        cacheHashMap.put(md5Url, object);
    }

    public Object getCacheData(String url) {
        String md5Url = ConvertUtils.md5(url);

        Object cacheData = cacheHashMap.get(md5Url);
        cacheHashMap.remove(md5Url);

        return cacheData;
    }
}
