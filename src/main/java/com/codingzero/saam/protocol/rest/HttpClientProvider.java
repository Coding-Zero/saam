package com.codingzero.saam.protocol.rest;

import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class HttpClientProvider {

    private CloseableHttpClient httpClient;

    public HttpClientProvider() {
        this.httpClient = getHttpClient();
    }

    private CloseableHttpClient getHttpClient() {
        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true)
                    .useProtocol("TLS")
                    .build();
            SSLConnectionSocketFactory connectionFactory =
                    new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
            CookieStore cookieStore = new BasicCookieStore();
            HttpClientContext context = HttpClientContext.create();
            context.setCookieStore(cookieStore);

            RequestConfig requestConfig = RequestConfig.custom()
                    .setCircularRedirectsAllowed(true)
                    .setConnectTimeout(3000)
                    .setSocketTimeout(3000)
                    .setConnectionRequestTimeout(3000)
                    .build();
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(connectionFactory)
                    .setDefaultCookieStore(cookieStore)
                    .setDefaultRequestConfig(requestConfig)
                    .build();
            return httpClient;
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public CloseableHttpClient get() {
        return httpClient;
    }
}
