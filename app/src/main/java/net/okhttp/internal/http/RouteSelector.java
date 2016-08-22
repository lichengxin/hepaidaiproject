/*
 * Copyright (C) 2012 Square, Inc.
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

/**
 * Selects routes to connect to an origin server. Each connection requires a
 * choice of proxy server, IP address, and TLS mode. Connections may also be
 * recycled.
 */
public final class RouteSelector {
  private final net.okhttp.Address address;
  private final net.okhttp.HttpUrl url;
  private final net.okhttp.internal.Network network;
  private final net.okhttp.OkHttpClient client;
  private final net.okhttp.internal.RouteDatabase routeDatabase;

  /* The most recently attempted route. */
  private java.net.Proxy lastProxy;
  private java.net.InetSocketAddress lastInetSocketAddress;

  /* State for negotiating the next proxy to use. */
  private java.util.List<java.net.Proxy> proxies = java.util.Collections.emptyList();
  private int nextProxyIndex;

  /* State for negotiating the next socket address to use. */
  private java.util.List<java.net.InetSocketAddress> inetSocketAddresses = java.util.Collections.emptyList();
  private int nextInetSocketAddressIndex;

  /* State for negotiating failed routes */
  private final java.util.List<net.okhttp.Route> postponedRoutes = new java.util.ArrayList<>();

  private RouteSelector(net.okhttp.Address address, 
		  net.okhttp.HttpUrl url, 
		  net.okhttp.OkHttpClient client) {
    this.address = address;
    this.url = url;
    this.client = client;
    this.routeDatabase = net.okhttp.internal.Internal.instance.routeDatabase(client);
    this.network = net.okhttp.internal.Internal.instance.network(client);

    resetNextProxy(url, address.getProxy());
  }

  public static RouteSelector get(net.okhttp.Address address, 
		  net.okhttp.Request request, 
		  net.okhttp.OkHttpClient client)
      throws IOException {
    return new RouteSelector(address, request.httpUrl(), client);
  }

  /**
   * Returns true if there's another route to attempt. Every address has at
   * least one route.
   */
  public boolean hasNext() {
    return hasNextInetSocketAddress()
        || hasNextProxy()
        || hasNextPostponed();
  }

  public net.okhttp.Route next() throws IOException {
    // Compute the next route to attempt.
    if (!hasNextInetSocketAddress()) {
      if (!hasNextProxy()) {
        if (!hasNextPostponed()) {
          throw new java.util.NoSuchElementException();
        }
        return nextPostponed();
      }
      lastProxy = nextProxy();
    }
    lastInetSocketAddress = nextInetSocketAddress();

    net.okhttp.Route route = new net.okhttp.Route(address, lastProxy, lastInetSocketAddress);
    if (routeDatabase.shouldPostpone(route)) {
      postponedRoutes.add(route);
      // We will only recurse in order to skip previously failed routes. They will be tried last.
      return next();
    }

    return route;
  }

  /**
   * Clients should invoke this method when they encounter a connectivity
   * failure on a connection returned by this route selector.
   */
  public void connectFailed(net.okhttp.Route failedRoute, IOException failure) {
    if (failedRoute.getProxy().type() != java.net.Proxy.Type.DIRECT && address.getProxySelector() != null) {
      // Tell the proxy selector when we fail to connect on a fresh connection.
      address.getProxySelector().connectFailed(
          url.uri(), failedRoute.getProxy().address(), failure);
    }

    routeDatabase.failed(failedRoute);
  }

  /** Prepares the proxy servers to try. */
  private void resetNextProxy(net.okhttp.HttpUrl url, java.net.Proxy proxy) {
    if (proxy != null) {
      // If the user specifies a proxy, try that and only that.
      proxies = java.util.Collections.singletonList(proxy);
    } else {
      // Try each of the ProxySelector choices until one connection succeeds. If none succeed
      // then we'll try a direct connection below.
      proxies = new java.util.ArrayList<>();
      java.util.List<java.net.Proxy> selectedProxies = client.getProxySelector().select(url.uri());
      if (selectedProxies != null) proxies.addAll(selectedProxies);
      // Finally try a direct connection. We only try it once!
      proxies.removeAll(java.util.Collections.singleton(java.net.Proxy.NO_PROXY));
      proxies.add(java.net.Proxy.NO_PROXY);
    }
    nextProxyIndex = 0;
  }

