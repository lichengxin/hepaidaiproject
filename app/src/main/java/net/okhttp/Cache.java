/*
 * Copyright (C) 2010 The Android Open Source Project
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

package net.okhttp;

import java.util.Collections;
import java.util.Iterator;

/**
 * Caches HTTP and HTTPS responses to the filesystem so they may be reused, saving time and
 * bandwidth.
 *
 * <h3>Cache Optimization</h3>
 * To measure cache effectiveness, this class tracks three statistics:
 * <ul>
 *   <li><strong>{@linkplain #getRequestCount() Request Count:}</strong> the number of HTTP
 *     requests issued since this cache was created.
 *   <li><strong>{@linkplain #getNetworkCount() Network Count:}</strong> the number of those
 *     requests that required network use.
 *   <li><strong>{@linkplain #getHitCount() Hit Count:}</strong> the number of those requests whose
 *     responses were served by the cache.
 * </ul>
 *
 * Sometimes a request will result in a conditional cache hit. If the cache contains a stale copy of
 * the response, the client will issue a conditional {@code GET}. The server will then send either
 * the updated response if it has changed, or a short 'not modified' response if the client's copy
 * is still valid. Such responses increment both the network count and hit count.
 *
 * <p>The best way to improve the cache hit rate is by configuring the web server to return
 * cacheable responses. Although this client honors all <a
 * href="http://tools.ietf.org/html/rfc7234">HTTP/1.1 (RFC 7234)</a> cache headers, it doesn't cache
 * partial responses.
 *
 * <h3>Force a Network Response</h3>
 * In some situations, such as after a user clicks a 'refresh' button, it may be necessary to skip
 * the cache, and fetch data directly from the server. To force a full refresh, add the {@code
 * no-cache} directive: <pre>   {@code
 *
 *   Request request = new Request.Builder()
 *       .cacheControl(new CacheControl.Builder().noCache().build())
 *       .url("http://publicobject.com/helloworld.txt")
 *       .build();
 * }</pre>
 *
 * If it is only necessary to force a cached response to be validated by the server, use the more
 * efficient {@code max-age=0} directive instead: <pre>   {@code
 *
 *   Request request = new Request.Builder()
 *       .cacheControl(new CacheControl.Builder()
 *           .maxAge(0, TimeUnit.SECONDS)
 *           .build())
 *       .url("http://publicobject.com/helloworld.txt")
 *       .build();
 * }</pre>
 *
 * <h3>Force a Cache Response</h3>
 * Sometimes you'll want to show resources if they are available immediately, but not otherwise.
 * This can be used so your application can show <i>something</i> while waiting for the latest data
 * to be downloaded. To restrict a request to locally-cached resources, add the {@code
 * only-if-cached} directive: <pre>   {@code
 *
 *     Request request = new Request.Builder()
 *         .cacheControl(new CacheControl.Builder()
 *             .onlyIfCached()
 *             .build())
 *         .url("http://publicobject.com/helloworld.txt")
 *         .build();
 *     Response forceCacheResponse = client.newCall(request).execute();
 *     if (forceCacheResponse.code() != 504) {
 *       // The resource was cached! Show it.
 *     } else {
 *       // The resource was not cached.
 *     }
 * }</pre>
 * This technique works even better in situations where a stale response is better than no response.
 * To permit stale cached responses, use the {@code max-stale} directive with the maximum staleness
 * in seconds: <pre>   {@code
 *
 *   Request request = new Request.Builder()
 *       .cacheControl(new CacheControl.Builder()
 *           .maxStale(365, TimeUnit.DAYS)
 *           .build())
 *       .url("http://publicobject.com/helloworld.txt")
 *       .build();
 * }</pre>
 *
 * <p>The {@link CacheControl} class can configure request caching directives and parse response
 * caching directives. It even offers convenient constants {@link CacheControl#FORCE_NETWORK} and
 * {@link CacheControl#FORCE_CACHE} that address the use cases above.
 */
