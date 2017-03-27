package com.github.ssullivan;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by catal on 3/23/2017.
 */
public interface IHttpClientKey {
    /**
     * The 'key' used as the name for a http cleint
     * <p>
     *     The word 'name' is used instead of 'key' so that Enums can implement this interface and it work natively.
     * </p>
     * @return String
     */
    String name();

    public static class Factory {
        private Factory() {}
        private static ConcurrentHashMap<String, IHttpClientKey> intern = new ConcurrentHashMap<>();

        public static IHttpClientKey asKey(String name) {
            IHttpClientKey k = intern.get(name);
            if (k == null) {
                k = new HttpClientKeyDefault(name);
                intern.putIfAbsent(name, k);
            }
            return k;
        }


        private static class HttpClientKeyDefault implements IHttpClientKey {
            private String _name;

            public HttpClientKeyDefault(String name) {
                _name = name;
            }

            @Override
            public String name() {
                return _name;
            }
        }
    }

}
