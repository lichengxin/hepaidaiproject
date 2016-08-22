package net.okhttp.internal.http;

public final class RequestLine {
  private RequestLine() {
  }

  /**
   * Returns the request status line, like "GET / HTTP/1.1". This is exposed
   * to the application by {@link HttpURLConnection#getHeaderFields}, so it
   * needs to be set even if the transport is SPDY.
   */
  static String get(net.okhttp.Request request, java.net.Proxy.Type proxyType, net.http.Protocol.ProtocolVersion protocol) {
    StringBuilder result = new StringBuilder();
    result.append(request.method());
    result.append(' ');

    if (includeAuthorityInRequestLine(request, proxyType)) {
      result.append(request.httpUrl());
    } else {
      result.append(requestPath(request.httpUrl()));
    }

    result.append(' ');
    result.append(version(protocol));
    return result.toString();
  }

  /**
   * Returns true if the request line should contain the full URL with host
   * and port (like "GET http://android.com/foo HTTP/1.1") or only the path
   * (like "GET /foo HTTP/1.1").
   */
  private static boolean includeAuthorityInRequestLine(net.okhttp.Request request, java.net.Proxy.Type proxyType) {
    return !request.isHttps() && proxyType == java.net.Proxy.Type.HTTP;
  }

  /**
   * Returns the path to request, like the '/' in 'GET / HTTP/1.1'. Never empty,
   * even if the request URL is. Includes the query component if it exists.
   */
  public static String requestPath(net.okhttp.HttpUrl url) {
    String path = url.encodedPath();
    String query = url.encodedQuery();
    return query != null ? (path + '?' + query) : path;
  }

  public static String version(net.http.Protocol.ProtocolVersion protocol) {
    return protocol == net.http.Protocol.ProtocolVersion.HTTP_1_0 ? "HTTP/1.0" : "HTTP/1.1";
  }
}