  /** Returns true if there's another proxy to try. */
  private boolean hasNextProxy() {
    return nextProxyIndex < proxies.size();
  }

  /** Returns the next proxy to try. May be PROXY.NO_PROXY but never null. */
  private java.net.Proxy nextProxy() throws IOException {
    if (!hasNextProxy()) {
      throw new java.net.SocketException("No route to " + address.getUriHost()
          + "; exhausted proxy configurations: " + proxies);
    }
    java.net.Proxy result = proxies.get(nextProxyIndex++);
    resetNextInetSocketAddress(result);
    return result;
  }

  /** Prepares the socket addresses to attempt for the current proxy or host. */
  private void resetNextInetSocketAddress(java.net.Proxy proxy) throws IOException {
    // Clear the addresses. Necessary if getAllByName() below throws!
    inetSocketAddresses = new java.util.ArrayList<>();

    String socketHost;
    int socketPort;
    if (proxy.type() == java.net.Proxy.Type.DIRECT || 
    	proxy.type() == java.net.Proxy.Type.SOCKS) {
      socketHost = address.getUriHost();
      socketPort = address.getUriPort();
    } else {
    	java.net.SocketAddress proxyAddress = proxy.address();
      if (!(proxyAddress instanceof java.net.InetSocketAddress)) {
        throw new IllegalArgumentException(
            "Proxy.address() is not an " + "InetSocketAddress: " + proxyAddress.getClass());
      }
      java.net.InetSocketAddress proxySocketAddress = (java.net.InetSocketAddress) proxyAddress;
      socketHost = getHostString(proxySocketAddress);
      socketPort = proxySocketAddress.getPort();
    }

    if (socketPort < 1 || socketPort > 65535) {
      throw new java.net.SocketException("No route to " + socketHost + ":" + socketPort
          + "; port is out of range");
    }

    // Try each address for best behavior in mixed IPv4/IPv6 environments.
    for (java.net.InetAddress inetAddress : network.resolveInetAddresses(socketHost)) {
      inetSocketAddresses.add(new java.net.InetSocketAddress(inetAddress, socketPort));
    }

    nextInetSocketAddressIndex = 0;
  }

  /**
   * Obtain a "host" from an {@link InetSocketAddress}. This returns a string containing either an
   * actual host name or a numeric IP address.
   */
  // Visible for testing
  static String getHostString(java.net.InetSocketAddress socketAddress) {
	  java.net.InetAddress address = socketAddress.getAddress();
    if (address == null) {
      // The InetSocketAddress was specified with a string (either a numeric IP or a host name). If
      // it is a name, all IPs for that name should be tried. If it is an IP address, only that IP
      // address should be tried.
      return socketAddress.getHostName();
    }
    // The InetSocketAddress has a specific address: we should only try that address. Therefore we
    // return the address and ignore any host name that may be available.
    return address.getHostAddress();
  }

  /** Returns true if there's another socket address to try. */
  private boolean hasNextInetSocketAddress() {
    return nextInetSocketAddressIndex < inetSocketAddresses.size();
  }

  /** Returns the next socket address to try. */
  private java.net.InetSocketAddress nextInetSocketAddress() throws IOException {
    if (!hasNextInetSocketAddress()) {
      throw new java.net.SocketException("No route to " + address.getUriHost()
          + "; exhausted inet socket addresses: " + inetSocketAddresses);
    }
    return inetSocketAddresses.get(nextInetSocketAddressIndex++);
  }

  /** Returns true if there is another postponed route to try. */
  private boolean hasNextPostponed() {
    return !postponedRoutes.isEmpty();
  }

  /** Returns the next postponed route to try. */
  private net.okhttp.Route nextPostponed() {
    return postponedRoutes.remove(0);
  }
}
