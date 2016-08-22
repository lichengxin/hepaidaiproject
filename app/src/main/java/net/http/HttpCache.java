/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.http;

/**
 * Author: wyouflf
 * Date: 13-8-1
 * Time: 下午12:04
 */
public class HttpCache {

    /**
     * key: url
     * value: response result
     */
    private final framework.cache.LruMemoryCache<String, String> mMemoryCache;

    private final static int DEFAULT_CACHE_SIZE = 1024 * 100;// string length
    private final static long DEFAULT_EXPIRY_TIME = 1000 * 60; // 60 seconds

    private int cacheSize = DEFAULT_CACHE_SIZE;

    private static long defaultExpiryTime = DEFAULT_EXPIRY_TIME;

    /**
     * HttpCache(HttpCache.DEFAULT_CACHE_SIZE, HttpCache.DEFAULT_EXPIRY_TIME);
     */
    public HttpCache() {
        this(HttpCache.DEFAULT_CACHE_SIZE, HttpCache.DEFAULT_EXPIRY_TIME);
    }

    public HttpCache(int strLength, long defaultExpiryTime) {
        this.cacheSize = strLength;
        HttpCache.defaultExpiryTime = defaultExpiryTime;

        mMemoryCache = new framework.cache.LruMemoryCache<String, String>(this.cacheSize) {
            @Override
            protected int sizeOf(String key, String value) {
                if (value == null) return 0;
                return value.length();
            }
        };
    }

    public void setCacheSize(int strLength) {
        mMemoryCache.setMaxSize(strLength);
    }

    public static void setDefaultExpiryTime(long defaultExpiryTime) {
        HttpCache.defaultExpiryTime = defaultExpiryTime;
    }

    public static long getDefaultExpiryTime() {
        return HttpCache.defaultExpiryTime;
    }

    public void put(String url, String result) {
        put(url, result, defaultExpiryTime);
    }

    public void put(String url, String result, long expiry) {
        if (url == null || result == null || expiry < 1) return;

        mMemoryCache.put(url, result, System.currentTimeMillis() + expiry);
    }

    public String get(String url) {
        return (url != null) ? mMemoryCache.get(url) : null;
    }

    public void clear() {
        mMemoryCache.evictAll();
    }

    public boolean isEnabled(Protocol.HttpMethod method) {
        if (method == null) return false;

        Boolean enabled = httpMethod_enabled_map.get(method.toString());
        return enabled == null ? false : enabled;
    }

    public boolean isEnabled(String method) {
        if (android.text.TextUtils.isEmpty(method)) return false;

        Boolean enabled = httpMethod_enabled_map.get(method.toUpperCase());
        return enabled == null ? false : enabled;
    }

    public void setEnabled(Protocol.HttpMethod method, boolean enabled) {
        httpMethod_enabled_map.put(method.toString(), enabled);
    }

    private final static java.util.concurrent.ConcurrentHashMap<String, Boolean> httpMethod_enabled_map;

    static {
        httpMethod_enabled_map = new java.util.concurrent.ConcurrentHashMap<String, Boolean>(10);
        httpMethod_enabled_map.put(Protocol.HttpMethod.GET.toString(), true);
    }
}
