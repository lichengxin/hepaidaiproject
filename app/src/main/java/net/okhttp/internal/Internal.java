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
package net.okhttp.internal;



import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;

import sys.io.BufferedSink;
import sys.io.BufferedSource;

import net.okhttp.Call;
import net.okhttp.Callback;
import net.okhttp.Connection;
import net.okhttp.ConnectionPool;
import net.okhttp.ConnectionSpec;
import net.okhttp.Headers;
import net.okhttp.HttpUrl;
import net.okhttp.OkHttpClient;
import net.okhttp.Request;
import net.okhttp.internal.http.HttpEngine;
import net.okhttp.internal.http.RouteException;
import net.okhttp.internal.http.Transport;

/**
 * Escalate internal APIs in {@code com.squareup.okhttp} so they can be used
 * from OkHttp's implementation packages. The only implementation of this
 * interface is in {@link net.okhttp.OkHttpClient}.
 */
public abstract class Internal {
  public static final Logger logger = Logger.getLogger(OkHttpClient.class.getName());

  public static void initializeInstanceForTests() {
    // Needed in tests to ensure that the instance is actually pointing to something.
    new OkHttpClient();
  }

  public static Internal instance;

  public abstract Transport newTransport(Connection connection, HttpEngine httpEngine)
      throws IOException;

  public abstract boolean clearOwner(Connection connection);

  public abstract void closeIfOwnedBy(Connection connection, Object owner) throws IOException;

  public abstract int recycleCount(Connection connection);

  public abstract void setProtocol(Connection connection, net.http.Protocol.ProtocolVersion protocol);

  public abstract void setOwner(Connection connection, HttpEngine httpEngine);

  public abstract boolean isReadable(Connection pooled);

  public abstract void addLenient(Headers.Builder builder, String line);

  public abstract void addLenient(Headers.Builder builder, String name, String value);

  public abstract void setCache(OkHttpClient client, InternalCache internalCache);

  public abstract InternalCache internalCache(OkHttpClient client);

  public abstract void recycle(ConnectionPool pool, Connection connection);

  public abstract RouteDatabase routeDatabase(OkHttpClient client);

  public abstract Network network(OkHttpClient client);

  public abstract void setNetwork(OkHttpClient client, Network network);

  public abstract void connectAndSetOwner(OkHttpClient client, Connection connection,
      HttpEngine owner, Request request) throws RouteException;

  public abstract void apply(ConnectionSpec tlsConfiguration, SSLSocket sslSocket,
      boolean isFallback);

  public abstract HttpUrl getHttpUrlChecked(String url)
      throws MalformedURLException, UnknownHostException;

  // TODO delete the following when web sockets move into the main package.
  public abstract void callEnqueue(Call call, Callback responseCallback, boolean forWebSocket);
  public abstract void callEngineReleaseConnection(Call call) throws IOException;
  public abstract Connection callEngineGetConnection(Call call);
  public abstract BufferedSource connectionRawSource(Connection connection);
  public abstract BufferedSink connectionRawSink(Connection connection);
  public abstract void connectionSetOwner(Connection connection, Object owner);
}
