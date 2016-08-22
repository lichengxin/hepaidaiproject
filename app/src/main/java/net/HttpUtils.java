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

package net;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import net.http.callback.HttpRedirectHandler;
import net.http.client.DefaultSSLSocketFactory;
import net.http.client.entity.GZipDecompressingEntity;

import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import framework.task.PriorityExecutor;



import java.io.IOException;
import java.util.Locale;

public class HttpUtils {

    public final static net.http.HttpCache sHttpCache = new net.http.HttpCache();

    private final DefaultHttpClient httpClient;
    private final HttpContext httpContext = new BasicHttpContext();

    private HttpRedirectHandler httpRedirectHandler;

    public HttpUtils() {
        this(HttpUtils.DEFAULT_CONN_TIMEOUT, null);
    }

    public HttpUtils(int connTimeout) {
        this(connTimeout, null);
    }

    public HttpUtils(String userAgent) {
        this(HttpUtils.DEFAULT_CONN_TIMEOUT, userAgent);
    }

    public HttpUtils(int connTimeout, String userAgent) {
        HttpParams params = new BasicHttpParams();

        ConnManagerParams.setTimeout(params, connTimeout);
        HttpConnectionParams.setSoTimeout(params, connTimeout);
        HttpConnectionParams.setConnectionTimeout(params, connTimeout);

        if (TextUtils.isEmpty(userAgent)) {
            userAgent = getUserAgent(null);
        }
        HttpProtocolParams.setUserAgent(params, userAgent);

        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(10));
        ConnManagerParams.setMaxTotalConnections(params, 10);

        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setSocketBufferSize(params, 1024 * 8);
        HttpProtocolParams.setVersion(params, org.apache.http.HttpVersion.HTTP_1_1);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", DefaultSSLSocketFactory.getSocketFactory(), 443));

        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);

        httpClient.setHttpRequestRetryHandler(new net.http.client.RetryHandler(DEFAULT_RETRY_TIMES));

        httpClient.addRequestInterceptor(new org.apache.http.HttpRequestInterceptor() {
            @Override
            public void process(org.apache.http.HttpRequest httpRequest, HttpContext httpContext) throws org.apache.http.HttpException, IOException {
                if (!httpRequest.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    httpRequest.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
            }
        });

        httpClient.addResponseInterceptor(new org.apache.http.HttpResponseInterceptor() {
            @Override
            public void process(org.apache.http.HttpResponse response, HttpContext httpContext) throws org.apache.http.HttpException, IOException {
                final org.apache.http.HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return;
                }
                final org.apache.http.Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (org.apache.http.HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase("gzip")) {
                            response.setEntity(new GZipDecompressingEntity(response.getEntity()));
                            return;
                        }
                    }
                }
            }
        });
    }

    // ************************************    default settings & fields ****************************

    private String responseTextCharset = org.apache.http.protocol.HTTP.UTF_8;

    private long currentRequestExpiry = net.http.HttpCache.getDefaultExpiryTime();

    private final static int DEFAULT_CONN_TIMEOUT = 1000 * 15; // 15s

    private final static int DEFAULT_RETRY_TIMES = 3;

    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private final static int DEFAULT_POOL_SIZE = 3;
    private final static PriorityExecutor EXECUTOR = new PriorityExecutor(DEFAULT_POOL_SIZE);

    public org.apache.http.client.HttpClient getHttpClient() {
        return this.httpClient;
    }

    /**
     * @param context if null, use the default format
     *                (Mozilla/5.0 (Linux; U; Android %s) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 %sSafari/534.30).
     * @return
     */
    public static String getUserAgent(Context context) {
        String webUserAgent = null;
        if (context != null) {
            try {
                Class sysResCls = Class.forName("com.android.internal.R$string");
                java.lang.reflect.Field webUserAgentField = sysResCls.getDeclaredField("web_user_agent");
                Integer resId = (Integer) webUserAgentField.get(null);
                webUserAgent = context.getString(resId);
            } catch (Throwable ignored) {
            }
        }
        if (TextUtils.isEmpty(webUserAgent)) {
            webUserAgent = "Mozilla/5.0 (Linux; U; Android %s) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 %sSafari/533.1";
        }

        Locale locale = Locale.getDefault();
        StringBuffer buffer = new StringBuffer();
        // Add version
        final String version = Build.VERSION.RELEASE;
        if (version.length() > 0) {
            buffer.append(version);
        } else {
            // default to "1.0"
            buffer.append("1.0");
        }
        buffer.append("; ");
        final String language = locale.getLanguage();
        if (language != null) {
            buffer.append(language.toLowerCase());
            final String country = locale.getCountry();
            if (country != null) {
                buffer.append("-");
                buffer.append(country.toLowerCase());
            }
        } else {
            // default to "en"
            buffer.append("en");
        }
        // add the model for the release build
        if ("REL".equals(Build.VERSION.CODENAME)) {
            final String model = Build.MODEL;
            if (model.length() > 0) {
                buffer.append("; ");
                buffer.append(model);
            }
        }
        final String id = Build.ID;
        if (id.length() > 0) {
            buffer.append(" Build/");
            buffer.append(id);
        }
        return String.format(webUserAgent, buffer, "Mobile ");
    }

    // ***************************************** config *******************************************

    public HttpUtils configResponseTextCharset(String charSet) {
        if (!TextUtils.isEmpty(charSet)) {
            this.responseTextCharset = charSet;
        }
        return this;
    }

    public HttpUtils configHttpRedirectHandler(HttpRedirectHandler httpRedirectHandler) {
        this.httpRedirectHandler = httpRedirectHandler;
        return this;
    }

    public HttpUtils configHttpCacheSize(int httpCacheSize) {
        sHttpCache.setCacheSize(httpCacheSize);
        return this;
    }

    public HttpUtils configDefaultHttpCacheExpiry(long defaultExpiry) {
    	net.http.HttpCache.setDefaultExpiryTime(defaultExpiry);
        currentRequestExpiry = net.http.HttpCache.getDefaultExpiryTime();
        return this;
    }

    public HttpUtils configCurrentHttpCacheExpiry(long currRequestExpiry) {
        this.currentRequestExpiry = currRequestExpiry;
        return this;
    }

    public HttpUtils configCookieStore(org.apache.http.client.CookieStore cookieStore) {
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        return this;
    }

    public HttpUtils configUserAgent(String userAgent) {
        HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
        return this;
    }

    public HttpUtils configTimeout(int timeout) {
        final HttpParams httpParams = this.httpClient.getParams();
        ConnManagerParams.setTimeout(httpParams, timeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
        return this;
    }

    public HttpUtils configSoTimeout(int timeout) {
        final HttpParams httpParams = this.httpClient.getParams();
        HttpConnectionParams.setSoTimeout(httpParams, timeout);
        return this;
    }

    public HttpUtils configRegisterScheme(Scheme scheme) {
        this.httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
        return this;
    }

    public HttpUtils configSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        Scheme scheme = new Scheme("https", sslSocketFactory, 443);
        this.httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
        return this;
    }

    public HttpUtils configRequestRetryCount(int count) {
        this.httpClient.setHttpRequestRetryHandler(new net.http.client.RetryHandler(count));
        return this;
    }

    public HttpUtils configRequestThreadPoolSize(int threadPoolSize) {
        HttpUtils.EXECUTOR.setPoolSize(threadPoolSize);
        return this;
    }

    // ***************************************** send request *******************************************

    public <T> net.http.HttpHandler<T> send(net.http.Protocol.HttpMethod method, String url,
    		net.http.callback.RequestCallBack<T> callBack) {
        return send(method, url, null, callBack);
    }

    public <T> net.http.HttpHandler<T> send(net.http.Protocol.HttpMethod method, String url, 
    		net.http.RequestParams params,
    		net.http.callback.RequestCallBack<T> callBack) {
        if (url == null) throw new IllegalArgumentException("url may not be null");

        net.http.client.HttpRequest request = new net.http.client.HttpRequest(method, url);
        return sendRequest(request, params, callBack);
    }

    public net.http.ResponseStream sendSync(net.http.Protocol.HttpMethod method, String url) throws net.http.HttpException {
        return sendSync(method, url, null);
    }

    public net.http.ResponseStream sendSync(net.http.Protocol.HttpMethod method, String url, 
    		net.http.RequestParams params) throws net.http.HttpException {
        System.out.println("sendSync:" + url);
        if (url == null) throw new IllegalArgumentException("url may not be null");
        net.http.client.HttpRequest request = new net.http.client.HttpRequest(method, url);
        return sendSyncRequest(request, params);
    }

    // ***************************************** download *******************************************

    public net.http.HttpHandler<java.io.File> download(String url, String target,
    		net.http.callback.RequestCallBack<java.io.File> callback) {
        return download(net.http.Protocol.HttpMethod.GET, url, target, null, false, false, callback);
    }

    public net.http.HttpHandler<java.io.File> download(String url, String target,
                                      boolean autoResume, net.http.callback.RequestCallBack<java.io.File> callback) {
        return download(net.http.Protocol.HttpMethod.GET, url, target, null, autoResume, false, callback);
    }

    public net.http.HttpHandler<java.io.File> download(String url, String target,
                                      boolean autoResume, boolean autoRename, net.http.callback.RequestCallBack<java.io.File> callback) {
        return download(net.http.Protocol.HttpMethod.GET, url, target, null, autoResume, autoRename, callback);
    }

    public net.http.HttpHandler<java.io.File> download(String url, String target,
    		net.http.RequestParams params, net.http.callback.RequestCallBack<java.io.File> callback) {
        return download(net.http.Protocol.HttpMethod.GET, url, target, params, false, false, callback);
    }

    public net.http.HttpHandler<java.io.File> download(String url, String target,
    		net.http.RequestParams params, boolean autoResume, net.http.callback.RequestCallBack<java.io.File> callback) {
        return download(net.http.Protocol.HttpMethod.GET, url, target, params, autoResume, false, callback);
    }

    public net.http.HttpHandler<java.io.File> download(String url, String target,
    		net.http.RequestParams params, boolean autoResume, boolean autoRename, net.http.callback.RequestCallBack<java.io.File> callback) {
        return download(net.http.Protocol.HttpMethod.GET, url, target, params, autoResume, autoRename, callback);
    }

    public net.http.HttpHandler<java.io.File> download(net.http.Protocol.HttpMethod method, String url, String target,
    		net.http.RequestParams params, net.http.callback.RequestCallBack<java.io.File> callback) {
        return download(method, url, target, params, false, false, callback);
    }

    public net.http.HttpHandler<java.io.File> download(net.http.Protocol.HttpMethod method, String url, String target,
    		net.http.RequestParams params, boolean autoResume, net.http.callback.RequestCallBack<java.io.File> callback) {
        return download(method, url, target, params, autoResume, false, callback);
    }

    public net.http.HttpHandler<java.io.File> download(
    		net.http.Protocol.HttpMethod method, 
    		String url, 
    		String target,
    		net.http.RequestParams params, boolean autoResume, boolean autoRename, 
    		net.http.callback.RequestCallBack<java.io.File> callback) {

        if (url == null) throw new IllegalArgumentException("url may not be null");
        if (target == null) throw new IllegalArgumentException("target may not be null");

        net.http.client.HttpRequest request = new net.http.client.HttpRequest(method, url);

        net.http.HttpHandler<java.io.File> handler = new net.http.HttpHandler<java.io.File>(httpClient, httpContext, responseTextCharset, callback);

        handler.setExpiry(currentRequestExpiry);
        handler.setHttpRedirectHandler(httpRedirectHandler);

        if (params != null) {
            request.setRequestParams(params, handler);
            handler.setPriority(params.getPriority());
        }
        handler.executeOnExecutor(EXECUTOR, request, target, autoResume, autoRename);
        return handler;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private <T> net.http.HttpHandler<T> sendRequest(
    		net.http.client.HttpRequest request, 
    		net.http.RequestParams params, 
    		net.http.callback.RequestCallBack<T> callBack) {

    	net.http.HttpHandler<T> handler = new net.http.HttpHandler<T>(httpClient, httpContext, responseTextCharset, callBack);

        handler.setExpiry(currentRequestExpiry);
        handler.setHttpRedirectHandler(httpRedirectHandler);
        request.setRequestParams(params, handler);

        if (params != null) {
            handler.setPriority(params.getPriority());
        }
        handler.executeOnExecutor(EXECUTOR, request);
        return handler;
    }

    private net.http.ResponseStream sendSyncRequest(
    		net.http.client.HttpRequest request, 
    		net.http.RequestParams params) throws net.http.HttpException {

    	net.http.SyncHttpHandler handler = new net.http.SyncHttpHandler(httpClient, httpContext, responseTextCharset);

        handler.setExpiry(currentRequestExpiry);
        handler.setHttpRedirectHandler(httpRedirectHandler);
        request.setRequestParams(params);

        return handler.sendRequest(request);
    }
}
