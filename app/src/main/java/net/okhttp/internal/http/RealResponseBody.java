/*
 * Copyright (C) 2014 Square, Inc.
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

public final class RealResponseBody extends net.okhttp.ResponseBody {
  private final net.okhttp.Headers headers;
  private final sys.io.BufferedSource source;

  public RealResponseBody(net.okhttp.Headers headers, sys.io.BufferedSource source) {
    this.headers = headers;
    this.source = source;
  }

  @Override public net.okhttp.MediaType contentType() {
    String contentType = headers.get("Content-Type");
    return contentType != null ? net.okhttp.MediaType.parse(contentType) : null;
  }

  @Override public long contentLength() {
    return OkHeaders.contentLength(headers);
  }

  @Override public sys.io.BufferedSource source() {
    return source;
  }
}
