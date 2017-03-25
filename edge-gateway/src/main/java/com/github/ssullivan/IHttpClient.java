package com.github.ssullivan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.github.ssullivan.stats.monitoring.HttpClientConfiguration;
import com.netflix.hystrix.HystrixThreadPool;
import com.netflix.hystrix.HystrixTimerThreadPoolProperties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by catal on 3/23/2017.
 */
public interface IHttpClient {
    Client getClient();

    ObjectMapper getObjectMapper();

    SSLContext getSSLContext();

    /* package */ static class Factory {
        /* package */ final static ConcurrentHashMap<String, IHttpClient> clients = new ConcurrentHashMap<>();

        public static IHttpClient getInstance(IHttpClientKey httpClientKey, HttpClientConfiguration configuration) {
            String key = httpClientKey.name();

            IHttpClient previouslyCached = clients.get(key);
            if (previouslyCached != null)
                return previouslyCached;

            synchronized (IHttpClient.class) {
                if (!clients.containsKey(key)) {
                    clients.put(key, new HttpClientDefault(httpClientKey, configuration));
                }
            }
            return clients.get(key);
        }

        /* package */ static synchronized void shutdown() {
            for (IHttpClient client : clients.values())
                client.getClient().close();
            clients.clear();
        }

        private static SSLContext buildSSLContext() {
            return null;
        }


        private static class HttpClientDefault implements IHttpClient {
            private final ObjectMapper _objectMapper;
            private Client _client;

            public HttpClientDefault(IHttpClientKey httpClientKey, HttpClientConfiguration configuration) {
                _objectMapper = new ObjectMapper();
                _client = ClientBuilder
                        .newBuilder()
                        .sslContext(buildSSLContext())
                        .register(new JacksonJaxbJsonProvider(_objectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS))
                        .build();
            }

            @Override
            public Client getClient() {
                return _client;
            }

            @Override
            public ObjectMapper getObjectMapper() {
                return _objectMapper;
            }

            @Override
            public SSLContext getSSLContext() {
                return _client.getSslContext();
            }
        }
    }

}
