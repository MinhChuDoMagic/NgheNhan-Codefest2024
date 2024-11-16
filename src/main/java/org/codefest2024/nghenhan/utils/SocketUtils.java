package org.codefest2024.nghenhan.utils;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.*;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class SocketUtils {
    private static final int TIMEOUT_IN_MINUTES = 1;

    public static Socket init(String url, boolean useFsoftProxy) {
        OkHttpClient okHttpClient = getHttpClientBuilder(useFsoftProxy);
        IO.setDefaultOkHttpCallFactory(okHttpClient);
        IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
        try {
            return IO.socket(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static OkHttpClient getHttpClientBuilder(boolean useFsoftProxy) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().
                connectTimeout(TIMEOUT_IN_MINUTES, TimeUnit.MINUTES).
                writeTimeout(TIMEOUT_IN_MINUTES, TimeUnit.MINUTES).
                readTimeout(TIMEOUT_IN_MINUTES, TimeUnit.MINUTES);

        if (useFsoftProxy) {
            String host = "hl-proxyb";
            int port = 8080;
            String username = "quyetnx";
            String password = "`tamnga2";
            clientBuilder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port)));
            clientBuilder.proxyAuthenticator(new ProxyAuthenticator(username, password));
        }
        return clientBuilder.build();
    }

    private static class ProxyAuthenticator implements Authenticator {
        private String username, password;

        ProxyAuthenticator(String username, String password) {
            this.password = password;
            this.username = username;
        }

        @Override
        public Request authenticate(Route route, Response response) {
            String credential = Credentials.basic(username, password);
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();

        }
    }
}
