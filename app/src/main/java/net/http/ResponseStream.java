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
 * Date: 13-7-31
 * Time: 下午3:27
 */
public class ResponseStream extends java.io.InputStream {

    private org.apache.http.HttpResponse baseResponse;
    private java.io.InputStream baseStream;

    private String charset;

    private String requestUrl;
    private String requestMethod;
    private long expiry;

    public ResponseStream(org.apache.http.HttpResponse baseResponse, String requestUrl, long expiry) throws java.io.IOException {
        this(baseResponse, org.apache.http.protocol.HTTP.UTF_8, requestUrl, expiry);
    }

    public ResponseStream(org.apache.http.HttpResponse baseResponse, String charset, String requestUrl, long expiry) throws java.io.IOException {
        if (baseResponse == null) {
            throw new IllegalArgumentException("baseResponse may not be null");
        }

        this.baseResponse = baseResponse;
        this.baseStream = baseResponse.getEntity().getContent();
        this.charset = charset;
        this.requestUrl = requestUrl;
        this.expiry = expiry;
    }

    private String _directResult;

    public ResponseStream(String result) throws java.io.IOException {
        if (result == null) {
            throw new IllegalArgumentException("result may not be null");
        }

        _directResult = result;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    /*package*/ void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public java.io.InputStream getBaseStream() {
        return baseStream;
    }

    public org.apache.http.HttpResponse getBaseResponse() {
        return baseResponse;
    }

    public int getStatusCode() {
        if (_directResult != null) return 200;
        return baseResponse.getStatusLine().getStatusCode();
    }

    public java.util.Locale getLocale() {
        if (_directResult != null) return java.util.Locale.getDefault();
        return baseResponse.getLocale();
    }

    public String getReasonPhrase() {
        if (_directResult != null) return "";
        return baseResponse.getStatusLine().getReasonPhrase();
    }

    public String readString() throws java.io.IOException {
        if (_directResult != null) return _directResult;
        if (baseStream == null) return null;
        try {
        	java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(baseStream, charset));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            _directResult = sb.toString();
            if (requestUrl != null && net.HttpUtils.sHttpCache.isEnabled(requestMethod)) {
            	net.HttpUtils.sHttpCache.put(requestUrl, _directResult, expiry);
            }
            return _directResult;
        } finally {
        	utils.IOUtils.closeQuietly(baseStream);
        }
    }

    public void readFile(String savePath) throws java.io.IOException {
        if (_directResult != null) return;
        if (baseStream == null) return;
        java.io.BufferedOutputStream out = null;
        try {
            out = new java.io.BufferedOutputStream(new java.io.FileOutputStream(savePath));
            java.io.BufferedInputStream ins = new java.io.BufferedInputStream(baseStream);
            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = ins.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } finally {
        	utils.IOUtils.closeQuietly(out);
        	utils.IOUtils.closeQuietly(baseStream);
        }
    }

    @Override
    public int read() throws java.io.IOException {
        if (baseStream == null) return -1;
        return baseStream.read();
    }

    @Override
    public int available() throws java.io.IOException {
        if (baseStream == null) return 0;
        return baseStream.available();
    }

    @Override
    public void close() throws java.io.IOException {
        if (baseStream == null) return;
        baseStream.close();
    }

    @Override
    public void mark(int readLimit) {
        if (baseStream == null) return;
        baseStream.mark(readLimit);
    }

    @Override
    public boolean markSupported() {
        if (baseStream == null) return false;
        return baseStream.markSupported();
    }

    @Override
    public int read(byte[] buffer) throws java.io.IOException {
        if (baseStream == null) return -1;
        return baseStream.read(buffer);
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws java.io.IOException {
        if (baseStream == null) return -1;
        return baseStream.read(buffer, offset, length);
    }

    @Override
    public synchronized void reset() throws java.io.IOException {
        if (baseStream == null) return;
        baseStream.reset();
    }

    @Override
    public long skip(long byteCount) throws java.io.IOException {
        if (baseStream == null) return 0;
        return baseStream.skip(byteCount);
    }

    public long getContentLength() {
        if (baseStream == null) return 0;
        return baseResponse.getEntity().getContentLength();
    }
}