public final class Cache {
  private static final int VERSION = 201105;
  private static final int ENTRY_METADATA = 0;
  private static final int ENTRY_BODY = 1;
  private static final int ENTRY_COUNT = 2;

  final net.okhttp.internal.InternalCache internalCache = 
		  new net.okhttp.internal.InternalCache() {
    @Override public Response get(Request request) throws java.io.IOException {
      return Cache.this.get(request);
    }
    @Override public net.okhttp.internal.http.CacheRequest put(Response response) throws java.io.IOException {
      return Cache.this.put(response);
    }
    @Override public void remove(Request request) throws java.io.IOException {
      Cache.this.remove(request);
    }
    @Override public void update(Response cached, Response network) throws java.io.IOException {
      Cache.this.update(cached, network);
    }
    @Override public void trackConditionalCacheHit() {
      Cache.this.trackConditionalCacheHit();
    }
    @Override public void trackResponse(net.okhttp.internal.http.CacheStrategy cacheStrategy) {
      Cache.this.trackResponse(cacheStrategy);
    }
  };

  private final net.okhttp.internal.DiskLruCache cache;

  /* read and write statistics, all guarded by 'this' */
  private int writeSuccessCount;
  private int writeAbortCount;
  private int networkCount;
  private int hitCount;
  private int requestCount;

  public Cache(java.io.File directory, long maxSize) {
    this(directory, maxSize, sys.io.FileSystem.SYSTEM);
  }

  Cache(java.io.File directory, long maxSize, sys.io.FileSystem fileSystem) {
    this.cache = net.okhttp.internal.DiskLruCache.create(fileSystem, directory, VERSION, ENTRY_COUNT, maxSize);
  }

  private static String urlToKey(Request request) {
    return net.okhttp.internal.Util.md5Hex(request.urlString());
  }

  Response get(Request request) {
    String key = urlToKey(request);
    net.okhttp.internal.DiskLruCache.Snapshot snapshot;
    Entry entry;
    try {
      snapshot = cache.get(key);
      if (snapshot == null) {
        return null;
      }
    } catch (java.io.IOException e) {
      // Give up because the cache cannot be read.
      return null;
    }

    try {
      entry = new Entry(snapshot.getSource(ENTRY_METADATA));
    } catch (java.io.IOException e) {
    	net.okhttp.internal.Util.closeQuietly(snapshot);
      return null;
    }

    Response response = entry.response(request, snapshot);

    if (!entry.matches(request, response)) {
    	net.okhttp.internal.Util.closeQuietly(response.body());
      return null;
    }

    return response;
  }

  private net.okhttp.internal.http.CacheRequest put(Response response) throws java.io.IOException {
    String requestMethod = response.request().method();

    if (net.http.Protocol.invalidatesCache(response.request().method())) {
      try {
        remove(response.request());
      } catch (java.io.IOException ignored) {
        // The cache cannot be written.
      }
      return null;
    }
    if (!requestMethod.equals("GET")) {
      // Don't cache non-GET responses. We're technically allowed to cache
      // HEAD requests and some POST requests, but the complexity of doing
      // so is high and the benefit is low.
      return null;
    }

    if (net.okhttp.internal.http.OkHeaders.hasVaryAll(response)) {
      return null;
    }

    Entry entry = new Entry(response);
    net.okhttp.internal.DiskLruCache.Editor editor = null;
    try {
      editor = cache.edit(urlToKey(response.request()));
      if (editor == null) {
        return null;
      }
      entry.writeTo(editor);
      return new CacheRequestImpl(editor);
    } catch (java.io.IOException e) {
      abortQuietly(editor);
      return null;
    }
  }

  private void remove(Request request) throws java.io.IOException {
    cache.remove(urlToKey(request));
  }

