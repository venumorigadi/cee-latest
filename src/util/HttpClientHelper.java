package util;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

public class HttpClientHelper {

	public HttpClient getClient() throws Exception {
		SSLContext sslContext = SSLContext.getInstance("SSL");

		sslContext.init(null, new TrustManager[] { new X509TrustManager() {

			public X509Certificate[] getAcceptedIssuers() {
				System.out.println("getAcceptedIssuers =============");
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
				System.out.println("checkClientTrusted =============");
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
				System.out.println("checkServerTrusted =============");
			}
		} }, new SecureRandom());

		SSLSocketFactory sf = new SSLSocketFactory(sslContext);
		Scheme httpsScheme = new Scheme("https", 443, sf);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(httpsScheme);

		ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
		return new DefaultHttpClient(cm);
	}
}