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

package net.okhttp.internal.http;

import java.io.IOException;

import static net.okhttp.internal.Util.checkOffsetAndCount;

/**
 * An HTTP request body that's completely buffered in memory. This allows
 * the post body to be transparently re-sent if the HTTP request must be
 * sent multiple times.
 */
public final class RetryableSink implements sys.io.Sink {
  private boolean closed;
  private final int limit;
  private final sys.io.Buffer content = new sys.io.Buffer();

  public RetryableSink(int limit) {
    this.limit = limit;
  }

  public RetryableSink() {
    this(-1);
  }

  @Override public void close() throws IOException {
    if (closed) return;
    closed = true;
    if (content.size() < limit) {
      throw new java.net.ProtocolException(
          "content-length promised " + limit + " bytes, but received " + content.size());
    }
  }

  @Override public void write(sys.io.Buffer source, long byteCount) throws IOException {
    if (closed) throw new IllegalStateException("closed");
    checkOffsetAndCount(source.size(), 0, byteCount);
    if (limit != -1 && content.size() > limit - byteCount) {
      throw new java.net.ProtocolException("exceeded content-length limit of " + limit + " bytes");
    }
    content.write(source, byteCount);
  }

  @Override public void flush() throws IOException {
  }

  @Override public sys.io.Timeout timeout() {
    return sys.io.Timeout.NONE;
  }

  public long contentLength() throws IOException {
    return content.size();
  }

  public void writeToSocket(sys.io.Sink socketOut) throws IOException {
    // Copy the content; otherwise we won't have data to retry.
	  sys.io.Buffer buffer = new sys.io.Buffer();
    content.copyTo(buffer, 0, content.size());
    socketOut.write(buffer, buffer.size());
  }
}