  private void update(Response cached, Response network) {
    Entry entry = new Entry(network);
    net.okhttp.internal.DiskLruCache.Snapshot snapshot = ((CacheResponseBody) cached.body()).snapshot;
    net.okhttp.internal.DiskLruCache.Editor editor = null;
    try {
      editor = snapshot.edit(); // Returns null if snapshot is not current.
      if (editor != null) {
        entry.writeTo(editor);
        editor.commit();
      }
    } catch (java.io.IOException e) {
      abortQuietly(editor);
    }
  }

  private void abortQuietly(net.okhttp.internal.DiskLruCache.Editor editor) {
    // Give up because the cache cannot be written.
    try {
      if (editor != null) {
        editor.abort();
      }
    } catch (java.io.IOException ignored) {
    }
  }

  /**
   * Initialize the cache. This will include reading the journal files from
   * the storage and building up the necessary in-memory cache information.
   * <p>
   * The initialization time may vary depending on the journal file size and
   * the current actual cache size. The application needs to be aware of calling
   * this function during the initialization phase and preferably in a background
   * worker thread.
   * <p>
   * Note that if the application chooses to not call this method to initialize
   * the cache. By default, the okhttp will perform lazy initialization upon the
   * first usage of the cache.
   */
  public void initialize() throws java.io.IOException {
    cache.initialize();
  }

  /**
   * Closes the cache and deletes all of its stored values. This will delete
   * all files in the cache directory including files that weren't created by
   * the cache.
   */
  public void delete() throws java.io.IOException {
    cache.delete();
  }

  /**
   * Deletes all values stored in the cache. In-flight writes to the cache will
   * complete normally, but the corresponding responses will not be stored.
   */
  public void evictAll() throws java.io.IOException {
    cache.evictAll();
  }

  /**
   * Returns an iterator over the URLs in this cache. This iterator doesn't throw {@code
   * ConcurrentModificationException}, but if new responses are added while iterating, their URLs
   * will not be returned. If existing responses are evicted during iteration, they will be absent
   * (unless they were already returned).
   *
   * <p>The iterator supports {@linkplain Iterator#remove}. Removing a URL from the iterator evicts
   * the corresponding response from the cache. Use this to evict selected responses.
   */
  public Iterator<String> urls() throws java.io.IOException {
    return new Iterator<String>() {
      final Iterator<net.okhttp.internal.DiskLruCache.Snapshot> delegate = cache.snapshots();

      String nextUrl;
      boolean canRemove;

      @Override public boolean hasNext() {
        if (nextUrl != null) return true;

        canRemove = false; // Prevent delegate.remove() on the wrong item!
        while (delegate.hasNext()) {
        	net.okhttp.internal.DiskLruCache.Snapshot snapshot = delegate.next();
          try {
        	  sys.io.BufferedSource metadata = sys.io.Okio.buffer(snapshot.getSource(ENTRY_METADATA));
            nextUrl = metadata.readUtf8LineStrict();
            return true;
          } catch (java.io.IOException ignored) {
            // We couldn't read the metadata for this snapshot; possibly because the host filesystem
            // has disappeared! Skip it.
          } finally {
            snapshot.close();
          }
        }

        return false;
      }

      @Override public String next() {
        if (!hasNext()) throw new java.util.NoSuchElementException();
        String result = nextUrl;
        nextUrl = null;
        canRemove = true;
        return result;
      }

      @Override public void remove() {
        if (!canRemove) throw new IllegalStateException("remove() before next()");
        delegate.remove();
      }
    };
  }

  public synchronized int getWriteAbortCount() {
    return writeAbortCount;
  }

  public synchronized int getWriteSuccessCount() {
    return writeSuccessCount;
  }

  public long getSize() throws java.io.IOException {
    return cache.size();
  }

  public long getMaxSize() {
    return cache.getMaxSize();
  }

  public void flush() throws java.io.IOException {
    cache.flush();
  }

  public void close() throws java.io.IOException {
    cache.close();
  }

