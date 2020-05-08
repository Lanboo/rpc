package com.xych.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.xych.rpc.common.rpc.RpcInvocation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteInvocationHandler implements InvocationHandler {
    private String host;
    private int port;

    public RemoteInvocationHandler(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("Remote Proxy Start");
        RpcInvocation invocation = new RpcInvocation();
        invocation.setClassName(method.getDeclaringClass().getName());
        invocation.setMethodName(method.getName());
        if(args != null && args.length > 0) {
            Class<?>[] argTypes = new Class<?>[args.length];
            for(int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
            }
            invocation.setParameterTypes(argTypes);
            invocation.setArguments(args);
        }
        RpcNetTransport netTransport = new RpcNetTransport(host, port);
        Object obj = netTransport.send(invocation);
        log.info("Remote Proxy End:" + obj);
        return obj;
    }

}
