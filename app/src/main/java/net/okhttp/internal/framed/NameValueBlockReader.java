/*
 * Copyright (C) 2013 Square, Inc.
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
package net.okhttp.internal.framed;

/**
 * Reads a SPDY/3 Name/Value header block. This class is made complicated by the
 * requirement that we're strict with which bytes we put in the compressed bytes
 * buffer. We need to put all compressed bytes into that buffer -- but no other
 * bytes.
 */
class NameValueBlockReader {
  /** This source transforms compressed bytes into uncompressed bytes. */
  private final sys.io.InflaterSource inflaterSource;

  /**
   * How many compressed bytes must be read into inflaterSource before
   * {@link #readNameValueBlock} returns.
   */
  private int compressedLimit;

  /** This source holds inflated bytes. */
  private final sys.io.BufferedSource source;

  public NameValueBlockReader(sys.io.BufferedSource source) {
    // Limit the inflater input stream to only those bytes in the Name/Value
    // block. We cut the inflater off at its source because we can't predict the
    // ratio of compressed bytes to uncompressed bytes.
	  sys.io.Source throttleSource = new sys.io.ForwardingSource(source) {
      @Override public long read(sys.io.Buffer sink, long byteCount) throws java.io.IOException {
        if (compressedLimit == 0) return -1; // Out of data for the current block.
        long read = super.read(sink, Math.min(byteCount, compressedLimit));
        if (read == -1) return -1;
        compressedLimit -= read;
        return read;
      }
    };

    // Subclass inflater to install a dictionary when it's needed.
    java.util.zip.Inflater inflater = new java.util.zip.Inflater() {
      @Override public int inflate(byte[] buffer, int offset, int count)
          throws java.util.zip.DataFormatException {
        int result = super.inflate(buffer, offset, count);
        if (result == 0 && needsDictionary()) {
          setDictionary(Spdy3.DICTIONARY);
          result = super.inflate(buffer, offset, count);
        }
        return result;
      }
    };

    this.inflaterSource = new sys.io.InflaterSource(throttleSource, inflater);
    this.source = sys.io.Okio.buffer(inflaterSource);
  }

  public java.util.List<Header> readNameValueBlock(int length) throws java.io.IOException {
    this.compressedLimit += length;

    int numberOfPairs = source.readInt();
    if (numberOfPairs < 0) throw new java.io.IOException("numberOfPairs < 0: " + numberOfPairs);
    if (numberOfPairs > 1024) throw new java.io.IOException("numberOfPairs > 1024: " + numberOfPairs);

    java.util.List<Header> entries = new java.util.ArrayList<>(numberOfPairs);
    for (int i = 0; i < numberOfPairs; i++) {
    	sys.io.ByteString name = readByteString().toAsciiLowercase();
    	sys.io.ByteString values = readByteString();
      if (name.size() == 0) throw new java.io.IOException("name.size == 0");
      entries.add(new Header(name, values));
    }

    doneReading();
    return entries;
  }

  private sys.io.ByteString readByteString() throws java.io.IOException {
    int length = source.readInt();
    return source.readByteString(length);
  }

  private void doneReading() throws java.io.IOException {
    // Move any outstanding unread bytes into the inflater. One side-effect of
    // deflate compression is that sometimes there are bytes remaining in the
    // stream after we've consumed all of the content.
    if (compressedLimit > 0) {
      inflaterSource.refill();
      if (compressedLimit != 0) throw new java.io.IOException("compressedLimit > 0: " + compressedLimit);
    }
  }

  public void close() throws java.io.IOException {
    source.close();
  }
}