  public java.io.File getDirectory() {
    return cache.getDirectory();
  }

  public boolean isClosed() {
    return cache.isClosed();
  }

  private synchronized void trackResponse(net.okhttp.internal.http.CacheStrategy cacheStrategy) {
    requestCount++;

    if (cacheStrategy.networkRequest != null) {
      // If this is a conditional request, we'll increment hitCount if/when it hits.
      networkCount++;

    } else if (cacheStrategy.cacheResponse != null) {
      // This response uses the cache and not the network. That's a cache hit.
      hitCount++;
    }
  }

  private synchronized void trackConditionalCacheHit() {
    hitCount++;
  }

  public synchronized int getNetworkCount() {
    return networkCount;
  }

  public synchronized int getHitCount() {
    return hitCount;
  }

  public synchronized int getRequestCount() {
    return requestCount;
  }

  private final class CacheRequestImpl implements net.okhttp.internal.http.CacheRequest {
    private final net.okhttp.internal.DiskLruCache.Editor editor;
    private sys.io.Sink cacheOut;
    private boolean done;
    private sys.io.Sink body;

    public CacheRequestImpl(final net.okhttp.internal.DiskLruCache.Editor editor) throws java.io.IOException {
      this.editor = editor;
      this.cacheOut = editor.newSink(ENTRY_BODY);
      this.body = new sys.io.ForwardingSink(cacheOut) {
        @Override public void close() throws java.io.IOException {
          synchronized (Cache.this) {
            if (done) {
              return;
            }
            done = true;
            writeSuccessCount++;
          }
          super.close();
          editor.commit();
        }
      };
    }

    @Override public void abort() {
      synchronized (Cache.this) {
        if (done) {
          return;
        }
        done = true;
        writeAbortCount++;
      }
      net.okhttp.internal.Util.closeQuietly(cacheOut);
      try {
        editor.abort();
      } catch (java.io.IOException ignored) {
      }
    }

    @Override public sys.io.Sink body() {
      return body;
    }
  }

  private static final class Entry {
    private final String url;
    private final Headers varyHeaders;
    private final String requestMethod;
    private final net.http.Protocol.ProtocolVersion protocol;
    private final int code;
    private final String message;
    private final Headers responseHeaders;
    private final Handshake handshake;

