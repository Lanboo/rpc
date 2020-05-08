package com.xych.rpc.demo.core.app;

import com.xych.rpc.demo.core.app.api.impl.HelloApiImpl;
import com.xych.rpc.demo.core.app.api.impl.UserApiImpl;
import com.xych.rpc.server.RpcProxyServer;

public class App {
    public static void main(String[] args) throws Exception {
        RpcProxyServer proxyServer = new RpcProxyServer(8080);
        proxyServer.put(new HelloApiImpl());
        proxyServer.put(new UserApiImpl());
        proxyServer.publisher();
    }
}
