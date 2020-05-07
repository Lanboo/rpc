package com.xych.rpc.demo.busi;

import com.xych.rpc.client.RpcProxyClient;
import com.xych.rpc.demo.core.api.api.HelloApi;
import com.xych.rpc.demo.core.api.api.UserApi;
import com.xych.rpc.demo.core.api.request.UserRequest;

public class App {
    private static String host;
    private static int port;
    private static RpcProxyClient rpcProxyClient;
    static {
        host = "localhost";
        port = 8080;
        rpcProxyClient = new RpcProxyClient();
    }

    public static void main(String[] args) {
        UserRequest userRequest = UserRequest.builder().name("XYCH").age(18).sex("M").build();
        UserApi userApi = rpcProxyClient.clienProxy(UserApi.class, host, port);
        userApi.save(userRequest);
        
        HelloApi helloApi = rpcProxyClient.clienProxy(HelloApi.class, host, port);
        helloApi.sayHello();
        helloApi.say("abcdefg");
    }
}