    /**
     * Reads an entry from an input stream. A typical entry looks like this:
     * <pre>{@code
     *   http://google.com/foo
     *   GET
     *   2
     *   Accept-Language: fr-CA
     *   Accept-Charset: UTF-8
     *   HTTP/1.1 200 OK
     *   3
     *   Content-Type: image/png
     *   Content-Length: 100
     *   Cache-Control: max-age=600
     * }</pre>
     *
     * <p>A typical HTTPS file looks like this:
     * <pre>{@code
     *   https://google.com/foo
     *   GET
     *   2
     *   Accept-Language: fr-CA
     *   Accept-Charset: UTF-8
     *   HTTP/1.1 200 OK
     *   3
     *   Content-Type: image/png
     *   Content-Length: 100
     *   Cache-Control: max-age=600
     *
     *   AES_256_WITH_MD5
     *   2
     *   base64-encoded peerCertificate[0]
     *   base64-encoded peerCertificate[1]
     *   -1
     * }</pre>
     * The file is newline separated. The first two lines are the URL and
     * the request method. Next is the number of HTTP Vary request header
     * lines, followed by those lines.
     *
     * <p>Next is the response status line, followed by the number of HTTP
     * response header lines, followed by those lines.
     *
     * <p>HTTPS responses also contain SSL session information. This begins
     * with a blank line, and then a line containing the cipher suite. Next
     * is the length of the peer certificate chain. These certificates are
     * base64-encoded and appear each on their own line. The next line
     * contains the length of the local certificate chain. These
     * certificates are also base64-encoded and appear each on their own
     * line. A length of -1 is used to encode a null array.
     */
    public Entry(sys.io.Source in) throws java.io.IOException {
      try {
    	  sys.io.BufferedSource source = sys.io.Okio.buffer(in);
        url = source.readUtf8LineStrict();
        requestMethod = source.readUtf8LineStrict();
        Headers.Builder varyHeadersBuilder = new Headers.Builder();
        int varyRequestHeaderLineCount = readInt(source);
        for (int i = 0; i < varyRequestHeaderLineCount; i++) {
          varyHeadersBuilder.addLenient(source.readUtf8LineStrict());
        }
        varyHeaders = varyHeadersBuilder.build();

        net.okhttp.internal.http.StatusLine statusLine = net.okhttp.internal.http.StatusLine.parse(source.readUtf8LineStrict());
        protocol = statusLine.protocol;
        code = statusLine.code;
        message = statusLine.message;
        Headers.Builder responseHeadersBuilder = new Headers.Builder();
        int responseHeaderLineCount = readInt(source);
        for (int i = 0; i < responseHeaderLineCount; i++) {
          responseHeadersBuilder.addLenient(source.readUtf8LineStrict());
        }
        responseHeaders = responseHeadersBuilder.build();

        if (isHttps()) {
          String blank = source.readUtf8LineStrict();
          if (blank.length() > 0) {
            throw new java.io.IOException("expected \"\" but was \"" + blank + "\"");
          }
          String cipherSuite = source.readUtf8LineStrict();
          java.util.List<java.security.cert.Certificate> peerCertificates = readCertificateList(source);
          java.util.List<java.security.cert.Certificate> localCertificates = readCertificateList(source);
          handshake = Handshake.get(cipherSuite, peerCertificates, localCertificates);
        } else {
          handshake = null;
        }
      } finally {
        in.close();
      }
    }

    public Entry(Response response) {
      this.url = response.request().urlString();
      this.varyHeaders = net.okhttp.internal.http.OkHeaders.varyHeaders(response);
      this.requestMethod = response.request().method();
      this.protocol = response.protocol();
      this.code = response.code();
      this.message = response.message();
      this.responseHeaders = response.headers();
      this.handshake = response.handshake();
    }

    public void writeTo(net.okhttp.internal.DiskLruCache.Editor editor) throws java.io.IOException {
    	sys.io.BufferedSink sink = sys.io.Okio.buffer(editor.newSink(ENTRY_METADATA));

      sink.writeUtf8(url);
      sink.writeByte('\n');
      sink.writeUtf8(requestMethod);
      sink.writeByte('\n');
      sink.writeDecimalLong(varyHeaders.size());
      sink.writeByte('\n');
      for (int i = 0, size = varyHeaders.size(); i < size; i++) {
        sink.writeUtf8(varyHeaders.name(i));
        sink.writeUtf8(": ");
        sink.writeUtf8(varyHeaders.value(i));
        sink.writeByte('\n');
      }

      sink.writeUtf8(new net.okhttp.internal.http.StatusLine(protocol, code, message).toString());
      sink.writeByte('\n');
      sink.writeDecimalLong(responseHeaders.size());
      sink.writeByte('\n');
      for (int i = 0, size = responseHeaders.size(); i < size; i++) {
        sink.writeUtf8(responseHeaders.name(i));
        sink.writeUtf8(": ");
        sink.writeUtf8(responseHeaders.value(i));
        sink.writeByte('\n');
      }

      if (isHttps()) {
        sink.writeByte('\n');
        sink.writeUtf8(handshake.cipherSuite());
        sink.writeByte('\n');
        writeCertList(sink, handshake.peerCertificates());
        writeCertList(sink, handshake.localCertificates());
      }
      sink.close();
    }

    private boolean isHttps() {
      return url.startsWith("https://");
    }

