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
package net.okhttp.internal.http;

/** Adapts {@link java.net.Authenticator} to {@link net.okhttp.Authenticator}. */
public final class AuthenticatorAdapter implements net.okhttp.Authenticator {
  /** Uses the global authenticator to get the password. */
  public static final net.okhttp.Authenticator INSTANCE = new AuthenticatorAdapter();

  @Override public net.okhttp.Request authenticate(java.net.Proxy proxy, net.okhttp.Response response) throws java.io.IOException {
	  java.util.List<net.okhttp.Challenge> challenges = response.challenges();
	  net.okhttp.Request request = response.request();
	  net.okhttp.HttpUrl url = request.httpUrl();
    for (int i = 0, size = challenges.size(); i < size; i++) {
    	net.okhttp.Challenge challenge = challenges.get(i);
      if (!"Basic".equalsIgnoreCase(challenge.getScheme())) continue;

      java.net.PasswordAuthentication auth = java.net.Authenticator.requestPasswordAuthentication(
          url.host(), getConnectToInetAddress(proxy, url), url.port(), url.scheme(),
          challenge.getRealm(), challenge.getScheme(), url.url(), java.net.Authenticator.RequestorType.SERVER);
      if (auth == null) continue;

      String credential = net.okhttp.Credentials.basic(auth.getUserName(), new String(auth.getPassword()));
      return request.newBuilder()
          .header("Authorization", credential)
          .build();
    }
    return null;

  }

  @Override public net.okhttp.Request authenticateProxy(java.net.Proxy proxy, net.okhttp.Response response) throws java.io.IOException {
	  java.util.List<net.okhttp.Challenge> challenges = response.challenges();
	  net.okhttp.Request request = response.request();
	  net.okhttp.HttpUrl url = request.httpUrl();
    for (int i = 0, size = challenges.size(); i < size; i++) {
    	net.okhttp.Challenge challenge = challenges.get(i);
      if (!"Basic".equalsIgnoreCase(challenge.getScheme())) continue;

      java.net.InetSocketAddress proxyAddress = (java.net.InetSocketAddress) proxy.address();
      java.net.PasswordAuthentication auth = java.net.Authenticator.requestPasswordAuthentication(
          proxyAddress.getHostName(), getConnectToInetAddress(proxy, url), proxyAddress.getPort(),
          url.scheme(), challenge.getRealm(), challenge.getScheme(), url.url(),
          java.net.Authenticator.RequestorType.PROXY);
      if (auth == null) continue;

      String credential = net.okhttp.Credentials.basic(auth.getUserName(), new String(auth.getPassword()));
      return request.newBuilder()
          .header("Proxy-Authorization", credential)
          .build();
    }
    return null;
  }

  private java.net.InetAddress getConnectToInetAddress(java.net.Proxy proxy, net.okhttp.HttpUrl url) throws java.io.IOException {
    return (proxy != null && proxy.type() != java.net.Proxy.Type.DIRECT)
        ? ((java.net.InetSocketAddress) proxy.address()).getAddress()
        : java.net.InetAddress.getByName(url.host());
  }
}
