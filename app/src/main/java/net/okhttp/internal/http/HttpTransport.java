/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.okhttp.internal.http;

public final class HttpTransport implements Transport {
  private final HttpEngine httpEngine;
  private final HttpConnection httpConnection;

  public HttpTransport(HttpEngine httpEngine, HttpConnection httpConnection) {
    this.httpEngine = httpEngine;
    this.httpConnection = httpConnection;
  }

  @Override public sys.io.Sink createRequestBody(net.okhttp.Request request, long contentLength) throws java.io.IOException {
    if ("chunked".equalsIgnoreCase(request.header("Transfer-Encoding"))) {
      // Stream a request body of unknown length.
      return httpConnection.newChunkedSink();
    }

    if (contentLength != -1) {
      // Stream a request body of a known length.
      return httpConnection.newFixedLengthSink(contentLength);
    }

    throw new IllegalStateException(
        "Cannot stream a request body without chunked encoding or a known content length!");
  }

  @Override public void finishRequest() throws java.io.IOException {
    httpConnection.flush();
  }

  @Override public void writeRequestBody(RetryableSink requestBody) throws java.io.IOException {
    httpConnection.writeRequestBody(requestBody);
  }

  /**
   * Prepares the HTTP headers and sends them to the server.
   *
   * <p>For streaming requests with a body, headers must be prepared
   * <strong>before</strong> the output stream has been written to. Otherwise
   * the body would need to be buffered!
   *
   * <p>For non-streaming requests with a body, headers must be prepared
   * <strong>after</strong> the output stream has been written to and closed.
   * This ensures that the {@code Content-Length} header field receives the
   * proper value.
   */
  public void writeRequestHeaders(net.okhttp.Request request) throws java.io.IOException {
    httpEngine.writingRequestHeaders();
    String requestLine = RequestLine.get(request,
        httpEngine.getConnection().getRoute().getProxy().type(),
        httpEngine.getConnection().getProtocol());
    httpConnection.writeRequest(request.headers(), requestLine);
  }

  @Override public net.okhttp.Response.Builder readResponseHeaders() throws java.io.IOException {
    return httpConnection.readResponse();
  }

  @Override public void releaseConnectionOnIdle() throws java.io.IOException {
    if (canReuseConnection()) {
      httpConnection.poolOnIdle();
    } else {
      httpConnection.closeOnIdle();
    }
  }

  @Override public boolean canReuseConnection() {
    // If the request specified that the connection shouldn't be reused, don't reuse it.
    if ("close".equalsIgnoreCase(httpEngine.getRequest().header("Connection"))) {
      return false;
    }

    // If the response specified that the connection shouldn't be reused, don't reuse it.
    if ("close".equalsIgnoreCase(httpEngine.getResponse().header("Connection"))) {
      return false;
    }

    if (httpConnection.isClosed()) {
      return false;
    }

    return true;
  }

  @Override public net.okhttp.ResponseBody openResponseBody(net.okhttp.Response response) throws java.io.IOException {
	  sys.io.Source source = getTransferStream(response);
    return new RealResponseBody(response.headers(), sys.io.Okio.buffer(source));
  }

  private sys.io.Source getTransferStream(net.okhttp.Response response) throws java.io.IOException {
    if (!HttpEngine.hasBody(response)) {
      return httpConnection.newFixedLengthSource(0);
    }

    if ("chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) {
      return httpConnection.newChunkedSource(httpEngine);
    }

    long contentLength = OkHeaders.contentLength(response);
    if (contentLength != -1) {
      return httpConnection.newFixedLengthSource(contentLength);
    }

    // Wrap the input stream from the connection (rather than just returning
    // "socketIn" directly here), so that we can control its use after the
    // reference escapes.
    return httpConnection.newUnknownLengthSource();
  }

  @Override public void disconnect(HttpEngine engine) throws java.io.IOException {
    httpConnection.closeIfOwnedBy(engine);
  }
}
