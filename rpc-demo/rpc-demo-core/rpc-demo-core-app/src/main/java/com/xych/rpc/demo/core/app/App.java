package com.xych.rpc.demo.core.app;

import com.xych.rpc.demo.core.app.api.impl.HelloApiImpl;
import com.xych.rpc.demo.core.app.api.impl.UserApiImpl;
import com.xych.rpc.server.RpcProxyServer;

public class App {
    public static void main(String[] args) {
        RpcProxyServer proxyServer = new RpcProxyServer();
        proxyServer.put(new HelloApiImpl());
        proxyServer.put(new UserApiImpl());
        proxyServer.publisher(8080);
    }
}
