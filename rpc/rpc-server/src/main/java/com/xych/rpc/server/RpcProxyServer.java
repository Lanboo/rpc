package com.xych.rpc.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcProxyServer {
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<String, Object> serviceMap = new HashMap<>();

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    public RpcProxyServer(int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        // 设置通道为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));
        this.selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void publisher() throws IOException {
        while(true) {
            // selector.select() 会阻塞
            // 阻塞n毫秒
            // 当异步注册事件时，selector.select()会一直阻塞
            int readyCount = selector.select(1000);
            if(readyCount == 0) {
                continue;
            }
            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
            while(keyIter.hasNext()) {
                SelectionKey key = keyIter.next();
                keyIter.remove();
                if(!key.isValid()) {
                    continue;
                }
                if(key.isAcceptable()) {
                    SocketChannel channel = serverSocketChannel.accept();
                    // 设置通道为非阻塞模式
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                }
                else if(key.isReadable()) {
                    executorService.execute(new ReadProcessorHandler(this.serviceMap, this.selector, key));
                    //                    new ReadProcessorHandler(this.serviceMap, this.selector, key).run();
                }
                else if(key.isWritable()) {
                    // executorService.execute(new WriteProcessorHandler(key));
                    new WriteProcessorHandler(key).run();
                }
            }
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
