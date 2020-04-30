package com.xych.rpc.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcProxyServer {
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<String, Object> serviceMap = new HashMap<>();

    public void publisher(int port) {
        try(ServerSocket serverSocket = new ServerSocket(port);) {
            while(true) {
                Socket socket = serverSocket.accept();
                executorService.execute(new ProcessorHandler(serviceMap, socket));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
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
