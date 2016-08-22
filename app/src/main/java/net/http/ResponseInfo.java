package net.http;

/**
 * Author: wyouflf
 * Date: 13-10-26
 * Time: 下午3:20
 */
public final class ResponseInfo<T> {

    private final org.apache.http.HttpResponse response;
    public T result;
    public final boolean resultFormCache;

    public final java.util.Locale locale;

    // status line
    public final int statusCode;
    public final org.apache.http.ProtocolVersion protocolVersion;
    public final String reasonPhrase;

    // entity
    public final long contentLength;
    public final org.apache.http.Header contentType;
    public final org.apache.http.Header contentEncoding;

    public org.apache.http.Header[] getAllHeaders() {
        if (response == null) return null;
        return response.getAllHeaders();
    }

    public org.apache.http.Header[] getHeaders(String name) {
        if (response == null) return null;
        return response.getHeaders(name);
    }

    public org.apache.http.Header getFirstHeader(String name) {
        if (response == null) return null;
        return response.getFirstHeader(name);
    }

    public org.apache.http.Header getLastHeader(String name) {
        if (response == null) return null;
        return response.getLastHeader(name);
    }

    public ResponseInfo(final org.apache.http.HttpResponse response, T result, boolean resultFormCache) {
        this.response = response;
        this.result = result;
        this.resultFormCache = resultFormCache;

        if (response != null) {
            locale = response.getLocale();

            // status line
            org.apache.http.StatusLine statusLine = response.getStatusLine();
            if (statusLine != null) {
                statusCode = statusLine.getStatusCode();
                protocolVersion = statusLine.getProtocolVersion();
                reasonPhrase = statusLine.getReasonPhrase();
            } else {
                statusCode = 0;
                protocolVersion = null;
                reasonPhrase = null;
            }

            // entity
            org.apache.http.HttpEntity entity = response.getEntity();
            if (entity != null) {
                contentLength = entity.getContentLength();
                contentType = entity.getContentType();
                contentEncoding = entity.getContentEncoding();
            } else {
                contentLength = 0;
                contentType = null;
                contentEncoding = null;
            }
        } else {
            locale = null;

            // status line
            statusCode = 0;
            protocolVersion = null;
            reasonPhrase = null;

            // entity
            contentLength = 0;
            contentType = null;
            contentEncoding = null;
        }
    }
}
