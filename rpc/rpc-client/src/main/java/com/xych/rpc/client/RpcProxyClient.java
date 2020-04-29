package com.xych.rpc.client;

import java.lang.reflect.Proxy;

public class RpcProxyClient {
    @SuppressWarnings("unchecked")
    public <T> T clienProxy(final Class<T> interfacesClass, String host, int port) {
        return (T) Proxy.newProxyInstance(interfacesClass.getClassLoader(), new Class<?>[] { interfacesClass }, new RemoteInvocationHandler(host, port));
    }
}
