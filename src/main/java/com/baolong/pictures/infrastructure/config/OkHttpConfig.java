package com.baolong.pictures.infrastructure.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp配置
 *
 * @author Silas Yan 2025-03-26:21:40
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "okhttp")
@Data
public class OkHttpConfig {
	private Integer connectTimeout = 30;
	private Integer readTimeout = 30;
	private Integer writeTimeout = 30;
	private Integer maxIdleConnections = 200;
	private Long keepAliveDuration = 300L;

	@Bean
	public OkHttpClient okHttpClient() {
		return new OkHttpClient.Builder()
				.sslSocketFactory(sslSocketFactory(), x509TrustManager())
				.retryOnConnectionFailure(false)
				.connectionPool(pool())
				.connectTimeout(connectTimeout, TimeUnit.SECONDS)
				.readTimeout(readTimeout, TimeUnit.SECONDS)
				.writeTimeout(writeTimeout, TimeUnit.SECONDS)
				.hostnameVerifier((hostname, session) -> true)
				.build();
	}

	@Bean
	public X509TrustManager x509TrustManager() {
		return new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		};
	}

	@Bean
	public SSLSocketFactory sslSocketFactory() {
		try {
			// 信任任何链接
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[]{x509TrustManager()}, new SecureRandom());
			return sslContext.getSocketFactory();
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Bean
	public ConnectionPool pool() {
		return new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.SECONDS);
	}
}
