package com.miya10kei;

import com.miya10kei.interfaces.Routes;
import reactor.netty.http.server.HttpServer;

public class Application {

    public static void main(String[] args) {
        var server = HttpServer.create()
                .host("localhost")
                .port(8080)
                .compress(true)
                .route(new Routes())
                .bindNow();
        server.onDispose().block();
    }
}
