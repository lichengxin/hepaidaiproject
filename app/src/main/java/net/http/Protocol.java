package net.http;

import java.io.IOException;

public class Protocol {

	public static enum HttpMethod {
		GET("GET"), POST("POST"), PUT("PUT"), HEAD("HEAD"), MOVE("MOVE"), COPY(
				"COPY"), DELETE("DELETE"), OPTIONS("OPTIONS"), TRACE("TRACE"), CONNECT(
				"CONNECT");

		private final String value;

		HttpMethod(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}
	}


	public static boolean invalidatesCache(String method) {
		return method.equals("POST") || method.equals("PATCH")
				|| method.equals("PUT") || method.equals("DELETE")
				|| method.equals("MOVE"); // WebDAV
	}

	public static boolean requiresRequestBody(String method) {
		return method.equals("POST") || method.equals("PUT")
				|| method.equals("PATCH") || method.equals("PROPPATCH") // WebDAV
				|| method.equals("REPORT"); // CalDAV/CardDAV (defined in WebDAV
											// Versioning)
	}

	public static boolean permitsRequestBody(String method) {
		return requiresRequestBody(method) || method.equals("DELETE") // Permitted
																		// as
																		// spec
																		// is
																		// ambiguous.
				|| method.equals("PROPFIND") // (WebDAV) without body: request
												// <allprop/>
				|| method.equals("MKCOL") // (WebDAV) may contain a body, but
											// behaviour is unspecified
				|| method.equals("LOCK"); // (WebDAV) body: create lock, without
											// body: refresh lock
	}
	
	/**
	 * Protocols that OkHttp implements for <a
	 * href="http://tools.ietf.org/html/draft-ietf-tls-applayerprotoneg"
	 * >ALPN</a> selection.
	 * 
	 * <h3>Protocol vs Scheme</h3> Despite its name,
	 * {@link java.net.URL#getProtocol()} returns the
	 * {@linkplain java.net.URI#getScheme() scheme} (http, https, etc.) of the
	 * URL, not the protocol (http/1.1, spdy/3.1, etc.). OkHttp uses the word
	 * <i>protocol</i> to identify how HTTP messages are framed.
	 */
	public static enum ProtocolVersion {
		/**
		 * An obsolete plaintext framing that does not use persistent sockets by
		 * default.
		 */
		HTTP_1_0("http/1.0"),

		/**
		 * A plaintext framing that includes persistent connections.
		 * 
		 * <p>
		 * This version of OkHttp implements <a
		 * href="http://www.ietf.org/rfc/rfc2616.txt">RFC 2616</a>, and tracks
		 * revisions to that spec.
		 */
		HTTP_1_1("http/1.1"),

		/**
		 * Chromium's binary-framed protocol that includes header compression,
		 * multiplexing multiple requests on the same socket, and server-push.
		 * HTTP/1.1 semantics are layered on SPDY/3.
		 * 
		 * <p>
		 * This version of OkHttp implements SPDY 3 <a href=
		 * "http://dev.chromium.org/spdy/spdy-protocol/spdy-protocol-draft3-1"
		 * >draft 3.1</a>. Future releases of OkHttp may use this identifier for
		 * a newer draft of the SPDY spec.
		 */
		SPDY_3("spdy/3.1"),

		/**
		 * The IETF's binary-framed protocol that includes header compression,
		 * multiplexing multiple requests on the same socket, and server-push.
		 * HTTP/1.1 semantics are layered on HTTP/2.
		 * 
		 * <p>
		 * HTTP/2 requires deployments of HTTP/2 that use TLS 1.2 support
		 * {@linkplain com.squareup.okhttp.CipherSuite#TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256}
		 * , present in Java 8+ and Android 5+. Servers that enforce this may
		 * send an exception message including the string
		 * {@code INADEQUATE_SECURITY}.
		 */
		HTTP_2("h2");

		private final String protocol;

		ProtocolVersion(String protocol) {
			this.protocol = protocol;
		}

		/**
		 * Returns the protocol identified by {@code protocol}.
		 * 
		 * @throws IOException
		 *             if {@code protocol} is unknown.
		 */
		public static ProtocolVersion get(String protocol) throws IOException {
			// Unroll the loop over values() to save an allocation.
			if (protocol.equals(HTTP_1_0.protocol))
				return HTTP_1_0;
			if (protocol.equals(HTTP_1_1.protocol))
				return HTTP_1_1;
			if (protocol.equals(HTTP_2.protocol))
				return HTTP_2;
			if (protocol.equals(SPDY_3.protocol))
				return SPDY_3;
			throw new IOException("Unexpected protocol: " + protocol);
		}

		/**
		 * Returns the string used to identify this protocol for ALPN, like
		 * "http/1.1", "spdy/3.1" or "h2".
		 */
		@Override
		public String toString() {
			return protocol;
		}
	}
}