    private java.util.List<java.security.cert.Certificate> readCertificateList(sys.io.BufferedSource source) throws java.io.IOException {
      int length = readInt(source);
      if (length == -1) return Collections.emptyList(); // OkHttp v1.2 used -1 to indicate null.

      try {
    	  java.security.cert.CertificateFactory certificateFactory = java.security.cert.CertificateFactory.getInstance("X.509");
        java.util.List<java.security.cert.Certificate> result = new java.util.ArrayList<>(length);
        for (int i = 0; i < length; i++) {
          String line = source.readUtf8LineStrict();
          sys.io.Buffer bytes = new sys.io.Buffer();
          bytes.write(sys.io.ByteString.decodeBase64(line));
          result.add(certificateFactory.generateCertificate(bytes.inputStream()));
        }
        return result;
      } catch (java.security.cert.CertificateException e) {
        throw new java.io.IOException(e.getMessage());
      }
    }

    private void writeCertList(sys.io.BufferedSink sink, java.util.List<java.security.cert.Certificate> certificates)
        throws java.io.IOException {
      try {
        sink.writeDecimalLong(certificates.size());
        sink.writeByte('\n');
        for (int i = 0, size = certificates.size(); i < size; i++) {
          byte[] bytes = certificates.get(i).getEncoded();
          String line = sys.io.ByteString.of(bytes).base64();
          sink.writeUtf8(line);
          sink.writeByte('\n');
        }
      } catch (java.security.cert.CertificateEncodingException e) {
        throw new java.io.IOException(e.getMessage());
      }
    }

    public boolean matches(Request request, Response response) {
      return url.equals(request.urlString())
          && requestMethod.equals(request.method())
          && net.okhttp.internal.http.OkHeaders.varyMatches(response, varyHeaders, request);
    }

    public Response response(Request request, net.okhttp.internal.DiskLruCache.Snapshot snapshot) {
      String contentType = responseHeaders.get("Content-Type");
      String contentLength = responseHeaders.get("Content-Length");
      Request cacheRequest = new Request.Builder()
          .url(url)
          .method(requestMethod, null)
          .headers(varyHeaders)
          .build();
      return new Response.Builder()
          .request(cacheRequest)
          .protocol(protocol)
          .code(code)
          .message(message)
          .headers(responseHeaders)
          .body(new CacheResponseBody(snapshot, contentType, contentLength))
          .handshake(handshake)
          .build();
    }
  }

  private static int readInt(sys.io.BufferedSource source) throws java.io.IOException {
    try {
      long result = source.readDecimalLong();
      String line = source.readUtf8LineStrict();
      if (result < 0 || result > Integer.MAX_VALUE || !line.isEmpty()) {
        throw new java.io.IOException("expected an int but was \"" + result + line + "\"");
      }
      return (int) result;
    } catch (NumberFormatException e) {
      throw new java.io.IOException(e.getMessage());
    }
  }

  private static class CacheResponseBody extends ResponseBody {
    private final net.okhttp.internal.DiskLruCache.Snapshot snapshot;
    private final sys.io.BufferedSource bodySource;
    private final String contentType;
    private final String contentLength;

    public CacheResponseBody(final net.okhttp.internal.DiskLruCache.Snapshot snapshot,
        String contentType, String contentLength) {
      this.snapshot = snapshot;
      this.contentType = contentType;
      this.contentLength = contentLength;

      sys.io.Source source = snapshot.getSource(ENTRY_BODY);
      bodySource = sys.io.Okio.buffer(new sys.io.ForwardingSource(source) {
        @Override public void close() throws java.io.IOException {
          snapshot.close();
          super.close();
        }
      });
    }

    @Override public MediaType contentType() {
      return contentType != null ? MediaType.parse(contentType) : null;
    }

    @Override public long contentLength() {
      try {
        return contentLength != null ? Long.parseLong(contentLength) : -1;
      } catch (NumberFormatException e) {
        return -1;
      }
    }

    @Override public sys.io.BufferedSource source() {
      return bodySource;
    }
  }
}
