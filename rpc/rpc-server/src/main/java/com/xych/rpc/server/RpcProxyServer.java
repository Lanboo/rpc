package com.xych.rpc.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class RpcProxyServer {
    private final Map<String, Object> serviceMap = new HashMap<>();
    private int port;

    // Boos线程数：CPU核心数*2
    private final int BIZ_GROUP_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    // work线程数
    private final int BIZ_THREAD_SIZE = 100;
    private final EventLoopGroup BOSS_GROUP = new NioEventLoopGroup(BIZ_GROUP_SIZE);
    private final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup(BIZ_THREAD_SIZE);

    public RpcProxyServer(int port) throws IOException {
        this.port = port;
    }

    public void publisher() throws Exception {

    }

    public void put(Object service) {
        if(!Objects.isNull(service)) {
            Class<?>[] interfaceClasses = service.getClass().getInterfaces();
            if(interfaceClasses != null && interfaceClasses.length > 0) {
                for(Class<?> interfaceClass : interfaceClasses) {
                    this.serviceMap.put(interfaceClass.getName(), service);
                }
            }
        }
    }
}
